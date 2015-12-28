package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Po基类信息
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:38:34
 */
public class BaseBo {



	/**
	 * 编码
	 */
	private String code;
	/**
	 * 统一模型gid
	 */
	private BigInteger id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 资源生产库id
	 */
	private String oldId;

	public BaseBo(){

	}

	public void finalize() throws Throwable {

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}
}