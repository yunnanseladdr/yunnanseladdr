package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;

import java.util.List;

/**
 * 创建全文搜索数据服务
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 */
public interface BuildFullTextSvc<T extends CdcBaseBo> {

	/**
	 * 删除一条数据.
	 * 
	 * @param areaCode
	 * @param addrType    可接受的参数有：suggaddr, useraddr两个，其它直接返回
	 * @param id
	 */
	public void delete(String areaCode, String addrType, String id);

	/**
	 * 删除一批数据.
	 * 
	 * @param areaCode
	 * @param addrType    可接受的参数有：suggaddr, useraddr两个，其它直接返回
	 * @param ids
	 */
	public void delete(String areaCode, String addrType, List<String> ids);

	/**
	 * 保存一批用户地址，根据T的类型自动确定保存的位置
	 * 
	 * @param areaCode
	 * @param data
	 */
	public void insert(String areaCode, List<T> data);

	/**
	 * 保存一条数据，根据T的类型自动确定保存的位置
	 * 
	 * @param areaCode
	 * @param data
	 */
	public void insert(String areaCode, T data);

	/**
	 * 更新一条用户地址,必须有一个id字段，否则donothing，根据T的类型自动确定保存的位置
	 * 
	 * @param areaCode
	 * @param data
	 */
	public void update(String areaCode, T data);

	/**
	 * 更新一批用户地址，必须有id字段，否则donothing，根据T的类型自动确定保存的位置
	 * 
	 * @param areaCode
	 * @param data
	 */
	public void update(String areaCode, List<T> data);

}