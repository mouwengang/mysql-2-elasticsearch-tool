package com.tk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tk.content.Globals;
import com.tk.es.Consumer;
import com.tk.mysql.Producer;
import com.tk.parse.ComfigurationParser;

import ch.qos.logback.classic.Logger;

/**
 * 
 * 〈功能简述〉工具的入口
 * @Credited 2018年01月24日 下午4:46:46
 */
public class Application {
	
	private static Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(Application.class);
	
	private static String location = "D:\\itw_muwg\\store_v2\\mysql-2-elasticsearch-tool\\document\\mysql-2-elasticsearch.xml";
	public static void main(String[] args) throws InterruptedException {
		//开始时间
		long start = System.currentTimeMillis();
		logger.info("init environment...");
		
		//解析配置文件,并初始化环境
		ComfigurationParser.parse(location);
		logger.info("start transfer data from mysql to elasticsearch...");
		
		//初始化spring上下文
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		//开始执行数据迁移
		TransportClient transportClient = (TransportClient) context.getBean("transportClient");
		JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
		//查询sql
		String selectSql = Globals.context.getSelectSql();
		//截取from后面的sql
		int indexOf = selectSql.indexOf("from");
		String fromSql = selectSql.substring(indexOf, selectSql.length()-1);
		String sql = "select count(*) "+fromSql;
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
		
		int totalPage = count % Globals.pageSize == 0 ? count / Globals.pageSize : count / Globals.pageSize + 1;
		
		createThread(transportClient, jdbcTemplate, totalPage);
		//结束时间
		long end = System.currentTimeMillis();
		long result = end-start;
		logger.info("transfer over daixianghuang in "+result/1000+" seconds"+" ,total :\t"+ count+" record.");
	}
	
	private static void createThread(TransportClient transportClient, JdbcTemplate jdbcTemplate, int totalPage)
			throws InterruptedException {
		// 声明一个容量为100的缓存队列
        BlockingQueue<List<Map<String, Object>>> queue = new LinkedBlockingQueue<List<Map<String, Object>>>(100);
        //存储生产者线程
        List<Producer> producers = new ArrayList<Producer>();
        //存储消费者线程
        List<Consumer> consumers = new ArrayList<Consumer>();
 
        // 借助Executors
        // 启动线程
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 1; i <= Globals.context.getProducerNum(); i++) {
        	Producer producer = new Producer(queue,"生产者_"+i,jdbcTemplate,totalPage);
        	producers.add(producer);
        	service.execute(producer);
		}
        for (int i = 1; i <= Globals.context.getConsumerNum(); i++) {
        	Consumer consumer = new Consumer(queue,"消费者_"+i,transportClient);
        	consumers.add(consumer);
        	service.execute(consumer);
        }
        //判断所有线程 都是否结束
        boolean flag = false;
        while(!flag){
        	flag = true;
        	for (Producer producer : producers) {
        		if(producer.isRunning())
        			flag = false;
        	}
        	for (Consumer consumer : consumers) {
        		if(consumer.isRunning())
        			flag = false;
        	}
        	Thread.sleep(1000);
        }
        //线程都结束了，关闭线程池
        service.shutdown();
	}
	
	

}
