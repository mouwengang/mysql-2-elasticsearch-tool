package com.tk.configure;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.tk.content.Globals;

public class TKPlaceHolderConfigure extends PropertyPlaceholderConfigurer{

	//从用户配置文件解析结果中加载属性信息
	@Override
	protected void loadProperties(Properties props) throws IOException {
		//props = Globals.context.getConnect_prop();
		props.putAll(Globals.context.getConnect_prop());
	}
	
}
