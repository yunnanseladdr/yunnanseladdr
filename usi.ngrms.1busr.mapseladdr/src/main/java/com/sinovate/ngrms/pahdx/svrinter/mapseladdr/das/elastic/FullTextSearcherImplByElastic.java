package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.elastic;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch.FullTextSearcher;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.aws.AddressSegmenter;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 全文搜索引擎接口的es实现类
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:36:56
 */
public class FullTextSearcherImplByElastic implements FullTextSearcher {

	private static final Logger logger = LoggerFactory.getLogger(FullTextSearcherImplByElastic.class);

	@Autowired
	private Client esClient;

	@Autowired
	private AddressSegmenter addressSegmenter;

	public FullTextSearcherImplByElastic(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	private String highlightedfield;

	public void setHighlightedfield(String highlightedfield) {
		this.highlightedfield = highlightedfield;
	}

	private String weightFieldList;

	public String getWeightFieldList() {
		return weightFieldList;
	}

	public void setWeightFieldList(String weightFieldList) {
		this.weightFieldList = weightFieldList;
	}

	/**
	 * 查询库中所有的表，匹配到的结果返回到List中；
	 * 
	 * @param dbname
	 * @param tablename
	 * @param searchText
	 * @param pageNo
	 * @param pageSize
	 */
	public List<Map<String, Object>> search(String dbname, String tablename, String searchText, int pageNo,int pageSize){
		logger.debug("输入参数：dbname("+dbname+") tablename(" + tablename + ") searchText(" + searchText + ") + pageNo(" + pageNo + ") pageSize(" + pageSize + ")" );
		if( pageNo > 10 ) {
			logger.warn("请求搜索10页以后的数据，不做查询直接返回！目的是为了防止对服务形成DOS攻击，并且翻页太多无利于工作！应考虑输入更多查询关键字！");
			return null;
		}
		if( null == dbname || null == tablename || null == searchText || dbname.isEmpty() || tablename.isEmpty() || searchText.isEmpty() ) {
			logger.warn("输入参数中的dbname、tablename、searchText有空值，直接返回null");
			return null;
		}

		String actualSearchText = addressSegmenter.splitAddr(searchText);
		logger.debug("实际搜索字符串为："+actualSearchText);
		QueryBuilder qb =multiMatchQuery(actualSearchText,this.weightFieldList.trim().split(",")).type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
		SearchRequestBuilder srb = esClient.prepareSearch(dbname).setTypes(tablename).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		srb.setFrom(pageNo*pageSize - pageSize).setSize(pageSize);
		srb.setQuery(qb).addHighlightedField(this.highlightedfield);
		SearchResponse response = srb.execute().actionGet();
		List<Map<String, Object>> data = getSearchResult(response);
		//data.forEach(e->addressSegmenter.recoverAddrData(e));
		logger.debug("返回结果为：" + data);
		return data;
	}

	/**
	 * 查询库中所有的表，匹配到的结果返回到List中
	 * 
	 * @param dbname
	 * @param searchText
	 * @param pageNo
	 * @param pageSize
	 */
	public List<Map<String, Object>> search(String dbname, String searchText, int pageNo,int pageSize){
		return null;
	}


	private List<Map<String, Object>> getSearchResult(SearchResponse searchResponse) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, HighlightField> highlightFieldMap = null;
		HighlightField highlightField = null;
		Text[] texts = null;
		for (SearchHit hit : searchResponse.getHits()) {
			Map<String, Object> data = new HashMap<>();
			data.putAll(hit.getSource());
			highlightFieldMap = hit.getHighlightFields();
			if( null == highlightFieldMap )	continue;
			highlightField = highlightFieldMap.get("name");
			if( null == highlightField ) continue;
			texts = highlightField.fragments();
			if( null == texts || null == texts[0] )	continue;
			data.put("highlightText",texts[0].toString());
			resultList.add(data);
		}

		return resultList;
	}

}