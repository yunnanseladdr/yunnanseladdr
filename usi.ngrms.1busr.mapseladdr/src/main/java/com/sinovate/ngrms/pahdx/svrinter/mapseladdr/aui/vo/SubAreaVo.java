package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 子区域（区县）信息vo类
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
public class SubAreaVo {

	private int id;
	private String name;

	public SubAreaVo(){

	}

	public void finalize() throws Throwable {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}