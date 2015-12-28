package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch;


import java.util.List;
import java.util.Map;

/**
 * 以数据库来表达，就是只一个表内进行全文搜索。
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:36:56
 */
public interface TableSearcher {

	/**
	 * 查询库中所有的表，匹配到的结果返回到List中；
	 * 
	 * @param dbname
	 * @param tablename
	 * @param searchText
	 * @param pageNo
	 * @param pageSize
	 */
	public List<Map<String, Object>> search(String dbname, String tablename, String searchText, int pageNo,int pageSize);

}