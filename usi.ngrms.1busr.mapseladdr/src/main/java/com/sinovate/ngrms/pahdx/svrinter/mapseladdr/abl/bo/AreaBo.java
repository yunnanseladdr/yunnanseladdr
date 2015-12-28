package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo;


import java.util.List;

/**
 * 区域信息
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:38:34
 */
public class AreaBo extends BaseBo {


	public List<SubAreaBo> m_SubAreaBo;

	public AreaBo(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public List<SubAreaBo> getM_SubAreaBo() {
		return m_SubAreaBo;
	}

	public void setM_SubAreaBo(List<SubAreaBo> m_SubAreaBo) {
		this.m_SubAreaBo = m_SubAreaBo;
	}
}