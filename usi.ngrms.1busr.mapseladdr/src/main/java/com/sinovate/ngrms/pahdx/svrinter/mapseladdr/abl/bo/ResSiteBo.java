package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo;


/**
 * 封装从数据库中查出来的局站信息包括：局站名称、局站id（old_id）、局站编码
 * @author john
 * @version 1.0
 * @updated 21-五月-2015 18:31:18
 */
public class ResSiteBo{

	private String siteId;

	private String siteCode;

	private String siteName;

	public ResSiteBo(){
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	@Override
	public String toString() {
		return "" + siteId +
				"," + siteName +
				"," + siteCode +
				"";
	}
}