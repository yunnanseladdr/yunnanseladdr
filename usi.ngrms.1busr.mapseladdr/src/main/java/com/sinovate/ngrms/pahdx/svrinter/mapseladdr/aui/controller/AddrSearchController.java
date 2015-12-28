package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.controller;


import com.sinovate.ngrms.comm.cptsvr.codemix.sinter.InterBusiCtxPaser;
import com.sinovate.ngrms.pahdx.svrinter.addrsearch.das.model.ResAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.StreetBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.SubAreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc.AblNearSearchSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc.AblSuggestAddrSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.commu.ResAbility;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.commu.ResAddrSchSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.ResAbilityBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc.AblUserAddrSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.SuggestiveAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl.GetProjInfoSvcImpl;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl.GetServiceTelSvcImpl;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.AdminDivisionSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.GetServiceTelSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.GetXqtInfoSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地址查询页面controller，相应地址查询的动作
 *
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
@Controller
@RequestMapping(value = "/")
public class AddrSearchController {

    private static final Logger logger = LoggerFactory.getLogger(AddrSearchController.class);

    //记录使用地址查询接口的次数
    public int userOipCount = 0;

    //记录使用预选址系统次数
    public int userSystemCount = 0;

    //记录icss使用预选址系统次数
    public int icssUseSystemCount = 0;

    @Autowired
    private AdminDivisionSvc adminDivisionSvc;

    @Autowired
    private AblSuggestAddrSvc ablSuggestAddrSvc;

    @Autowired
    private AblUserAddrSvc ablUserAddrSvc;

    @Autowired
    private AblNearSearchSvc ablNearSearchSvc;

    @Autowired
    private ResAbility resAbility;

    @Autowired
    private ResAddrSchSvc resAddrSchSvc;

    @Autowired
    @Qualifier("getXqtInfoSvcImpl")
    private GetXqtInfoSvc getXqtInfoSvc;

    @Autowired
    private GetProjInfoSvcImpl getProjInfoSvc;

    @Autowired
    private GetServiceTelSvcImpl getServiceTelSvc;
    
    private static final Map<String, String> areaEid2areaCode = new HashMap<String, String>(){{
        put("11", "550");
        put("2", "551");
        put("3", "552");
        put("4", "553");
        put("5", "554");
        put("6", "555");
        put("7", "556");
        put("8", "557");
        put("9", "558");
        put("10", "559");
        put("12", "561");
        put("13", "562");
        put("14", "563");
        put("15", "564");
        put("17", "566");
        put("18", "567");
    }};

    public AddrSearchController() {

    }

    public void finalize() throws Throwable {

    }

    /**
     * 根据区域编码、地址id（oldId）获取地址的接入能力，调用国朗注册在oip上的webService接口
     *
     * @param areaCode
     * @param addrId
     */
    @ResponseBody
    @RequestMapping(value = "/getResAbility", method = RequestMethod.POST)
    public List<ResAbilityBo> getResAbility(@RequestParam String areaCode, @RequestParam String addrId,
                                            @RequestParam(value = "siteIds") List<String> siteIds,
                                            @RequestParam(value = "siteNames") List<String> siteNames,
                                            @RequestParam String oipUrl) {
        List<ResAbilityBo> list = null;
        try {
            if (null != siteIds && siteIds.size() > 0 && null != siteNames && siteNames.size() > 0) {
                List<ResAbilityBo> subList = null;
                list = new ArrayList<ResAbilityBo>();
                for (int i = 0; i < siteIds.size(); i++) {
                    subList = new ArrayList<ResAbilityBo>();
                    subList = resAbility.getResAbility(areaCode, addrId, siteIds.get(i), siteNames.get(i), oipUrl);
                    if (null != subList && subList.size() > 0) {
                        list.addAll(subList);
                    }
                    //万号要求只展示标准地址下的一个局站的预判结果这里用break直接跳出，保证一个地址对应多个局站时只进行一次资源预判
                    break;
                }
            } else {
                //如果地址不存在局站信息是局站的名称和局站id传入空值
                list = resAbility.getResAbility(areaCode, addrId, "", "", oipUrl);
            }
            return list;
        } catch (Exception e) {
            logger.info("IOException");
        }
        return null;
    }

