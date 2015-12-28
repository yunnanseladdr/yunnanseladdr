package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据变化的基类
 * @author zszhang
 * @version 1.0
 * @created 21-五月-2015 18:31:45
 */
public class CdcBaseBo {

	private final static Logger logger = LoggerFactory.getLogger(CdcBaseBo.class);

	/**
	 * 默认是插入'I'
	 */
	private transient char actionFlag = 'I';
	private String tableName;
	private String id;
	private String areaCode;
	private String name;
	private String oldId;

	public CdcBaseBo(){

	}

	public void finalize() throws Throwable {

	}

	public char getActionFlag() {
		return this.actionFlag;
	}

	public void setActionFlag(char actionFlag) {
		this.actionFlag = actionFlag;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAreaCode() {
		return this.areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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