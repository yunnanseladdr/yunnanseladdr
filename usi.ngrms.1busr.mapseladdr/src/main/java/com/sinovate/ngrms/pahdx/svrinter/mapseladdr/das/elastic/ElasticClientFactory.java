package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.elastic;


import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * es客户端连接工程类，用于创建es客户端连接的过程
 * @author zszhang
 * @version 1.0
 * @created 18-五月-2015 13:36:55
 */
public class ElasticClientFactory {

	private static final Logger logger = LoggerFactory.getLogger(ElasticClientFactory.class);

	/**
	 * 做一个缓存池
	 */
	private Map<String, Client> clients;
	/**
	 * 已':'分割的集群列表。格式为 clusterName:host:transport
	 * 格式错误的将不建立对应的连接
	 */
	private List<String> clusterList;

	public ElasticClientFactory(){

	}

	public void finalize() throws Throwable {
		destroy();
	}

	/**
	 * 返回指定到指定cluster的连接，若缓存中找不到，返回null
	 * 
	 * @param clusterName
	 */
	public Client getClient(String clusterName){
		if( null == clusterName || clusterName.isEmpty() )	{
			logger.warn("指定的集群名称为空，返回null");
			return null;
		}
		if( this.clients == null )
			init();
		if( this.clients == null ){
			logger.warn("ES客户端连接工厂为初始化null值，返回null");
			return null;
		}

		return this.clients.get(clusterName);
	}

	public List<String> getClusterList() {
		return clusterList;
	}

	public void setClusterList(List<String> clusterList) {
		if( null == clusterList ) {
			logger.error("设置连接串时，指定为null值");
			return;
		}
		this.clusterList = new ArrayList<String>();
		for(String str : clusterList) {
			String[] value = str.split(":");
			if( value.length != 3 || !value[2].matches("\\d+")) {
				logger.warn("连接串格式错误，应为clusterName:host:transport,实为：" + str);
				continue;
			}
			this.clusterList.add(str);
		}
	}

	public void init() {
		if( this.clients != null )	return;
		this.clients = new HashMap<String, Client>();

		for(String str : clusterList) {
			String[] value = str.split(":");
			createClient(value[0], value[1], Integer.valueOf(value[2]));
		}
	}

	private void createClient(String clusterName, String host, int port) {
		if( clusterName == null || host == null || port <= 0 || port > 65535 || clusterName.isEmpty() || host.isEmpty() ) {
			logger.warn("创建Elasticsearch客户端连接失败，原因是传入参数异常");
			return;
		}
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("client.transport.sniff", true)
				.put("cluster.name", clusterName).build();
		TransportClient client =  new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(host, port));
		clients.put(clusterName, client);
		logger.debug("创建到" + clusterName + "的客户端成功，集群中共有节点数量：" + client.listedNodes().size());
	}

	public void destroy() {
		if(this.clients != null) {
			for(Client cli : this.clients.values()) {
				cli.close();
			}
		}
	}
}