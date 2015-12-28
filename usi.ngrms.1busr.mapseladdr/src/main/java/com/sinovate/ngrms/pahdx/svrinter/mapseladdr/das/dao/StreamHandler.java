package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;

/**
 * 用于对发送来的数据进行操作、自动输出。
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:36:56
 */
public interface StreamHandler<T extends CdcBaseBo> {

	/**
	 * 调用此方法，通知StreamHandler所有数据已经发送完。
	 */
	public void gameOver();

	/**
	 * 想流中增加一条数据
	 * 
	 * @param obj
	 */
	public void handle(T obj);

}