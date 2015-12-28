package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提示地址Vo类
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:39:06
 */
public class SuggestiveAddrVo{

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
	 * 地址信息。
	 */
	protected String name;

	/**
	 * 下级名称
	 */
	private String subNames;

	private String nameBak;

	public SuggestiveAddrVo() {
	}

	public SuggestiveAddrVo(String hightLightText, String id, String name) {
		this.highlightText = highlightText;
		this.id = id;
		this.name = name;
	}


	public void finalize() throws Throwable {
		super.finalize();
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

	public String getSubNames() {
		return subNames;
	}

	public void setSubNames(String subNames) {
		this.subNames = subNames;
	}

	public String getNameBak() {
		return nameBak;
	}

	public void setNameBak(String nameBak) {
		this.nameBak = nameBak;
	}
}