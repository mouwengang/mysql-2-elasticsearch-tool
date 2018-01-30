package com.tk.es;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.tk.content.Globals;
import com.tk.util.DateUtil;

import ch.qos.logback.classic.Logger;

/**
 * 消费者线程
 * 
 * 
 */
public class Consumer implements Runnable {
 
	private static Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(Consumer.class);
    private BlockingQueue<List<Map<String, Object>>> queue;
    private static final int      DEFAULT_RANGE_FOR_SLEEP = 1000;
    private volatile boolean      isRunning               = true;
    private String name;
    private TransportClient client;

    
    public Consumer(BlockingQueue<List<Map<String, Object>>> queue, String name, TransportClient client) {
		super();
		this.queue = queue;
		this.name = name;
		this.client = client;
	}

	@Override
    public void run() {
        Random r = new Random();
        try {
            while (isRunning) {
                List<Map<String, Object>> list = queue.poll(5, TimeUnit.SECONDS);
                if (null != list && !list.isEmpty()) {
                	for (Map<String, Object> map : list) {
						XContentBuilder json = getXContentBuilderKeyValue(map);
						saveDoc(Globals.context.getIndex(), Globals.context.getType(), map.get("id").toString(), json);
                	}
                	logger.info("消费者线程:"+name+"消费"+list.size()+"个记录！");
//                    Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));
                } else {
                    // 超过2s还没数据，认为所有生产线程都已经退出，自动退出消费线程。
                    isRunning = false;
                }
            }
        } catch (Exception e) {
            logger.error("consumer thread error !",e);
            isRunning = false;
            Thread.currentThread().interrupt();
        } finally {
        	logger.info("consumer: "+name+" thead closed !");
        }
    }
    
    public boolean isRunning() {
		return isRunning;
	}

	private static XContentBuilder getXContentBuilderKeyValue(Map<String, Object> map) {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
            Set<Entry<String,Object>> entrySet = map.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				convertDateType(builder, entry);
			}
            builder.endObject();
            logger.debug(builder.string());
            return builder;
        } catch (Exception e) {
            logger.error("获取object key-value失败，{}", e);
        }
        return null;
     }
	/**
	 * 将date类型 转换
	 * @param builder
	 * @param entry
	 * @throws IOException
	 */
	private static void convertDateType(XContentBuilder builder, Entry<String, Object> entry) throws IOException {
		String key = entry.getKey();
		Object value = entry.getValue();
		if (value != null && key != null) {
			String[] dataColumns = Globals.context.getDataColumns();
			for (String dateColumn : dataColumns) {
				if(dateColumn.equals(key)){
					value = DateUtil.parseDate("yyyy-MM-dd HH:mm:ss", value.toString());
				}
			}
			builder.field(key,value);
		}
	}
    
    /**
     * 创建搜索引擎文档
     * @param index 索引名称
     * @param type 索引类型
     * @param id 索引id
     * @param doc
     * @return
     */
    private String saveDoc(String index, String type, String id, XContentBuilder json){
        IndexResponse response = client.prepareIndex(index, type,id).setSource(json).get();
        return response.getId();
   }
 
    
}