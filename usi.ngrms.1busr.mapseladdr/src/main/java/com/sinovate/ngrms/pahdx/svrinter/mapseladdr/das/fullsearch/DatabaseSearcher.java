package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch;


import java.util.List;
import java.util.Map;

/**
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:36:55
 */
public interface DatabaseSearcher {

	/**
	 * 查询库中所有的表，匹配到的结果返回到List中
	 * 
	 * @param dbname
	 * @param searchText
	 * @param pageNo
	 * @param pageSize
	 */
	public List<Map<String, Object>> search(String dbname, String searchText,int pageNo,int pageSize);

}