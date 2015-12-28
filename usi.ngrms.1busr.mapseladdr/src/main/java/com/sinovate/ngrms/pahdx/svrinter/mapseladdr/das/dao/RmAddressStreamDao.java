package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcRmAddrBo;

/**
 * 地址获取Dao
 * @author shen
 * @version 1.0
 * @created 18-五月-2015 13:36:56
 */
public interface RmAddressStreamDao {

	/**
	 * 从库表中取出所有的SuggAddr地址, 并逐条调用handler进行处理。
	 * 处理步骤：
	 * 1. 创建获取数据的SQL语句（或从配置文件读取）；
	 * 2. 调用StreamReadAllBaseDao.query方法进行处理，传入handler.
	 * 
	 * @param handler
	 */
	public void procAllSuggAddr(String areaCode,StreamHandler<CdcRmAddrBo> handler);

	/**
	 * 从库表中取出所有的UserAddr地址, 并逐条调用handler进行处理。
	 * 处理步骤：
	 * 1. 创建获取数据的SQL语句（或从配置文件读取）；
	 * 2. 调用StreamReadAllBaseDao.query方法进行处理，传入handler.
	 * 
	 * @param handler
	 */
	public void procAllUserAddr(String areaCode,StreamHandler<CdcRmAddrBo> handler);

}