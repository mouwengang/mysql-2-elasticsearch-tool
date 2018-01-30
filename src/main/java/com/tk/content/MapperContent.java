package com.tk.content;

import java.util.Properties;

/**
 * 〈功能简述〉该类用于存放解析配置文件的信息
 * @Credited 2016年10月24日 下午3:48:47
 */
public class MapperContent {

	public static final String CLUSTER_NAME = "clusterName";
	public static final String CLUSTER_NODES= "clusterNodes";
	
	public static final String MYSQL_USERNAME = "username";
	public static final String MYSQL_PASSWORD = "password";
	
	public static final String MYSQL_URL = "url";
	public static final String DBNAME = "dbname";
	private String selectSql = "sql";
	private String index = "index";
	private String type = "type";
	private Integer producerNum = 1;
	private Integer consumerNum = 1;
	private String[] dataColumns = {};
	
	public Integer getProducerNum() {
		return producerNum;
	}
	public void setProducerNum(Integer producerNum) {
		this.producerNum = producerNum;
	}
	public Integer getConsumerNum() {
		return consumerNum;
	}
	public void setConsumerNum(Integer consumerNum) {
		this.consumerNum = consumerNum;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getSelectSql() {
		return selectSql;
	}
	public void setSelectSql(String selectSql) {
		this.selectSql = selectSql;
	}
	//连接信息
	private Properties connect_prop =  new Properties();
	
	
	
	public Properties getConnect_prop() {
		return connect_prop;
	}
	public void setConnect_prop(Properties connect_prop) {
		this.connect_prop = connect_prop;
	}
	public String[] getDataColumns() {
		return dataColumns;
	}
	public void setDataColumns(String[] dataColumns) {
		this.dataColumns = dataColumns;
	}
}
