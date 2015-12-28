package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.commu;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.ResAbilityBo;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 资源能力接口实现，本实现是调用资源生产库提供的方法。
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:45
 */
@Service
public class ResAbilityImpl implements ResAbility {

	private static final Logger logger = LoggerFactory.getLogger(ResAbilityImpl.class);

	public ResAbilityImpl(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 *	使用httpClient调用注册在oip上的资源预判webService接口
	 * @param areaCode 区域编码
	 * @param addrId 地址id
	 * @param siteId 站点id
	 * @param siteName 站点名称
	 * @param oipUrl 资源预判webService接口地址
	 * @return
	 * @throws HttpException
	 * @throws java.io.IOException
	 */
	public List<ResAbilityBo> getResAbility(String areaCode, String addrId,
									  String siteId, String siteName,
									  String oipUrl) throws HttpException, IOException {
		logger.info("区域编码："+areaCode+";"+"地址id:"+addrId+";"+"站点id:"+siteId+";"+"oipUrl:"+oipUrl);
		List<ResAbilityBo> lists = new ArrayList<ResAbilityBo>();
		String soapRequestData = createRequestXml(areaCode,addrId,siteId,"42010100","PstnAddressService");
		//"http://134.64.110.182:9999/service/mboss/route?wsdl"
		PostMethod postMethod = new PostMethod(oipUrl);

		// 然后把Soap请求数据添加到PostMethod中
		byte[] b = soapRequestData.getBytes("utf-8");
		InputStream is = new ByteArrayInputStream(b, 0, b.length);
		RequestEntity re = new InputStreamRequestEntity(is, b.length,
				"application/soap+xml; charset=utf-8");
		postMethod.setRequestEntity(re);
		// 最后生成一个HttpClient对象，并发出postMethod请求
		HttpClient httpClient = new HttpClient();
		int statusCode = httpClient.executeMethod(postMethod);
		String soapResponseData = null;
		if(statusCode == 200) {
			logger.info("调用成功！");
			soapResponseData = postMethod.getResponseBodyAsString();
			soapResponseData = soapResponseData.replace("&lt;","<");
			soapResponseData = soapResponseData.replace("&gt;",">");
		} else {
			logger.info("调用失败！错误码：" + statusCode);
		}
//		logger.info("获取到的资源确认报文信息："+soapResponseData);
		soapResponseData = getXmlStr("<ns1:out>",soapResponseData);
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(new StringReader(soapResponseData));
			Element info = document.getRootElement();
			Element result = info.element("result");
			Element voiceAccessTypes = info.element("voiceAccessTypes");
			List voiceAcTyLists  = voiceAccessTypes.elements("voiceAccessType");
			Element voiceAccessType = null;
			if (null != voiceAcTyLists && voiceAcTyLists.size() > 0) {
				ResAbilityBo bo = null;
				for (int i = 0 ;i < voiceAcTyLists.size(); i ++) {
					bo = new ResAbilityBo();
					voiceAccessType = (Element) voiceAcTyLists.get(i);
					bo.setProductName("语音");
					bo.setTerminalPortType(voiceAccessType.element("terminalPortType").getTextTrim());
					bo.setTerminal(voiceAccessType.element("terminal").getTextTrim());
					bo.setIsE8C(voiceAccessType.element("isE8C").getTextTrim());
					bo.setPonType(voiceAccessType.element("ponType").getTextTrim());
					bo.setAbility(voiceAccessType.element("ability").getTextTrim());
					bo.setIsFirstChoice(voiceAccessType.element("isFirstChoice").getTextTrim());
					bo.setPostDirection(siteName);
					lists.add(bo);
				}
			}
			Element broadBandAccessTypes = info.element("broadBandAccessTypes");
			List broadBandAcTyLists  = broadBandAccessTypes.elements("broadBandAccessType");
			Element broadBandAccessType = null;
			if (null != broadBandAcTyLists && broadBandAcTyLists.size() > 0) {
				ResAbilityBo bo = null;
				for (int i = 0 ;i < broadBandAcTyLists.size(); i ++) {
					bo = new ResAbilityBo();
					broadBandAccessType = (Element) broadBandAcTyLists.get(i);
					bo.setProductName("天翼宽带有线");
					bo.setTerminalPortType(broadBandAccessType.element("terminalPortType").getTextTrim());
					bo.setTerminal(broadBandAccessType.element("terminal").getTextTrim());
					bo.setIsE8C(broadBandAccessType.element("isE8C").getTextTrim());
					bo.setPonType(broadBandAccessType.element("ponType").getTextTrim());
					bo.setAbility(broadBandAccessType.element("ability").getTextTrim());
					bo.setLogicRate(broadBandAccessType.element("logicRate").getTextTrim());
					bo.setIsFirstChoice(broadBandAccessType.element("isFirstChoice").getTextTrim());
					bo.setPostDirection(siteName);
					lists.add(bo);
				}
			}
		}catch (Exception e) {
			logger.info("返回报文解析异常");
		}
		if (null != lists && lists.size() > 0){
			return lists;
		}
		return null;
	}

	/**
	 * 构建资源预判接口的请求报文
	 * @param areaCode
	 * @param addrId
	 * @param siteId
	 * @param projectCode
	 * @param taskCode
	 * @return
	 */
	private String createRequestXml(String areaCode,String addrId,String siteId,String projectCode,String taskCode) {
		StringBuffer reqXml = new StringBuffer();
		reqXml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
				" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"<soapenv:Header><Esb soapenv:actor=\"\" soapenv:mustUnderstand=\"0\">");
		reqXml.append("<Route>");
		reqXml.append("<MsgId>"+"TYZY_"+ getTime()+"_"+get3Random()+"</MsgId>");
		reqXml.append("<Sender>TYZY</Sender>");
		reqXml.append("<Time>"+getRightNowTime()+"</Time>");
		reqXml.append("<AuthCode/>" +
				"<ServCode>1103.wsconfirmResource"+ areaCode +".SynReq</ServCode>" +
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
		reqXml.append("&lt;info&gt;&lt;params.key&gt;"+areaCode+"&lt;/params.key&gt;" +
				"&lt;projectCode&gt;"+ projectCode +"&lt;/projectCode&gt;" +
				"&lt;siteId&gt;"+siteId+"&lt;/siteId&gt;" +
				"&lt;areaCode&gt;"+areaCode+"&lt;/areaCode&gt;" +
				"&lt;LATN_ID_KEY&gt;"+areaCode+"&lt;/LATN_ID_KEY&gt;" +
				"&lt;taskCode&gt;"+taskCode+"&lt;/taskCode&gt;" +
				"&lt;businessOperation&gt;confirmResource&lt;/businessOperation&gt;" +
				"&lt;addressId&gt;"+addrId+"&lt;/addressId&gt;" +
				"&lt;/info&gt;</ns1:arg0></ns1:service></soapenv:Body></soapenv:Envelope>");
		logger.debug(reqXml.toString());

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

}