package com.tk.mysql;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jdbc.core.JdbcTemplate;

import com.tk.content.Globals;

import ch.qos.logback.classic.Logger;

/**
 * 生产者线程
 */
public class Producer implements Runnable {
	
	private static Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(Producer.class);
    private volatile boolean      isRunning               = true;
    private BlockingQueue<List<Map<String, Object>>> queue;
    private static AtomicInteger  count                   = new AtomicInteger();
    private static final int      DEFAULT_RANGE_FOR_SLEEP = 1000;
    private String name;
    private JdbcTemplate jdbcTemplate;
	private int totalPage;
 
    public Producer(BlockingQueue<List<Map<String, Object>>> queue, String name, JdbcTemplate jdbcTemplate,
			int totalPage) {
		super();
		this.queue = queue;
		this.name = name;
		this.jdbcTemplate = jdbcTemplate;
		this.totalPage = totalPage;
	}

	public void run() {
        Random r = new Random();
        try {
            while (isRunning ) {
//                Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));
                if(count.get() < totalPage){
                	int page = count.incrementAndGet();
                	int pageIndex = (page - 1) * Globals.pageSize;
                	String sql = Globals.context.getSelectSql()+"LIMIT " + pageIndex + " , " + Globals.pageSize + " ";
                	List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
                	if (!queue.offer(list, 2, TimeUnit.SECONDS)) {
                		logger.info("放入数据失败：第" + page+"页");
                	}else{
                		logger.info("线程"+name+"插入第"+page+"页，"+list.size()+"条数据成功！");
                	}
                }else{
                	isRunning = false;
                }
            }
        } catch (InterruptedException e) {
        	logger.error("thead interrupt !", e);
        	isRunning = false;
            Thread.currentThread().interrupt();
        } finally {
        	logger.info("producer: "+name+" thead closed !");
        }
    }
    
	public boolean isRunning() {
		return isRunning;
	}

	public void stop() {
        isRunning = false;
    }
 }