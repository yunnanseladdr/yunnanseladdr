package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 全文搜索相关的数据CUD接口
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:36:56
 */
@Component
public interface FullTextDao {

	/**
	 * 删除指定id的记录
	 * 
	 * @param dbname
	 * @param tabname
	 * @param id
	 */
	public void delete(String dbname, String tabname, String id);

	/**
	 * 删除指定id的记录
	 * 
	 * @param dbname
	 * @param tabname
	 * @param ids
	 */
	public void delete(String dbname, String tabname, List<String> ids);

	/**
	 * 保存一批数据
	 * 
	 * @param dbname
	 * @param tabname
	 * @param data
	 */
	public void insert(String dbname, String tabname, List<Map<String, Object>> data);

	/**
	 * 保存一条数据
	 * 
	 * @param dbname
	 * @param tabname
	 * @param data
	 */
	public void insert(String dbname, String tabname, Map<String, Object> data);

	/**
	 * 更新一批数据，如果本来没有该条数据，就直接新增
	 * 
	 * @param dbname
	 * @param tabname
	 * @param data
	 */
	public void update(String dbname, String tabname, List<Map<String, Object>> data);

	/**
	 * 更新一条数据，如果本来没有该条数据，就直接新增
	 * 
	 * @param dbname
	 * @param tabname
	 * @param data
	 */
	public void update(String dbname, String tabname, Map<String, Object> data);

}