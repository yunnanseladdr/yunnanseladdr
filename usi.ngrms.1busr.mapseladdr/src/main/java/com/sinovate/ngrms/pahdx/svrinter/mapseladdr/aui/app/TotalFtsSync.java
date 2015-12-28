package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.app;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcRmAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GridInfoDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.RmAddressStreamDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.StreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 刷新所有的全文搜索数据
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
@Service
public class TotalFtsSync {

	private static final Logger logger = LoggerFactory.getLogger(TotalFtsSync.class);

	private static final List<String> areaCodes = Arrays.asList("550", "551", "552", "553", "554", "555", "556", "557",
						  "558", "559", "561", "562", "563", "564", "566", "567");

	@Autowired
	private RmAddressStreamDao rmAddressStreamDao;

	@Autowired
	private StreamHandler<CdcRmAddrBo> streamHandler;

	@Autowired
	private GridInfoDao gridInfoDao;

	public TotalFtsSync(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 刷新所有的提示地址数据，处理步骤如下：
	 * 1. 取一个地市的areaCode
	 * 2. 调用syncSuggAddr(areaCode)方法处理一个地市的信息
	 * 3. 地市取完后结束。
	 */
	public void syncSuggAddr(){
		for(String areaCode : areaCodes) {
			syncSuggAddr(areaCode);
		}
	}

	/**
	 * 刷新所有的提示地址数据，处理步骤如下：
	 * 1.取当前地市的提示地址
	 * 2. suggFtsSvc进行插入
	 * 3. 取完后结束
	 * 
	 * @param areaCode
	 */
	public void syncSuggAddr(String areaCode){
		if (null == areaCode || areaCode.isEmpty()) {
			logger.warn("areaCode为空，无法处理");
		}

		rmAddressStreamDao.procAllSuggAddr(areaCode, streamHandler);


	}

	/**
	 * 刷新所有的提示地址数据，处理步骤类比syncSuggAddr
	 */
	public void syncUserAddr(){
		for(String areaCode : areaCodes) {
			syncUserAddr(areaCode);
		}
	}

	/**
	 * 刷新所有的用户地址数据，处理步骤如下：
	 * 1. 分页取当前地市的用户地址
	 * 2. suggFtsSvc进行插入
	 * 3. 取下一页数据
	 * 4. 取完后结束
	 * 
	 * @param areaCode
	 */
	public void syncUserAddr(String areaCode){
		if (null == areaCode || areaCode.isEmpty()) {
			logger.warn("areaCode为空，无法处理");
		}
		rmAddressStreamDao.procAllUserAddr(areaCode, streamHandler);
	}

	/**
	 * 刷新grid_points表
	 */
	public void syncGridCharacter() {
		gridInfoDao.getGridCharacter();
	}

}