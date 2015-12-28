package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.support;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实现从areaCode到dbname的转换
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 */
public class DatabaseNameConverter {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseNameConverter.class);

	private final String dbNamePrefix;

	public DatabaseNameConverter(String dbNamePrefix){
		this.dbNamePrefix = dbNamePrefix;
	}

	public void finalize() throws Throwable {

	}

	/**
	 * 从areaCode得到DbName
	 * 
	 * @param areaCode
	 */
	public String getDbName(String areaCode){
		if(null == areaCode || areaCode.equals("")){
			return null;
		}
		return this.dbNamePrefix + areaCode;
	}

}