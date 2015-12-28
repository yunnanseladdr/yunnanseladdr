package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.ws;

import com.sinovate.ngrms.pahdx.svrinter.addrsearch.das.model.ResAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.addrsearch.ps.websvr.AddrSchEsSvcImpl;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsSuggestAddrSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.ResSiteBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.StreetBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.SuggestiveAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.AdminDivisionSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsUserAddrSvc;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * webService接口实现
 * @author john
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
@WebService(targetNamespace = "http://integration.service.cots")
public class AddrSchWsSvcImpl implements AddrSchWsSvc {

	private static final String RESULT_ERROR_TYPE = "error";//报文错误

	private static final String RESULT_EMPTY_TYPE = "empty";//返回地址为空

	private static final String IS_USERADDRDATA = "1";//用户地址数据

	private static final String IS_SUGGADDRDATA = "0";//推荐词地址数据

	private static final String IS_STREETDATA = "2";//街道乡镇地址数据

	private static final String SUB_AREA_MAP = "subareamap.properties";

	Map<String,Map<String, String>> subAreaNameMap  = new HashMap<String,Map<String, String>>();

	@Autowired
	private AdminDivisionSvc adminDivisionSvc;

	@Autowired
	private PsSuggestAddrSvc psSuggestAddrSvc;

	@Autowired
	private PsUserAddrSvc psUserAddrSvc;

	private static final Logger logger = LoggerFactory.getLogger(AddrSchWsSvcImpl.class);

	protected static final Logger log4extflume = LoggerFactory.getLogger("CMCC-EXT-FLUME");

	DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private  String LOG_FLUME_INFO = "@|BL|数据同步@（NGRMS预选址）@|" + dfmt.format(new Date()) + "@|一般@|";

	private String LOG_FLUME_WARN = "@|BL|数据同步@（NGRMS预选址）@|" + dfmt.format(new Date()) + "@|重要@|";

	private final static String REMOTE_SUBAREAMAP = "subarea-subarea-map.properties";//网厅子区域和资源子区域的映射


	public AddrSchWsSvcImpl(){

	}

