package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.svc.AblUserAddrSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.UserAddrVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.PsUserAddrSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:38:34
 */
@Service
public class AblUserAddrSvcImpl implements AblUserAddrSvc {

	private static final Logger logger = LoggerFactory.getLogger(AblUserAddrSvcImpl.class);

	@Autowired
	@Qualifier("psUserAddrSvcImpl")
	private PsUserAddrSvc psUserAddrSvc;

	public AblUserAddrSvcImpl(){

	}

	public void finalize() throws Throwable {

	}

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
	public List<UserAddrVo> getUserAddrByInput(String areaCode, String input, int pageNo, int pageSize){

		if(areaCode == null || areaCode.isEmpty()) {
			logger.warn("区域代码areaCode为空，无法处理！");
			return null;
		}

		if(input == null || input.isEmpty()) {
			logger.warn("input为空，无法处理");
			return null;
		}

		//		setSites(list,areaCode);
		return psUserAddrSvc.getUserAddr(areaCode, input, pageNo, pageSize);
	}

	/**
	 * 取用户地址信息
	 * 根据用户输入取地址信息
	 * 实现步骤：
	 * 1、将subAreaName、streetName、input中间以空格隔开后赋值给searchText；
	 * 2、调用PsUserAddrSvc中的getUserAddr(String,String,PageRequest)方法获得用户地址信息;
	 * 
	 * @param areaCode 不可为空
	 * @param subAreaName  可为空
	 * @param streetName 可为空
	 * @param input 不可为空
	 * @param pageNo 不可为空
	 * @param pageSize 不可为空
	 */
	public List<UserAddrVo> getUserAddrBySuggest(String areaCode, String subAreaName, String streetName, String input, int pageNo, int pageSize){

		String searchText = null;
		List<UserAddrVo> list = null;

		if(areaCode == null || areaCode.isEmpty()) {
			logger.warn("区域代码areaCode为空，无法处理！");
			return null;
		}

		if(input == null || input.isEmpty()) {
			logger.warn("input为空，无法处理");
			return null;
		}

		if(subAreaName == null || subAreaName.isEmpty()) {
			searchText = (new StringBuilder(input)).toString();
			list = psUserAddrSvc.getUserAddr(areaCode, searchText, pageNo, pageSize);
		}else {
			if(streetName == null || streetName.isEmpty()) {
				searchText = (new StringBuilder(subAreaName).append(" ").append(input)).toString();
				list = psUserAddrSvc.getUserAddr(areaCode, searchText, pageNo, pageSize);
			} else {
				searchText = (new StringBuilder(subAreaName).append(" ").append(streetName).append(" ").append(input)).toString();
				list = psUserAddrSvc.getUserAddr(areaCode, searchText, pageNo, pageSize);
			}
		}
		return list;
	}
}

