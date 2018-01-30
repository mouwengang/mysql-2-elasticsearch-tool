package com.tk.configure;

import java.net.InetAddress;
import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
public class TransportClientFactoryBean implements FactoryBean<TransportClient>,InitializingBean, DisposableBean{

	private static final Logger logger =LoggerFactory.getLogger(TransportClientFactoryBean.class);
	   private String clusterNodes = "127.0.0.1:9300";
	   private String clusterName = "elasticsearch";
	   private Boolean clientTransportSniff = true;
	   private Boolean clientIgnoreClusterName = Boolean.FALSE;
	   private String clientPingTimeout = "5s";
	   private String clientNodesSamplerInterval = "5s";
	   private TransportClient client;
	   static final String COLON = ":";
	   static final String COMMA = ",";
	   
	   public TransportClientFactoryBean(String clusterName, String clusterNodes) {
			super();
			this.clusterName = clusterName;
			this.clusterNodes = clusterNodes;
		}
	 
	   @Override
	   public void destroy() throws Exception {
	       try {
	           logger.info("Closing elasticSearch client");
	           if (client != null) {
	                client.close();
	           }
	       } catch (final Exception e) {
	           logger.error("Error closing ElasticSearch client: ", e);
	       }
	    }
	 
	   @Override
	   public TransportClient getObject() throws Exception {
	       return client;
	    }
	 
	   @Override
	   public Class<TransportClient> getObjectType() {
	       return TransportClient.class;
	    }
	 
	   @Override
	   public boolean isSingleton() {
	       return false;
	    }
	 
	   @Override
	   public void afterPropertiesSet() throws Exception {
	       buildClient();
	    }
	 
	protected void buildClient()  {
	       client = new PreBuiltTransportClient(settings());
	       try {
	       for (String clusterNode : clusterNodes.split(COMMA)) {
	           String hostName =clusterNode.split(COLON)[0];
	           String port = clusterNode.split(COLON)[1];
	           Assert.hasText(hostName, "[Assertion failed] missing host name in'clusterNodes'");
	           Assert.hasText(port, "[Assertion failed] missing port in'clusterNodes'");
	           logger.info("adding transport node : " + clusterNode);
			   client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostName),Integer.valueOf(port)));
	        }
	       List<DiscoveryNode> connectedNodes = client.connectedNodes();
	       for (DiscoveryNode discoveryNode : connectedNodes) {
	    	   logger.info("collected Nodes:"+discoveryNode.getHostName());
	       }
	       } catch (Exception e) {
	    	   logger.error("Error connect ElasticSearch : ", e);
	       }
	    }

	private Settings settings() {
	       return Settings.builder()
	                .put("cluster.name",clusterName)
	               .put("client.transport.sniff", clientTransportSniff)
	               .put("client.transport.ignore_cluster_name",clientIgnoreClusterName)
	               .put("client.transport.ping_timeout", clientPingTimeout)
	                .put("client.transport.nodes_sampler_interval",clientNodesSamplerInterval)
	                .build();
	    }
	 
	   public void setClusterNodes(String clusterNodes) {
	       this.clusterNodes = clusterNodes;
	    }
	 
	   public void setClusterName(String clusterName) {
	       this.clusterName = clusterName;
	    }
	 
	   public void setClientTransportSniff(Boolean clientTransportSniff) {
	       this.clientTransportSniff = clientTransportSniff;
	    }
	 
	   public String getClientNodesSamplerInterval() {
	       return clientNodesSamplerInterval;
	    }
	 
	   public void setClientNodesSamplerInterval(String clientNodesSamplerInterval) {
	       this.clientNodesSamplerInterval = clientNodesSamplerInterval;
	    }
	 
	   public String getClientPingTimeout() {
	       return clientPingTimeout;
	    }
	 
	   public void setClientPingTimeout(String clientPingTimeout) {
	       this.clientPingTimeout = clientPingTimeout;
	    }
	 
	   public Boolean getClientIgnoreClusterName() {
	       return clientIgnoreClusterName;
	    }
	 
	   public void setClientIgnoreClusterName(Boolean clientIgnoreClusterName){
	       this.clientIgnoreClusterName = clientIgnoreClusterName;
	    }

}
