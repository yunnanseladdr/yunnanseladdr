package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.AreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.StreetBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.SubAreaBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.RmAreaDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.AdminDivisionSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 *
 * 获取区域、子区域以及子区域下面的乡镇街道的类
 * 服务启动时会将区域、子区域以及乡镇街道信息加载至data集合中
 */
public class AdminDivisionSvcImpl implements AdminDivisionSvc {

	private static final Logger logger = LoggerFactory.getLogger(AdminDivisionSvcImpl.class);

/*	public List<AreaBo> getData() {
		return data;
	}*/

	//服务启动时使用init方法进行初始化
	private List<AreaBo> data;

	@Autowired
	private RmAreaDao rmAreaDao;

	public AdminDivisionSvcImpl(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 获取所有的地市信息
	 */
	public List<AreaBo> getAreas(){
		if(null != data){
			return data;
		}
		return null;
	}

	/**
	 * 取乡镇街道名称信息
	 * 查询区县（areaCode, subAreaName) 下的街道或乡镇信息，返回名称列表
	 * 1、获得子区域
	 * 2、获得街道
	 * 
	 * @param areaCode
	 * @param subAreaName
	 */
	public List<StreetBo> getStreetNames(String areaCode, String subAreaName){
		if(null == areaCode || "".equals(areaCode)){
			return null;
		}
		AreaBo key = new AreaBo();
		key.setCode(areaCode);
		int pos = Collections.binarySearch(data, key, new Comparator<AreaBo>() {
			@Override
			public int compare(AreaBo o1, AreaBo o2) {
				return o1.getCode().compareTo(o2.getCode());
			}
		});
		if (pos < 0 || pos >= data.size()) {
			logger.debug("未从区域列表data中找到指定区域AreaBo");
			return null;
		}
		List<SubAreaBo> subAreaBoList = data.get(pos).getM_SubAreaBo();
		if(null != subAreaBoList && subAreaBoList.size() > 0){
			if(null != subAreaName && !subAreaName.equals("")){
				SubAreaBo subAreaBo = new SubAreaBo();
				subAreaBo.setName(subAreaName);
				int index = Collections.binarySearch(subAreaBoList, subAreaBo, new Comparator<SubAreaBo>() {
					@Override
					public int compare(SubAreaBo o1, SubAreaBo o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				if (index < 0 || index >= subAreaBoList.size()) {
					logger.debug("未从子区域列表subAreaBoList中找到指定子区域");
					return null;
				}
				SubAreaBo subAreaBo1 = subAreaBoList.get(index);
				if(null != subAreaBo1){
					List<StreetBo> streetBoList = subAreaBo1.getM_StreetBo();
					return streetBoList;
				}
			}
		}
		return null;
	}

	/**
	 * 取区县信息接口
	 * 查询地市(areaCode)下的区县名称信息，并将名称列表返回
	 *
	 * @param areaCode
	 */
	public List<SubAreaBo> getSubAreas(final String areaCode){
		if(null == areaCode && "".equals(areaCode)){
			return null;
		}
		AreaBo key = new AreaBo();
		key.setCode(areaCode);
		int pos = Collections.binarySearch(data, key, new Comparator<AreaBo>() {
			@Override
			public int compare(AreaBo o1, AreaBo o2) {
				return o1.getCode().compareTo(o2.getCode());
			}
		});
		if (pos < 0 || pos >= data.size()) {
			logger.debug("未从区域列表data中找到指定区域AreaBo");
			return null;
		}
		return data.get(pos).getM_SubAreaBo();
	}

	/**
	 * 从数据库中读取所有的子区域、街道等信息到内存中
	 */
	public void init(){
		//获得区域信息
		data = rmAreaDao.queryArea();
		data.sort(new Comparator<AreaBo>() {
			@Override
			public int compare(AreaBo o1, AreaBo o2) {
				return o1.getCode().compareTo(o2.getCode());
			}
		});
		if(null != data && data.size() > 0){
			for(AreaBo a:data){
				BigInteger areaId = a.getId();
				String name = a.getName();
				//获得子区域及乡镇街道信息
				List<SubAreaBo> subAreaBoList = rmAreaDao.querySubArea(areaId,name);
				if(null != subAreaBoList && subAreaBoList.size() > 0){
					subAreaBoList.sort(new Comparator<SubAreaBo>() {
						@Override
						public int compare(SubAreaBo o1, SubAreaBo o2) {
							return o1.getName().compareTo(o2.getName());
						}
					});
					a.setM_SubAreaBo(subAreaBoList);
				}
			}
		}

	}

	@Override
	public BigInteger getAreaId(String areaCode) {
		if(null != areaCode && !areaCode.isEmpty()){
			if(null != data && data.size() > 0){
				BigInteger id = null;
				for(AreaBo a:data){
					if(a.getCode().equals(areaCode)){
						id = a.getId();
						break;
					}
				}
				return  id;
			}
		}
		return null;
	}

}