    /**
     * 取乡镇街道名称信息
     * 查询区县（areaCode, subAreaName) 下的街道或乡镇信息，返回名称列表
     *
     * @param areaCode
     * @param subAreaName
     */
    @ResponseBody
    @RequestMapping(value = "/getStreetNames", method = RequestMethod.POST)
    public List<StreetBo> getStreetNames(String areaCode, String subAreaName) {
        if (null == areaCode || areaCode.isEmpty()) {
            logger.warn("区域代码areaCode为空，无法处理！");
            return null;
        }
        if (null == subAreaName || subAreaName.isEmpty()) {
            logger.warn("子区域名称subAreaName为空，无法处理！");
            return null;
        }

        List<StreetBo> list = new ArrayList<StreetBo>();
        list = adminDivisionSvc.getStreetNames(areaCode, subAreaName);

        return list;

    }

    /**
     * 取区县信息接口
     * 查询地市(areaCode)下的区县名称信息，并将名称列表返回
     *
     * @param areaCode
     */
    @ResponseBody
    @RequestMapping(value = "/getSubAreas", method = RequestMethod.POST)
    public List<SubAreaBo> getSubAreas(String areaCode) {
        if (null == areaCode || areaCode.isEmpty()) {
            logger.warn("区域代码areaCode为空，无法处理！");
            return null;
        }

        List<SubAreaBo> list = new ArrayList<SubAreaBo>();
        list = adminDivisionSvc.getSubAreas(areaCode);

        return list;
    }

    /**
     * 取提示地址信息
     * 取街道(areaCode, subAreaName, streetName,pageNo,pageSize)下面的提示地址信息；
     * 实现步骤：
     * 1、验证areaCode是否为空，为空则直接返回
     * 2、调用AblSuggestAddrSvc中的getSuggAddr(areaCode, subAreaName, streetName,pageNo,
     * pageSize)方法获取提示地址信息
     *
     * @param areaCode    不可为空
     * @param subAreaName 不可为空
     * @param streetName  不可为空
     * @param pageNo      不可为空
     * @param pageSize    不可为空
     */
    @ResponseBody
    @RequestMapping(value = "/getSuggAddr", method = RequestMethod.POST)
    public List<SuggestiveAddrVo> getSuggAddr(String areaCode, String subAreaName, String streetName, int pageNo, int pageSize) {
        if (null == areaCode || areaCode.isEmpty()) {
            logger.warn("区域代码areaCode为空，无法处理！");
            return null;
        }
        if (null == subAreaName || subAreaName.isEmpty()) {
            logger.warn("子区域名称subAreaName为空，无法处理！");
            return null;
        }
        if (null == streetName || streetName.isEmpty()) {
            logger.warn("街道名称streetName为空，无法处理！");
            return null;
        }
        if (pageNo == 0 || pageSize == 0) {
            logger.warn("未传入pageSize，无法处理！");
            return null;
        }
        if (pageSize == 0 || pageNo == 0) {
            logger.warn("未传入pageNo，无法处理！");
        }


        return ablSuggestAddrSvc.getSuggAddr(areaCode, subAreaName, streetName, pageNo, pageSize);
    }

