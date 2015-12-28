package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch.FullTextDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.svc.BuildFullTextSvc;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcBaseBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo.CdcRmAddrBo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.support.DatabaseNameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:37:44
 */
@Service
public class BuildFullTextSvcImpl<T extends CdcBaseBo> implements BuildFullTextSvc<T> {

	private static final Logger logger = LoggerFactory.getLogger(BuildFullTextSvcImpl.class);

	@Autowired
	private FullTextDao fullTextDao;

	@Autowired
	DatabaseNameConverter databaseNameConverter;

	public BuildFullTextSvcImpl(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 删除一批数据.
	 * 
	 * @param areaCode
	 * @param addrType    可接受的参数有：SuggAddr, UserAddr两个，其它直接返回
	 * @param ids
	 */
	public void delete(String areaCode, String addrType, List<String> ids){
		if(null == areaCode || areaCode.equals("")){
			logger.warn("区域编码为空，未删除任何记录，直接返回!");
			return ;
		}
		String dbName = databaseNameConverter.getDbName(areaCode);
		if(null == dbName || dbName.equals("")){
			logger.warn("通过区域编码："+areaCode+"获取dbname失败，直接返回!");
			return;
		}
		fullTextDao.delete(dbName,addrType,ids);
	}

	/**
	 * 删除一条数据.
	 * 
	 * @param areaCode
	 * @param addrType    可接受的参数有：suggaddr, useraddr两个，其它直接返回
	 * @param id
	 */
	public void delete(String areaCode, String addrType, String id){
		if(null == areaCode || areaCode.equals("")){
			logger.warn("区域编码为空，未删除任何记录，直接返回!");
			return ;
		}
		String dbName = databaseNameConverter.getDbName(areaCode);
		if(null == dbName || dbName.equals("")){
			logger.warn("通过区域编码："+areaCode+"获取dbname失败，直接返回!");
			return;
		}
		fullTextDao.delete(dbName,addrType,id);

	}

	/**
	 * 保存一条数据，根据T的类型自动确定保存的位置
	 * 
	 * @param areaCode
	 * @param data
	 */
	public void insert(String areaCode, T data){

		if(areaCode == null || areaCode.isEmpty()) {
			logger.warn("区域编码为空，未插入任何记录，直接返回!");
			return ;
		}

		String dbname = databaseNameConverter.getDbName(areaCode);
		if(null == dbname || dbname.isEmpty()) {
			logger.warn("通过区域编码："+areaCode+"获取dbname失败，直接返回!");
			return;
		}

		String tabname = data.getTableName();
		int tabtype = getTableType(tabname);
		Map<String, Object> map = data2Map(data,tabtype);

		fullTextDao.update(dbname,tabname,map);
	}

	/**
	 * 保存一批用户地址，根据T的类型自动确定保存的位置
	 * 
	 * @param areaCode
	 * @param data
	 */
	public void insert(String areaCode, List<T> data){

		if(null == areaCode || areaCode.isEmpty() || null == data || data.isEmpty() ) {
			logger.warn("区域编码为空，未插入任何记录，直接返回!");
			return ;
		}

		String dbname = databaseNameConverter.getDbName(areaCode);
		if(null == dbname || dbname.isEmpty()) {
			logger.warn("通过区域编码："+areaCode+"获取dbname失败，直接返回!");
			return;
		}

		String tabname = data.get(0).getTableName();
		int tabtype = getTableType(tabname);

		// 调用批量插入；注意，对list中每个元素进行转换，要注意参照ES表结构。
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(data.size());
		data.forEach(e->result.add(data2Map(e, tabtype)));

		fullTextDao.insert(dbname,tabname, result);
	}

	/**
	 * 更新一批用户地址，必须有id字段，否则donothing，根据T的类型自动确定保存的位置
	 * 
	 * @param areaCode
	 * @param data
	 */
	public void update(String areaCode, List<T> data){

		if(null == areaCode || areaCode.equals("")) {
			logger.warn("区域编码为空，未修改任何记录，直接返回!");
			return ;
		}

		String dbname = databaseNameConverter.getDbName(areaCode);
		if(null == dbname || dbname.equals("")) {
			logger.warn("通过区域编码："+areaCode+"获取dbname失败，直接返回!");
			return;
		}

		String tabname = data.get(0).getTableName();
		int tabtype = getTableType(tabname);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(data.size());
		data.forEach(e->result.add(data2Map(e, tabtype)));

		fullTextDao.update(dbname, tabname, result);
	}

	private int getTableType(String tabname) {
		if ( tabname.equals("SuggAddr")) return 1;
		else if( tabname.equals("UserAddr")) return 0;
		return -1;
	}

	private Map<String, Object> data2Map(T data, int tabtype) {
		Map<String, Object> m = null;
		switch (tabtype	) {
			case 1: // SuggAddr
				m = new HashMap<String, Object>();
				m.put("id", ((CdcRmAddrBo) data).getId());
				m.put("name", ((CdcRmAddrBo) data).getName());
				m.put("oldid", ((CdcRmAddrBo) data).getOldId());
				m.put("subareaname", ((CdcRmAddrBo) data).getSubAreaName());
				m.put("subnames",((CdcRmAddrBo) data).getSubnames());
				m.put("namebak",((CdcRmAddrBo) data).getNameBak());
				return m;
			case 0: // UserAddr;
				m = new HashMap<String, Object>();
				m.put("id", ((CdcRmAddrBo) data).getId());
				m.put("name", ((CdcRmAddrBo) data).getName());
				m.put("oldid", ((CdcRmAddrBo) data).getOldId());
				m.put("subareaname", ((CdcRmAddrBo) data).getSubAreaName());
				m.put("oldsubareaid", ((CdcRmAddrBo) data).getOldSubAreaId());
				m.put("sites",((CdcRmAddrBo) data).getSites());
				return m;
			default:
				logger.error("表类型出错！！！1");
				return null;
		}
	}
	/**
	 * 更新一条用户地址,必须有一个id字段，否则donothing，根据T的类型自动确定保存的位置
	 * 
	 * @param areaCode
	 * @param data
	 */
	public void update(String areaCode, T data){

		if(null == areaCode || areaCode.equals("")) {
			logger.warn("区域编码为空，未修改任何记录，直接返回!");
			return ;
		}

		String dbname = databaseNameConverter.getDbName(areaCode);
		if(null == dbname || dbname.equals("")) {
			logger.warn("通过区域编码："+areaCode+"获取dbname失败，直接返回!");
			return;
		}

		String tabname = data.getTableName();
		int tabtype = getTableType(tabname);
		Map<String, Object> map = data2Map(data,tabtype);

		fullTextDao.update(dbname,tabname,map);
	}
}