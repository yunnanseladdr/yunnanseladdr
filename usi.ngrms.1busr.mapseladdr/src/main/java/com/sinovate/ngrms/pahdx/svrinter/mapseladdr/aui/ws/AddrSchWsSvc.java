package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.ws;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * webService接口
 * @author john
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
@WebService(targetNamespace = "http://integration.service.cots")
public interface AddrSchWsSvc {

	static final Logger logger = LoggerFactory.getLogger(AddrSchWsSvc.class);

	/**
	 * 调用Es查询接口和查询算法获取地址数据后进行报文封装
	 * 
	 * @param reqDto
	 */
	@WebMethod(operationName="service")
	@WebResult(name="out") String queryResStdAddr(@WebParam(name = "in0", targetNamespace = "http://integration.service.cots") final String reqDto);

}