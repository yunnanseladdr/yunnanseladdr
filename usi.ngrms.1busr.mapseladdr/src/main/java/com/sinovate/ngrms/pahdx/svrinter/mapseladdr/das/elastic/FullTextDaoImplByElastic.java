package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.elastic;


import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.fullsearch.FullTextDao;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.aws.AddressSegmenter;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:36:56
 */
@Service("fullTextDaoImplByElastic")
public class FullTextDaoImplByElastic implements FullTextDao {

	private static final Logger logger = LoggerFactory.getLogger(FullTextDaoImplByElastic.class);

	private int maxBatchSize = 1000;

	@Autowired
	private Client esClient;

	@Autowired
	private AddressSegmenter addressSegmenter;

	public FullTextDaoImplByElastic() {
	}

	public FullTextDaoImplByElastic(Client esClient){
		this.esClient = esClient;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * 删除指定id的记录
	 * 
	 * @param dbname
	 * @param tabname
	 * @param ids
	 */
	public void delete(String dbname, String tabname, List<String> ids){
		if(null == dbname || null == tabname || null == ids || ids.isEmpty() || dbname.isEmpty() || tabname.isEmpty() ) {
			logger.warn("删除索引数据时，输入参数不能为空！");
			return;
		}

		for(int from=0,to; from<ids.size(); from+=maxBatchSize) {
			to = from + maxBatchSize > ids.size() ? ids.size() : from + maxBatchSize;
			batch_delete(dbname, tabname, ids.subList(from, to));
		}
	}

	private void batch_delete(String dbname, String tabname, List<String> ids) {
		BulkRequestBuilder bulkRequest = esClient.prepareBulk();
		ids.forEach(id->bulkRequest.add(esClient.prepareDelete(dbname,tabname,id)));
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			logger.warn("批量删除发生失败");
			// todo> 以后可以在此处做一些异常补救措施或者详细提示。
		}
	}
	/**
	 * 删除指定id的记录
	 * 
	 * @param dbname
	 * @param tabname
	 * @param id
	 */
	public void delete(String dbname, String tabname, String id){
		if(null == dbname || null == tabname || null == id || id.isEmpty() || dbname.isEmpty() || tabname.isEmpty() ) {
			logger.warn("删除索引数据时，输入参数不能为空！");
			return;
		}
		DeleteResponse response = esClient.prepareDelete(dbname,tabname,id).execute().actionGet();
		/*if( !response.isFound() ) {
			logger.warn("删除时未找到指定索引,type:" +tabname + " id:" +id);
		}*/
	}


	/**
	 * 保存一条数据
	 * 
	 * @param dbname
	 * @param tabname
	 * @param data
	 */
	public void insert(String dbname, String tabname, Map<String, Object> data){
		if(null == dbname || null == tabname || null == data || data.isEmpty() || dbname.isEmpty() || tabname.isEmpty() ) {
			logger.warn("创建索引数据时，输入参数不能为空！");
			return;
		}
		String id = (String) data.get("id");
		addressSegmenter.splitAddrData(data);
		IndexResponse response = null;
		if( null == id )
			response = esClient.prepareIndex(dbname,tabname).setSource(data).execute().actionGet();
		else
			response = esClient.prepareIndex(dbname,tabname,id).setSource(data).execute().actionGet();
		if( !response.isCreated()) {
			logger.warn("创建索引数据出错 index:" + dbname + " type:" + tabname + " data:" + data);
		}
	}

	/**
	 * 保存一批数据
	 * 
	 * @param dbname
	 * @param tabname
	 * @param data
	 */
	public void insert(String dbname, String tabname, List<Map<String, Object>> data){
		if(null == dbname || null == tabname || null == data || data.isEmpty() || dbname.isEmpty() || tabname.isEmpty() ) {
			logger.warn("创建索引数据时，输入参数不能为空！");
			return;
		}
		for(int from=0,to; from<data.size(); from+=maxBatchSize) {
			to = from + maxBatchSize > data.size() ? data.size() : from + maxBatchSize;
			batch_insert(dbname, tabname, data.subList(from, to));
		}
	}

	private void batch_insert(String dbname, String tabname, List<Map<String, Object>> data){
		if (esClient == null) {
			logger.debug("esclient is null");
		}
		BulkRequestBuilder bulkRequest = esClient.prepareBulk();
		// 写入前先分词
		data.forEach(e->{addressSegmenter.splitAddrData(e);bulkRequest.add(esClient.prepareIndex(dbname, tabname, (String) e.get("id")).setSource(e));});
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			logger.warn("批量创建索引发生失败");
			// todo> 以后可以在此处做一些异常补救措施或者详细提示。
		}

	}

	/**
	 * 更新一条数据，如果本来没有该条数据，就直接新增
	 * 
	 * @param dbname
	 * @param tabname
	 * @param data
	 */
	public void update(String dbname, String tabname, Map<String, Object> data){
		if( null == data || data.isEmpty() || data.get("id") == null ) {
			logger.warn("更新索引数据时，对象必须的id属性必须有值！");
			return;
		}
		this.insert(dbname, tabname, data);
	}

	/**
	 * 更新一批数据，如果本来没有该条数据，就直接新增
	 * 
	 * @param dbname
	 * @param tabname
	 * @param data
	 */
	public void update(String dbname, String tabname, List<Map<String, Object>> data){
		if( null == data || data.isEmpty() ) {
			logger.warn("更新索引数据时，对象列表不能为空");
			return;
		}
		logger.info("批量更新时，将放弃所有没有属性为id的数据！！");
		List<Map<String, Object>> validData = (List<Map<String, Object>>) data.stream().filter(x -> x.get("id")!=null);
		this.insert(dbname, tabname, validData);
	}
}