	public void finalize() throws Throwable {

	}
	public void initSubAreaMap() {
		Map<String, String> subAreaMap = null;
		Properties properties = new Properties();
		try {
			InputStream inputStream = AddrSchWsSvcImpl.class.getClassLoader().getResourceAsStream(SUB_AREA_MAP);
			properties.load(inputStream);
			String key = null;
			String value = null;
			String[] values = null;
			for (int i = 550; i <= 567; i++) {
				subAreaMap = new HashMap<String, String>();
				if (i == 560 || i == 565)
					continue;
				value = properties.getProperty("localSubAreaMap"+i);
				String[] subAreaArray = value.split(":");
				key = subAreaArray[0];
				values = subAreaArray[1].split(",");
				for (String s : values){
					subAreaMap.put(s,key);
				}
				this.subAreaNameMap.put(String.valueOf(i),subAreaMap);
			}
		}catch (IOException e) {
			logger.info("subareamap.properties文件存在问题！");
		}
	}
	private String getRightSubArea(String areaCode, String subAreaName) {
		Map<String,String> subMaps = this.subAreaNameMap.get(areaCode);
		if (null == subMaps) return subAreaName;
		if(subMaps.keySet().contains(subAreaName)) {
			return subMaps.get(subAreaName);
		}
		return subAreaName;

	}
	/**
	 * 调用Es查询接口和查询算法获取地址数据后进行报文封装
	 *
	 * @param reqDto
	 */
	@Override
	public String queryResStdAddr(String reqDto){

		String bak = null;

		if(reqDto != null && !"".equals(reqDto)) {

			SAXReader saxReader = new SAXReader();
			try {
				Document document = saxReader.read(new StringReader(reqDto));
				Element root = document.getRootElement();
				CdcBaseBo cdcBaseBo = new CdcBaseBo();

				String areaCode = null;
				String subAreaId = null;
				String streetOrTownName = null;
				String addressNamePattern = null;
				String addressCodePattern = null;
				int pageIndex = 1;
				int resultSize = 100;

				try {
					streetOrTownName = root.element("streetOrTownName").getTextTrim();
				} catch (Exception e) {
					logger.info("没有streetOrTownName这个节点!");
				}
				try {
					pageIndex = Integer.valueOf(root.element("pageIndex").getTextTrim());
					if (pageIndex < 0) {
						pageIndex = 1;
					}
				} catch (NumberFormatException e) {
					logger.info("pageIndex节点数值不合法！");

				}
				try {
					resultSize = Integer.valueOf(root.element("resultSize").getTextTrim());
					if (resultSize < 0) {
						resultSize = 100;
					}
				} catch (NumberFormatException e) {
					logger.info("resultSize节点数值不合法！");

				}

				areaCode = root.element("areaCode").getTextTrim();
				// 通过枚举将crm中560转换成567（亳州）
//				if(!"".equals(getRightSubName(areaCode)) && null != getRightSubName(areaCode)) {
//					areaCode = getRightSubName(areaCode);
//				}
				if ("560".equals(areaCode)) {
					areaCode = "567";
				}

				subAreaId = root.element("subAreaId").getTextTrim();
				String oldSubAreaId = subAreaId;
				//过滤查询参数中的“部队”，2015-06-18，涂成红
				addressNamePattern = root.element("addressNamePattern").getTextTrim().toLowerCase().replace("部队","");
				addressCodePattern = root.element("addressCodePattern").getTextTrim().toLowerCase().replace("部队", "");
				subAreaId = getRightSubArea(areaCode,subAreaId);
				// 验证区域不为空子区域名称不为空
				if(null == areaCode || "".equals(areaCode) || null == subAreaId || "".equals(subAreaId)) {
					return bakWithEmptyOrError(RESULT_ERROR_TYPE);
				}
				// 只有区域和子区域不为空，查询子区域下的街道乡镇信息
				if (("".equals(addressNamePattern)||null == addressNamePattern) &&
						("".equals(addressCodePattern)||null == addressCodePattern) && ("".equals(streetOrTownName)||null == streetOrTownName)) {
					List<StreetBo> streetLists = null;
					try {
						streetLists = adminDivisionSvc.getStreetNames(areaCode, oldSubAreaId);
					}catch(Exception e) {
						logger.info("找不到子区域"+subAreaId+"对应的街道乡镇信息！");
					}
					if (null == streetLists || streetLists.isEmpty() ) {
						return bakWithEmptyOrError(RESULT_EMPTY_TYPE);
					}
					return bakWithStreetBos(streetLists);
				}
				String searchText = null;
				StringBuffer searchTextBuffer = new StringBuffer();
				List<String> params = new ArrayList<String>();
				params.add(subAreaId);
				params.add(streetOrTownName);
				params.add(addressCodePattern);
				params.add(addressNamePattern);
				for (int i = 0 ; i < params.size(); i ++) {
					if (null != params.get(i) && !"".equals(params.get(i))) {
						if (i !=  params.size() - 1) {
							searchTextBuffer.append(params.get(i)).append(" ");
						}else {
							searchTextBuffer.append(params.get(i));
						}
					}
				}
				searchText = searchTextBuffer.toString();
				String[] inputParams = addressNamePattern.split(" ");
				int inputParamsCount = 0;
				for (int i = 0 ; i < inputParams.length ; i ++ ) {
					if (null != inputParams[i] && inputParams[i].length() > 0 && !"".equals(inputParams[i])) {
						inputParamsCount ++;
					}
				}
				if (null == addressCodePattern || "".equals(addressCodePattern)) {
					if (inputParamsCount > 1) {
						//查询用户地址数据
						List<UserAddrVo> userLists = psUserAddrSvc.getUserAddr(areaCode, searchText, pageIndex, resultSize);
						if (null == userLists || userLists.isEmpty() ) {
							return bakWithEmptyOrError(RESULT_EMPTY_TYPE);
						}
						return bakWithUserAddrVos(userLists);
					} else {
						//查询提示词数据
						List<SuggestiveAddrVo> suggLists = psSuggestAddrSvc.getSuggAddr(areaCode, searchText, pageIndex, resultSize);
						if (null == suggLists || suggLists.isEmpty() ) {
							return bakWithEmptyOrError(RESULT_EMPTY_TYPE);
						}
						return bakWithSuggVoLists(suggLists);
					}
				}else {
					//查询用户地址数据
					List<UserAddrVo> userLists = psUserAddrSvc.getUserAddr(areaCode, searchText, pageIndex, resultSize);
					if (null == userLists || userLists.isEmpty() ) {
						return bakWithEmptyOrError(RESULT_EMPTY_TYPE);
					}
					return bakWithUserAddrVos(userLists);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bak;
	}

	private String createOutXml(){
		return null;
	}

	private String bakWithEmptyOrError(String result) {
		String bak = null;
		Document faultOut = DocumentHelper.createDocument();
		Element root = null;
		if (RESULT_ERROR_TYPE.equals(result)) {
			root = faultOut.addElement("error");
			root.setText("区域编号/子区域名称必填，请检查确认！");
		}else if (RESULT_EMPTY_TYPE.equals(result)) {
			root = faultOut.addElement("info");
			Element count = root.addElement("addressCount");
			count.setText("0");
		}
		faultOut.setRootElement(root);
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw);
		try {
			xw.setEscapeText(false);
			xw.write(faultOut);
			bak = sw.toString();
			xw.close();
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != xw) {
				try {
					xw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != sw) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bak;
	}

	/**
	 * 拼装街道提示词
	 * @param stLists
	 * @return
	 */
	private String bakWithStreetBos(List<StreetBo> stLists) {
		StringBuffer bufferNames = new StringBuffer();
		for (int i = 0 ; i < stLists.size(); i ++ ) {
			if (null != stLists.get(i).getName() && !"".equals(stLists.get(i).getName())) {
				if (i != stLists.size()) {
					bufferNames.append(stLists.get(i).getName()).append(";");
				}else {
					bufferNames.append(stLists.get(i).getName());
				}
			}
		}
		String bak = null;
		Document outDocs = DocumentHelper.createDocument();
		Long start = System.currentTimeMillis();
		Element info = null;
		info = outDocs.addElement("info");
		Element isAddrData = info.addElement("isAddrData");
		isAddrData.setText(IS_STREETDATA);
		Element tmpIndexE = null;
		Element tmpIndexAttrs = null;
		Element addressCount = null;
		tmpIndexE = info.addElement("addressObject");
		tmpIndexAttrs = tmpIndexE.addElement("streetOrTownNames");
		tmpIndexAttrs.setText(bufferNames.toString());
		addressCount = tmpIndexE.addElement("tmpIndexE");
		addressCount.setText("1");
		Long end = System.currentTimeMillis();
		logger.info(" 关键词(街道/乡镇)Dom拼装耗时：" + (end-start));
		log4extflume.info(LOG_FLUME_INFO +" 关键词(街道/乡镇)Dom拼装耗时：" + (end-start));
		outDocs.setRootElement(info);
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw);
		try {
			xw.setEscapeText(false);
			xw.write(outDocs);
			bak = sw.toString();
//                    logger.info("返回报文信息为：" + bak);
			xw.close();
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != xw) {
				try {
					xw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != sw) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bak;
	}

	/**
	 * 拼接用户地址数据
	 * @param userVoLists
	 * @return
	 */
	private String bakWithUserAddrVos( List<UserAddrVo> userVoLists) {
		String bak = null;
		Document outDocs = DocumentHelper.createDocument();
		Element info = null;
		info = outDocs.addElement("info");
		Element isAddrData = info.addElement("isAddrData");
		isAddrData.setText(IS_USERADDRDATA);
		Element count = info.addElement("addressCount");
		count.setText(String.valueOf(userVoLists.size()));
		ResAddrBo addrBo = null;
		Element tmpAddrE = null;
		Element tmpAttrs = null;
		Element tmpSite = null;
		Set<ResSiteBo> siteBos = null;
		for(UserAddrVo userVo : userVoLists) {
			//过滤掉返回的用户地址中带有“部队”的地址，2015-06-18，涂成红
			if(checkArmy(null != userVo.getName() ? userVo.getName().replace(" ", "") : "")){
				continue;
			}
			tmpAddrE = info.addElement("addressObject");
			tmpAttrs = tmpAddrE.addElement("addressId");
			tmpAttrs.setText(null!=userVo.getId()?userVo.getId():"");
			tmpAttrs = tmpAddrE.addElement("addressName");
			tmpAttrs.setText(null!=userVo.getName()?userVo.getName().replace(" ",""):"");
			tmpAttrs = tmpAddrE.addElement("addressLevel");
			tmpAttrs.setText("");
			tmpAttrs = tmpAddrE.addElement("highlightText");
			tmpAttrs.setText(null!=userVo.getHighlightText()?userVo.getHighlightText():"");
			tmpSite = tmpAddrE.addElement("sites");
			siteBos = userVo.getSites();
			if(null!=siteBos && siteBos.size()>0) {
				for(ResSiteBo site : siteBos) {
					tmpAttrs = tmpSite.addElement("siteId");
					tmpAttrs.setText(null!=site.getSiteId()?site.getSiteId():"");
					tmpAttrs = tmpSite.addElement("siteCode");
					tmpAttrs.setText(null!=site.getSiteCode()?site.getSiteCode():"");
					tmpAttrs = tmpSite.addElement("siteName");
					tmpAttrs.setText(null!=site.getSiteName()?site.getSiteName():"");
				}
			} else {
				tmpAttrs = tmpSite.addElement("siteId");
				tmpAttrs.setText("");
				tmpAttrs = tmpSite.addElement("siteCode");
				tmpAttrs.setText("");
				tmpAttrs = tmpSite.addElement("siteName");
				tmpAttrs.setText("");
			}
		}
		outDocs.setRootElement(info);
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw);
		try {
			xw.setEscapeText(false);
			xw.write(outDocs);
			bak = sw.toString();
//                    logger.info("返回报文信息为：" + bak);
			xw.close();
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != xw) {
				try {
					xw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != sw) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bak;
	}


	/**
	 * 判断字符串中是否包含“部队”
	 * @param name
	 * @return
	 */
	private  boolean checkArmy(String name){
		String word = "部队";
		if(null == name){
			return true;
		}
		boolean flag = name.contains(word);
		return flag;
	}


	/**
	 * 拼接提示词地址数据
	 * @param suggVoLists
	 * @return
	 */
	private String bakWithSuggVoLists( List<SuggestiveAddrVo> suggVoLists) {
		String bak = null;
		Document outDocs = DocumentHelper.createDocument();
		//redis直接查数据
		Element info = null;
		info = outDocs.addElement("info");
		Element isAddrData = info.addElement("isAddrData");
		isAddrData.setText(IS_SUGGADDRDATA);
		Element count = info.addElement("addressCount");
		count.setText(String.valueOf(suggVoLists.size()));
		Element tmpIndexE = null;
		Element tmpIndexAttrs = null;
		for (SuggestiveAddrVo bo : suggVoLists) {
			//过滤掉返回的8级地址中带有“部队”的地址，2015-06-18，涂成红
			if(checkArmy(null!= bo.getNameBak()? bo.getNameBak().replace("<em>", "").replace("</em>",""):"")){
				continue;
			}
			tmpIndexE = info.addElement("addressObject");
			tmpIndexAttrs = tmpIndexE.addElement("addressName");
			tmpIndexAttrs.setText(null!= bo.getNameBak()? bo.getNameBak().replace("<em>","").replace("</em>",""):"");
			tmpIndexAttrs = tmpIndexE.addElement("hightlightText");
			tmpIndexAttrs.setText(null!= bo.getNameBak()? bo.getNameBak():"");
			tmpIndexAttrs = tmpIndexE.addElement("subNameLists");
			//过滤9级地址中”部队“地址信息，2015-06-18，涂成红
			String nineAddrName="";
			if(null !=bo.getSubNames()){
				nineAddrName = getNineAddrName(bo.getSubNames());
			}
			tmpIndexAttrs.setText(nineAddrName);
		}
		outDocs.setRootElement(info);
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw);
		try {
			xw.setEscapeText(false);
			xw.write(outDocs);
			bak = sw.toString();
//                    logger.info("返回报文信息为：" + bak);
			xw.close();
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != xw) {
				try {
					xw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != sw) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
     	return bak;
	}
	private String getRightSubName(String subName) {
		long start = System.currentTimeMillis();
		String result = "";
		String keyMapNums = "keyMapNums";
		String keyPrix = "keyPrix";
		Properties properties = new Properties();
		String cfgFile = REMOTE_SUBAREAMAP;
		try {
			InputStream inputStream = AddrSchEsSvcImpl.class.getClassLoader().getResourceAsStream(cfgFile);
			properties.load(inputStream);
			int nums = Integer.valueOf(properties.getProperty(keyMapNums));
			String mapName = properties.getProperty(keyPrix);
			String value = null;
			for (int i = 1; i <= nums; i++) {
				value = properties.getProperty(mapName+i);
				String[] v = value.split(",");
				if (subName.equals(v[0])) {
					result = v[1];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		logger.info("区域-子区域映射查找耗时为：" + (end-start));
		log4extflume.info(LOG_FLUME_INFO+"区域-子区域映射查找耗时为：" + (end-start));
		return result;
	}

	/**
	 * 过滤9级地址中包含“部队”的地址信息
	 * @param name
	 * @return
	 */
	private String getNineAddrName(String name){
		String nineAddrName="";
		if(null !=name){
			String[] nineAddrs = name.split(" ");
			StringBuffer nineName=new StringBuffer();
			for(int i=0;i<nineAddrs.length;i++){
				if(checkArmy(nineAddrs[i])){
					continue;
				}
				if(i==0){
					nineName.append(nineAddrs[i]);
				}else{
					nineName.append(" "+nineAddrs[i]);
				}
			}
			//九级地址之间以;号相隔
			nineAddrName = (null!=nineName?nineName.toString().replace(" ",";"):"");
		}
		return nineAddrName;
	}

}