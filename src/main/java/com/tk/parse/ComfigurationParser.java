package com.tk.parse;

import java.io.File;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tk.content.Globals;
import com.tk.content.MapperContent;

/**
 * 〈功能简述〉该类主要用于解析用户提供的映射文件
 *  itw_muwg
 */
public class ComfigurationParser {

	private static Logger logger = LoggerFactory.getLogger(ComfigurationParser.class);
	public static String ESSOURCE = "elasticsearch";
	public static String MYSQLSOURCE = "mysqlsource";
	public static String THREADSOURCE = "threadsource";
	
	//配置文件解析完之后将解析结果存入content中
	private static MapperContent content = Globals.context;
	
	/**
	 * 解析用户配置文件
	 * @param location
	 */
	public static void parse(String location){
		File resource = new File(location);
		if(resource.exists()){
			SAXReader sax=new SAXReader();//创建一个SAXReader对象 
			try {
				Document read = sax.read(resource);
				
				//获取根节点
				Element rootElement = read.getRootElement();
				
				//获取elasticsearch节点并解析
				Element esElement = rootElement.element(ESSOURCE);
				parseEssource(esElement);
				
				//获取mysqlsource节点并解析
				Element mysqlElement = rootElement.element(MYSQLSOURCE);
				parseMysqlsource(mysqlElement);
				
				//获取threadsource节点并解析
				Element threadElement = rootElement.element(THREADSOURCE);
				parseThreadsource(threadElement);
				
			} catch (DocumentException e) {
				StackTraceElement stackTraceElement = e.getStackTrace()[0];
				logger.error("配置文件解析错误..");
				logger.error(stackTraceElement.toString());
			}
		}
	}
	/**
	 * 解析线程个数配置
	 * @param threadElement
	 */
	private static void parseThreadsource(Element ele) {
		Element producerNum = ele.element("producerNum");
		Element consumerNum = ele.element("consumerNum");
		//判断elasticsearch配置是否齐全
		if(producerNum == null || consumerNum == null ){
			logger.error("the property [producerNum] or [consumerNum]  for thread hava not specify");
		}else {
				String producerNumValue = producerNum.getStringValue();
				String consumerNumValue = consumerNum.getStringValue();
				content.setProducerNum(Integer.valueOf(producerNumValue));
				content.setConsumerNum(Integer.valueOf(consumerNumValue));
		}
	}

	/**
	 * 解析mongosource标签及其子标签
	 */
	private static void parseEssource(Element ele){
		Element clusterName = ele.element("clusterName");
		Element clusterNodes = ele.element("clusterNodes");
		Element index = ele.element("index");
		Element type = ele.element("type");
		Properties connect_prop = content.getConnect_prop();
		//判断elasticsearch配置是否齐全
		if(clusterName == null || clusterNodes == null || index == null || type == null){
			logger.error("the property [clusterName],[clusterNodes],[index] or [type]  for elasticsearch hava not specify");
		}else {
				String clusterNameValue = clusterName.getStringValue();
				String clusterNodesValue = clusterNodes.getStringValue();
				String indexValue = index.getStringValue();
				String typeValue = type.getStringValue();
				connect_prop.setProperty(MapperContent.CLUSTER_NAME, clusterNameValue);
				connect_prop.setProperty(MapperContent.CLUSTER_NODES, clusterNodesValue);
				content.setIndex(indexValue);
				content.setType(typeValue);
		}
	}
	
	/**
	 * 解析mysqlsource标签及其子标签
	 */
	private static void parseMysqlsource(Element ele){
		Properties connect_prop = content.getConnect_prop();
		Element url = ele.element("url");
		if(url!=null){
			String urlValue = url.getStringValue();
			connect_prop.setProperty(MapperContent.MYSQL_URL, urlValue);
		}
		Element username = ele.element("username");
		if(username!=null){
			String usernameValue = username.getStringValue();
			connect_prop.setProperty(MapperContent.MYSQL_USERNAME, usernameValue);
		}
		Element password = ele.element("password");
		if(password!=null){
			String passwordValue = password.getStringValue();
			connect_prop.setProperty(MapperContent.MYSQL_PASSWORD, passwordValue);
		}
		Element sql = ele.element("sql");
		if(sql!=null){
			String sqlValue = sql.getStringValue();
			content.setSelectSql(sqlValue);
		}
		//date列
		Element datecolumns = ele.element("datecolumns");
		if(datecolumns!=null){
			String datecolumnsValue = datecolumns.getStringValue();
			String[] split = datecolumnsValue.split(",");
			content.setDataColumns(split);
		}
	}
}
