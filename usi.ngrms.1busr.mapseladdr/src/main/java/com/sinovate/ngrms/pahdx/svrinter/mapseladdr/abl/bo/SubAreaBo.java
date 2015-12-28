package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo;


import java.util.List;

/**
 * 子区域(区县）信息
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:38:35
 */
public class SubAreaBo extends BaseBo {

	private List<StreetBo> m_StreetBo;

	public SubAreaBo(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public List<StreetBo> getM_StreetBo() {
		return m_StreetBo;
	}

	public void setM_StreetBo(List<StreetBo> m_StreetBo) {
		this.m_StreetBo = m_StreetBo;
	}
}