    /**
     * 取提示地址信息
     * 取街道(areaCode, subAreaName, streetName)下面的能匹配用户输入(input)信息的提示地址。
     * subAreaName, streetName 可以同时为空
     * streetName可以单独为空
     * 实现步骤：
     * 1、验证areaCode是否为空，为空则直接返回
     * 2、调用AblSuggestAddrSvc中的getSuggAddrWithInput(String,String,String,String,int,
     * int)方法获取提示地址信息
     *
     * @param areaCode    不可为空
     * @param subAreaName 可为空
     * @param streetName  可为空
     * @param input       不可为空
     * @param pageNo      不可为空
     * @param pageSize    不可为空
     */
    @ResponseBody
    @RequestMapping(value = "/getSuggAddrWithInput", method = RequestMethod.POST)
    public List<SuggestiveAddrVo> getSuggAddrWithInput(@RequestParam String areaCode, @RequestParam String subAreaName, @RequestParam String streetName,
                                                       @RequestParam String input, @RequestParam int pageNo, @RequestParam int pageSize) {
        if (null == areaCode || areaCode.isEmpty()) {
            logger.warn("区域代码areaCode为空，无法处理！");
            return null;
        }
        /*if ( (null == subAreaName || subAreaName.isEmpty())  &&  (streetName != null &&  !streetName.isEmpty()) ) {
            logger.warn("参数应满足条件：subAreaName, streetName可以同时为空，streetName可以单独为空");
			return null;
		}*/
        if (null == input || input.isEmpty()) {
            logger.warn("输入字符串input为空，无法处理！");
            return null;
        }
        if (pageNo == 0 || pageSize == 0) {
            logger.warn("未传入pageSize或pageNo，无法处理！");
            return null;
        }

        //2位阿拉伯数字与中文转换
        Boolean transFlag = false;
        String target = "";
        String nums = "1234567890";
        String chs = "一二三四五六七八九十";
        for (int iter = 0; iter < input.length(); ++iter) {
            if (iter != input.length() - 1) {
                char c1 = input.charAt(iter);
                char c2 = input.charAt(iter + 1);
                if (nums.indexOf(c1) >= 0 && c1 != '0' && nums.indexOf(c2) >= 0) {
                    transFlag = true;
                    if (c1 != '1') {
                        target += chs.charAt(nums.indexOf(c1));
                    }
                    target += '十';
                    if (c2 != '0') {
                        target += chs.charAt(nums.indexOf(c2));
                    }
                    ++iter;
                    continue;
                }
            }
            target += input.charAt(iter);
        }

        if (!transFlag) {
            return ablSuggestAddrSvc.getSuggAddrWithInput(areaCode, subAreaName, streetName, input, pageNo, pageSize);
        }
        List<SuggestiveAddrVo> original = ablSuggestAddrSvc.getSuggAddrWithInput(areaCode, subAreaName, streetName, input, pageNo, pageSize/2);
        List<SuggestiveAddrVo> trans = ablSuggestAddrSvc.getSuggAddrWithInput(areaCode, subAreaName, streetName, target, pageNo, pageSize/2);

        if (original == null) {
            return trans;
        }
        if (trans != null && !trans.isEmpty()) {
            for (int i = 0; i < trans.size(); ++i) {
                original.add(trans.get(i));
            }
        }
        return original;
    }

    /**
     * 取用户地址信息（全地址查询）
     * 根据用户输入取地址信息
     * 实现步骤：
     * 1、验证areaCode是否为空，如果为空则直接返回
     * 2、调用AblUserAddrSvc中的getUserAddrByInput(String,String,int,int)方法获取用户地址信息
     *
     * @param areaCode 不可为空
     * @param input    不可为空
     * @param pageNo   不可为空
     * @param pageSize 不可为空
     */
    @ResponseBody
    @RequestMapping(value = "/getUserAddrByInput", method = RequestMethod.POST)
    public List<UserAddrVo> getUserAddrByInput(String areaCode, String input, int pageNo, int pageSize) {
        if (null == areaCode || areaCode.isEmpty()) {
            logger.warn("区域代码areaCode为空，无法处理！");
            return null;
        }
        if (null == input || input.isEmpty()) {
            logger.warn("输入字符串input为空，无法处理！");
            return null;
        }
        if (pageNo == 0 || pageSize == 0) {
            logger.warn("未传入pageSize，无法处理！");
            return null;
        }
        if (pageSize == 0 || pageNo == 0) {
            logger.warn("未传入pageNo，无法处理！");
        }

        return ablUserAddrSvc.getUserAddrByInput(areaCode, input, pageNo, pageSize);
    }

