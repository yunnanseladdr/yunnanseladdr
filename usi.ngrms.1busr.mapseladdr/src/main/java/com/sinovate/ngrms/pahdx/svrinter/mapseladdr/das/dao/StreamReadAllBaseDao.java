package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;

import java.math.BigInteger;

/**
 * 逐页读取所有数据，并暴露对每页数据的接口。
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:36:56
 */
public interface StreamReadAllBaseDao<T extends CdcBaseBo> {

	/**
	 * 根据SQL语句内容，将结果集中数据逐条封装为T类型，并交由hanler处理。
	 * 本方法采用prepareStatement , args为要绑定进去的参数。
	 * 
	 * @param sql
	 * @param regionId
	 * @param handler
	 */
	public void query(String sql, BigInteger regionId, StreamHandler<T> handler, Class<T> clazz);

	/**
	 * 根据SQL语句内容，将结果集中数据逐条封装为T类型，并交由hanler处理。要采用preparestatement，请使用另一个同名方法。
	 * 
	 * @param sql
	 * @param handler
	 */
	public void query(String sql, StreamHandler<T> handler, Class<T> clazz);

	/**
	 * 根据SQL语句内容，将结果集中数据逐条封装为T类型，并交由hanler处理。
	 * 本方法采用prepareStatement , args为要绑定进去的参数。
	 *
	 * @param sql
	 * @param maxRsId
	 * @param tableName
	 * @param handler
	 */
	public void query(String sql, String tableName, String maxRsId, StreamHandler<T> handler, Class<T> clazz);

	/**
	 * 删除操作不能放在线程里操作所以写了这个方法只是为了删除es数据用的
	 * @param sql
	 * @param tableName
	 * @param maxRsId
	 */
	public void query(String sql, String tableName, String maxRsId);


}