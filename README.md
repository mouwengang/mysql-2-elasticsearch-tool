# mysql-2-elasticsearch-tool

用于将mysql数据导入到elasticsearch5.x中，网上有通过logstash导入的教程，但需要安装的依赖太多，并且我没有管理员权限，所以自己通过java写了一个工具，
方便以后使用。

简单配置：
在document下有个mysql-2-elasticsearch.xml配置。
配置说明：

1、elasticsearch的集群节点、索引、类型配置
<elasticsearch>
			<clusterName>store_es_cluster</clusterName>
			<clusterNodes>127.0.0.1:9300</clusterNodes>
			<index>store</index>
			<type>goods</type>
	</elasticsearch>
  
 2、mysql配置，sql查询出的列 是需要存储到 es中的数据，提前在es中配置了mapping的，需要查询出的别名和mapping的属性名称一致。
 <!-- mysql连接配置 -->
	<mysqlsource>
		<url>jdbc:mysql://127.0.0.1:3306/ltci_store?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull</url>
		<username>store</username>
		<password>store</password>
		<!-- 查询的sql，列的别名 需要和 elasticsearch的mapping字段对应，date类型的字段要转成yyyy-MM-dd HH:mm:ss格式 ,为了有序最好加一个排序-->
		<sql>select DATE_FORMAT(g.addtime,'%Y-%m-%d %H:%i:%s') addtime, g.id, g.name
			from goods g  ORDER BY g.addtime asc </sql>
		<!-- 因es中的时间格式特殊，需要转换，所以要把是date类型的字段转换，在这里配置，多个用 “,”号隔开，如：addtime,updatetime-->
		<datecolumns>addtime</datecolumns>
	</mysqlsource>
  
  3、线程数配置。
  <!-- 查询、插入线程个数 ,默认都是1-->
	<threadsource>
		<!-- 查询mysql数据的线程 -->
		<producerNum>6</producerNum>
		<!-- 插入数据到elasticsearch的线程,建议比查询线程 多一点 -->
		<consumerNum>8</consumerNum>
	</threadsource>
  
  启动说明：
  在Application.java中，修改mysql-2-elasticsearch.xml的路径，执行该main方法，就ok了。