    /**
     * 取用户地址信息（全地址查询）
     * 根据用户输入取地址信息
     * 实现步骤：
     * 1、验证areaCode是否为空，如果为空则直接返回
     * 2、调用AblUserAddrSvc中的getUserAddrBySuggest(String,String,String,String,int,
     * int)方法获取用户地址信息
     *
     * @param areaCode    不可为空
     * @param subAreaName 可为空
     * @param streetName  可为空
     * @param input       不可为空
     * @param pageNo      不可为空
     * @param pageSize    不可为空
     */
    @ResponseBody
    @RequestMapping(value = "/getUserAddrBySuggest", method = RequestMethod.POST)
    public List<UserAddrVo> getUserAddrBySuggest(String areaCode, String subAreaName,
                                                 String streetName, String input, int pageNo, int pageSize) {
        if (null == areaCode || areaCode.isEmpty()) {
            logger.warn("区域代码areaCode为空，无法处理！");
            return null;
        }
        if (null == input || input.isEmpty()) {
            logger.warn("输入字符串input为空，无法处理！");
            return null;
        }
        if (pageNo == 0 || pageSize == 0) {
            logger.warn("未传入pageSize，无法处理！");
            return null;
        }
        if (pageSize == 0 || pageNo == 0) {
            logger.warn("未传入pageNo，无法处理！");
        }

        return ablUserAddrSvc.getUserAddrBySuggest(areaCode, subAreaName, streetName, input, pageNo, pageSize);
    }

