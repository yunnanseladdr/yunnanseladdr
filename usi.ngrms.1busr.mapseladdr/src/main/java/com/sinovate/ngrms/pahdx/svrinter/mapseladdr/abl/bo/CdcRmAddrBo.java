package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo;

/**
 * @单位名称：科大国创—电信资源事业部 USTC Sinovate Software Co.,Ltd. Copyright (c) 2014 All
 * Rights Reserved.
 * @系统名称：NGRMS—下一代网络资源管理系统
 * @工程名称：usi.ngrms.allparent
 * @文件名称:
 * @类路径: com.sinovate.ngrms.pahdx.svrinter.seladdr.abl.bo
 * @date 2015-05-20 21:42
 * @desc
 * @see
 * @author tu.chenghong@ustcinfo.com
 * @version 1.0
 * @updated 21-五月-2015 18:34:55
 */
public class CdcRmAddrBo extends CdcBaseBo {

	//九级地址
	private String subnames;
	//子区域oldId
	private String oldSubAreaId;
	//子区域名称
	private String subAreaName;
	//地址名称
	private String nameBak;
	//局向信息
	private String sites;

	public String getSubnames() {
		return subnames;
	}

	public void setSubnames(String subnames) {
		this.subnames = subnames;
	}

	public CdcRmAddrBo(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getOldSubAreaId() {
		return oldSubAreaId;
	}

	public void setOldSubAreaId(String oldSubAreaId) {
		this.oldSubAreaId = oldSubAreaId;
	}

	public String getSubAreaName() {
		return subAreaName;
	}

	public void setSubAreaName(String subAreaName) {
		this.subAreaName = subAreaName;
	}

	public String getNameBak() {
		return nameBak;
	}

	public void setNameBak(String nameBak) {
		this.nameBak = nameBak;
	}

	public String getSites() {
		return sites;
	}

	public void setSites(String sites) {
		this.sites = sites;
	}
}