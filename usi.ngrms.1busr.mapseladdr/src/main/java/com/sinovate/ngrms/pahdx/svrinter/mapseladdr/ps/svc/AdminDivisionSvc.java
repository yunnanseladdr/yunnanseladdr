package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.StreetBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.SubAreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.AreaBo;

import java.math.BigInteger;
import java.util.List;

/**
 * 行政区划查询服务
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 */
public interface AdminDivisionSvc {

	/**
	 * 获取所有的地市信息
	 */
	public List<AreaBo> getAreas();

	/**
	 * 取乡镇街道名称信息
	 * 查询区县（areaCode, subAreaName) 下的街道或乡镇信息，返回名称列表
	 * 
	 * @param areaCode
	 * @param subAreaName
	 */
	public List<StreetBo> getStreetNames(String areaCode, String subAreaName);

	/**
	 * 取区县信息接口
	 * 查询地市(areaCode)下的区县名称信息，并将名称列表返回
	 *
	 * @param areaCode
	 */
	public List<SubAreaBo> getSubAreas(String areaCode);

	/**
	 * 从数据库中读取所有的子区域、街道等信息到内存中
	 */
	public void init();


	public BigInteger getAreaId(String areaCode);

}