    /**
     * 调用地址查询接口，查询用户地址
     *
     * @param areaCode
     * @param subAreaName
     * @param streetName
     * @param input
     * @param oipUrl
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAddrBySearch", method = RequestMethod.POST)
    public List<UserAddrVo> getAddrBySearch(String areaCode, String subAreaName, String streetName, String
            input, String oipUrl, String sysCode, int pageNo, int pageSize) {
        if (null == areaCode || areaCode.isEmpty()) {
            logger.warn("区域代码areaCode为空，无法处理！");
            return null;
        }
        if (null == input || input.isEmpty()) {
            logger.warn("输入字符串input为空，无法处理！");
            return null;
        }
        String searchText = "";
        if (null == subAreaName || subAreaName.isEmpty()) {
            if (null == streetName || streetName.isEmpty()) {
                searchText = new StringBuilder().append("%").append(input).append("%").toString();
            } else {
                searchText = new StringBuilder().append("%").append(streetName).append("%").append(input).append("%").toString();
            }
        } else {
            searchText = new StringBuilder().append("%").append(subAreaName).append("%").append("%").append(streetName).append("%").append(input).append("%").toString();
        }
        List<UserAddrVo> userAddrVoList = null;
        try {
            userAddrVoList = resAddrSchSvc.getResAddr(areaCode, searchText, pageNo, pageSize, oipUrl, sysCode);
        } catch (IOException e) {
            logger.error("调用地址查询接口失败!");
        }
        return userAddrVoList;
    }

    @ResponseBody
    @RequestMapping(value = "/SetOipCount", method = RequestMethod.POST)
    public ResAddrBo SetOipCount() {
        userOipCount++;
        logger.info("调用地址查询接口的次数：" + userOipCount + ";" + "调用预选址系统次数：" + userSystemCount + ";其中icss调用系统次数: " + icssUseSystemCount);
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/setSelaCount", method = RequestMethod.POST)
    public ResAddrBo setSelaCount(String fromsystem) {
        userSystemCount++;
        if (fromsystem != null && fromsystem.equals("icss")) {
            ++icssUseSystemCount;
        }
        logger.info("调用地址查询接口的次数：" + userOipCount + ";" + "调用预选址系统次数：" + userSystemCount + ";其中icss调用系统次数: " + icssUseSystemCount);
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getMapAddrInfo", method = RequestMethod.POST)
    public Map<String, List<SuggestiveAddrVo>> getMapAddrInfo(Double lng, Double lat) {
        //logger.info("获取坐标数据 lng:" + lng + " lat: " + lat);
        if (lng < 0 || lat < 0) {
            logger.warn("传入经纬度参数错误，无法处理！");
            return null;
        }
        ArrayList<String> gridNameInfo = ablSuggestAddrSvc.getMapAddrInfo(lng, lat);
        if (gridNameInfo == null || gridNameInfo.isEmpty()) {
            return null;
        }
        String gridType = gridNameInfo.get(0);
        String subareaName = gridNameInfo.get(1);
        String gridName = gridNameInfo.get(2);
        String grid68Name = gridNameInfo.get(3);
        String gridPolygon = gridNameInfo.get(4);
        String gridId = gridNameInfo.get(5);
        String areaId = gridNameInfo.get(6);

        if (subareaName.indexOf('市') > 0) {
            subareaName = "";
        }
        logger.info("#######匹配的地址为: " + grid68Name);
        if (grid68Name == null || grid68Name.isEmpty())
            return null;
        Map<String, List<SuggestiveAddrVo>> rs = new HashMap<>();
        String gridInfo = gridType + ":" + gridName + ":" + grid68Name + ":" + gridPolygon + ":" + gridId + ":" + areaEid2areaCode.get(areaId);
        rs.put(gridInfo, ablSuggestAddrSvc.getSuggAddrWithInput(areaEid2areaCode.get(areaId), subareaName, "", grid68Name, 1, 5));
        logger.info("根据网格名称搜索匹配的建议地址成功");
        return rs;
    }

    @ResponseBody
    @RequestMapping(value = "/searchAround", method = RequestMethod.POST)
    public String searchAround(Integer area_eid, String number) {
        if (area_eid == null || number == null || number.isEmpty()) {
            logger.warn("传入就近号码信息不完整");
            return null;
        }
        return ablNearSearchSvc.getNearAddInfo(area_eid, number);
    }

    @ResponseBody
    @RequestMapping(value = "/getProjectInfo", method = RequestMethod.POST)
    public List<String> getProjectInfo(String areaCode, Integer gridId) {
        if (areaCode == null || gridId == null || areaCode.isEmpty()) {
            logger.warn("传入工程信息查询条件不完整");
            return null;
        }
        return getProjInfoSvc.getProjInfo(areaCode, gridId);
    }

    @ResponseBody
    @RequestMapping(value = "/getProjectInfo2", method = RequestMethod.POST)
    public List<String> getProjectInfo(String areaId, String addrId) {
        if (areaId == null || addrId == null || areaId.isEmpty() || addrId.isEmpty()) {
            logger.warn("传入工程信息查询条件不完整");
            return null;
        }
        return getProjInfoSvc.getProjInfo2(areaId, addrId);
    }

    @ResponseBody
    @RequestMapping(value = "/getServiceTel", method = RequestMethod.POST)
    public String getServiceTel(Integer gridId) {
        if (gridId == null || gridId < 0) {
            logger.warn("传入网格ID为空");
        }
        return getServiceTelSvc.getServiceTel(gridId);
    }

    @ResponseBody
    @RequestMapping(value = "/getXqtDataByProduct", method = RequestMethod.POST)
    public Map<String, Object> getXqtDataByProduct(String areaCode, String productCode, String productInstance) {
        if (productCode == null || productInstance == null) {
            logger.warn("传入参数不完整");
            return null;
        }
        return getXqtInfoSvc.getXqtInfo(areaCode, productCode, productInstance);
    }
}