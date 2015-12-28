package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo;



import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.ResSiteBo;

import java.util.Set;

/**
 * 用户地址VO类
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
public class UserAddrVo {

	/**
	 * 用以在界面加量显示的字段；
	 * 信息表示为： 铁静苑[<em>张洼路</em>]， 其中em中内容为匹配到的结果 ，前台界面依次进行加亮显示。
	 */
	protected String highlightText;
	/**
	 * 该地址在统一库中的Id
	 */
	protected String id;
	/**
	 * 地址名称。
	 */
	protected String name;

	//局向信息
	private Set<ResSiteBo> sites;

	private String oldSubAreaId;

	private String subAreaName;
	public UserAddrVo(){

	}

	public UserAddrVo(String highlightText, String id, String name) {
		this.highlightText = highlightText;
		this.id = id;
		this.name = name;
	}
	public String getHighlightText() {
		return highlightText;
	}

	public void setHighlightText(String highlightText) {
		this.highlightText = highlightText;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<ResSiteBo> getSites() {
		return sites;
	}

	public void setSites(Set<ResSiteBo> sites) {
		this.sites = sites;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserAddrVo that = (UserAddrVo) o;

		if (highlightText != null ? !highlightText.equals(that.highlightText) : that.highlightText != null)
			return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (oldSubAreaId != null ? !oldSubAreaId.equals(that.oldSubAreaId) : that.oldSubAreaId != null) return false;
		if (sites != null ? !sites.equals(that.sites) : that.sites != null) return false;
		if (subAreaName != null ? !subAreaName.equals(that.subAreaName) : that.subAreaName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = highlightText != null ? highlightText.hashCode() : 0;
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (sites != null ? sites.hashCode() : 0);
		result = 31 * result + (oldSubAreaId != null ? oldSubAreaId.hashCode() : 0);
		result = 31 * result + (subAreaName != null ? subAreaName.hashCode() : 0);
		return result;
	}
}