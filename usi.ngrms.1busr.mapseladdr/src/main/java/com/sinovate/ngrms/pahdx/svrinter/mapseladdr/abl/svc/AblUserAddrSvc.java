package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用户地址服务
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:38:34
 */
public interface AblUserAddrSvc {

	static final Logger logger = LoggerFactory.getLogger(AblUserAddrSvc.class);

	/**
	 * 取用户地址信息（全地址查询）
	 * 根据用户输入取地址信息
	 * 实现步骤：
	 * 1、将input赋值给searchText；
	 * 2、调用PsUserAddrSvc中的getUserAddr(String,String,PageRequest)方法获得用户地址信息;
	 * 
	 * @param areaCode
	 * @param input
	 * @param pageNo
	 * @param pageSize
	 */
	public List<UserAddrVo> getUserAddrByInput(String areaCode, String input, int pageNo, int pageSize);

	/**
	 * 取用户地址信息（全地址查询）
	 * 根据用户输入取地址信息
	 * 实现步骤：
	 * 1、将subAreaName、streetName、input中间以空格隔开后赋值给searchText；
	 * 2、调用PsUserAddrSvc中的getUserAddr(String,String,PageRequest)方法获得用户地址信息;
	 * 
	 * @param areaCode
	 * @param subAreaName
	 * @param streetName
	 * @param input
	 * @param pageNo
	 * @param pageSize
	 */
	public List<UserAddrVo> getUserAddrBySuggest(String areaCode, String subAreaName, String streetName, String input, int pageNo, int pageSize);

}