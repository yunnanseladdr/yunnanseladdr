package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.commu;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.ResSiteBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/5/27.
 */
@Service
public class ResAddrSchSvcImpl implements ResAddrSchSvc {

    private static final Logger logger = LoggerFactory.getLogger(ResAbilityImpl.class);

    public ResAddrSchSvcImpl(){

    }

    public void finalize() throws Throwable {

    }

    /**
     * 根据区域编码、用户输入获取用户地址数据，调用国朗注册在oip上的webService接口
     * @param areaCode
     * @param searchText
     * @param pageIndex
     * @param resultSize
     * @param oipUrl
     * @return
     * @throws HttpException
     * @throws IOException
     */
    @Override
    public List<UserAddrVo> getResAddr(String areaCode, String searchText , int pageIndex, int resultSize, String oipUrl, String sysCode) throws HttpException, IOException{

        logger.info("区域编码："+areaCode+";"+"searchText:"+searchText+";"+"pageIndex:"+pageIndex+";"+"resultSize:"+resultSize+"oipUrl:"+oipUrl + "sysCode:" + sysCode);
        List<UserAddrVo> lists = new ArrayList<UserAddrVo>();
        String soapRequestData = createRequestXml(areaCode, searchText, pageIndex, resultSize, "42010100", sysCode);
        PostMethod postMethod = new PostMethod(oipUrl);

        // 把Soap请求数据添加到PostMMethod中
        byte[] b = soapRequestData.getBytes("utf-8");
        InputStream is = new ByteArrayInputStream(b,0,b.length);
        RequestEntity re = new InputStreamRequestEntity(is,b.length,"application/soap+xml; charset=utf-8");
        postMethod.setRequestEntity(re);
        // 最后生成一个HttpClient对象，并发出postMethod请求
        HttpClient httpClient = new HttpClient();
        int statusCode = httpClient.executeMethod(postMethod);
        String soapResponseData = null;
        if(statusCode == 200) {
            logger.info("调用成功");
            soapResponseData = postMethod.getResponseBodyAsString();
            soapResponseData = soapResponseData.replace("&lt;","<");
            soapResponseData = soapResponseData.replace("&gt;",">");
        } else {
            logger.info("调式失败！错误码：" + statusCode);
        }

        soapResponseData = getXmlStr("<ns1:out>", soapResponseData);
        if (null == soapResponseData) {
            return null;
        }
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new StringReader(soapResponseData));
            Element info = document.getRootElement();
            Element addressCount = info.element("addressCount");
            List addressObjectList = info.elements("addressObject");
            Element sitesNode = null;
            List sitesIdList = null;
            List siteCodeList = null;
            List siteNameList = null;
            Element addressObject = null;
            Element siteIdNode = null;
            Element siteCodeNode = null;
            Element siteNameNode = null;
            if(null != addressObjectList && addressObjectList.size() > 0) {
                UserAddrVo vo = null;
                for(int i = 0; i < addressObjectList.size(); i++) {
                    addressObject = (Element) addressObjectList.get(i);
                    vo = new UserAddrVo();
                    vo.setId(addressObject.element("addressId").getTextTrim());
                    vo.setHighlightText(addressObject.element("addressName").getTextTrim());
                    vo.setName(addressObject.element("addressName").getTextTrim());
                    sitesNode = (Element)addressObject.element("sites");
                    sitesIdList = sitesNode.elements("siteId");
                    siteCodeList = sitesNode.elements("siteCode");
                    siteNameList = sitesNode.elements("siteName");
                    Set<ResSiteBo> list = new HashSet<>();
                    if(null != sitesIdList && sitesIdList.size() == siteCodeList.size() && siteCodeList.size() == siteNameList.size() ) {
                        ResSiteBo bo = null;
                        for(int j = 0; j < sitesIdList.size(); j++) {
                            bo = new ResSiteBo();
                            siteIdNode = (Element) sitesIdList.get(j);
                            siteCodeNode = (Element) siteCodeList.get(j);
                            siteNameNode = (Element) siteNameList.get(j);;
                            bo.setSiteId(siteIdNode.getTextTrim());
                            bo.setSiteCode(siteCodeNode.getTextTrim());
                            bo.setSiteName(siteNameNode.getTextTrim());
                            list.add(bo);
                        }
                    }
                    vo.setSites(list);
                    lists.add(vo);
                }
            }
        } catch (Exception e) {
            logger.info("返回报文解析异常");
        }
        if(null == lists && lists.isEmpty()) {
            return null;
        }
        return lists;
    }

    /**
     * 构建资源预判接口的请求报文
     * @param areaCode
     * @param searchText
     * @param pageIndex
     * @param resultSize
     * @param projectCode
     * @return
     */
    private String createRequestXml(String areaCode, String searchText , int pageIndex, int resultSize, String projectCode, String sysCode) {

        StringBuffer reqXml = new StringBuffer();
        reqXml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<soapenv:Header><Esb soapenv:actor=\"\" soapenv:mustUnderstand=\"0\">");
        reqXml.append("<Route>");
        reqXml.append("<MsgId>"+sysCode+"_"+ getTime()+"_"+get3Random()+"</MsgId>");
        reqXml.append("<Sender>"+sysCode+"</Sender>");
        reqXml.append("<Time>"+getRightNowTime()+"</Time>");
        reqXml.append("<AuthCode/>" +
                "<ServCode>1103.wsfindAddresses"+ areaCode +".SynReq</ServCode>" +
                "<TransId/>" +
                "<ServTestFlag/>" +
                "<EsbId/>" +
                "<Version/>" +
                "<CarryType/>" +
                "<AuthType/>" +
                "<MsgType/>" +
                "</Route>");
        reqXml.append("</Esb></soapenv:Header><soapenv:Body><ns1:service xmlns:ns1=\"http://integration.service.cots\"" +
                " soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<ns1:arg0 xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
                "xsi:type=\"soapenc:string\">" +
                "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;");
        reqXml.append("&lt;info&gt;&lt;params.key&gt;" + areaCode + "&lt;/params.key&gt;" +
                "&lt;projectCode&gt;" + projectCode + "&lt;/projectCode&gt;" +
                "&lt;areaCode&gt;" + areaCode + "&lt;/areaCode&gt;" +
                "&lt;addressCodePattern&gt;&lt;/addressCodePattern&gt;" +
                "&lt;LATN_ID_KEY&gt;" + areaCode + "&lt;/LATN_ID_KEY&gt;" +
                "&lt;addressNamePattern&gt;" + searchText + "&lt;/addressNamePattern&gt;" +
                "&lt;taskCode&gt;PstnAddressService&lt;/taskCode&gt;" +
                "&lt;pageIndex&gt;" + pageIndex + "&lt;/pageIndex&gt;" +
                "&lt;nodeId&gt;&lt;/nodeId&gt;" +
                "&lt;nearPstnCode&gt;&lt;/nearPstnCode&gt;" +
                "&lt;businessOperation&gt;findAddresses&lt;/businessOperation&gt;" +
                "&lt;resultSize&gt;" + resultSize + "&lt;/resultSize&gt;" +
                "&lt;subAreaId&gt;&lt;/subAreaId&gt;" +
                "&lt;/info&gt;</ns1:arg0></ns1:service></soapenv:Body></soapenv:Envelope>");

        return reqXml.toString();
    }

    /**
     * 从oip的报文获取标准的xml报文
     * @param key
     * @param xml
     * @return
     */
    private String getXmlStr(String key, String xml) {
        if (null == xml || "".equals(xml)) {
            throw new NullPointerException("返回报文为空！");
        }
        Integer index = xml.indexOf(key,1);
        xml = xml.substring(index+9);
        index = xml.indexOf("</ns1:out>", 1);
        if (index == -1) {
            logger.info("返回报文：" + xml + "有问题！");
            return null;
        }
        xml = xml.substring(0,index-1);
        logger.info(xml);
        return xml;
    }

    /**
     * 获取指定格式的时间字符串
     * @return
     */
    private String getTime(){
        String time = null;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(" 'time':yyyyMMdd");
        time = sdf.format(date)+date.getTime();
        return time;
    }

    /**
     * 获取三位的随机数用来构建调用oip所需要的msgId字段
     * @return
     */
    private String get3Random(){
        Random random = new Random();
        String randomStr = "";
        for (int i = 0; i < 3; i ++) {
            int s = random.nextInt(9) % (9 - 0 + 1) + 0;
            randomStr += s;
        }
        return randomStr;
    }

    /**
     * 获取当前时间
     * @return
     */
    private String getRightNowTime(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(" 'time':yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    public static void main(String[] ar) throws Exception{
        ResAddrSchSvcImpl impl  =  new ResAddrSchSvcImpl();
        List<UserAddrVo>  listVos = impl.getResAddr("551", "%2%", 1, 500, "http://134.64.110.182:9999/service/mboss/route","1001");

    }
}

