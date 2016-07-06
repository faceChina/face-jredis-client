package com.zjlp.face.jredis.jredis;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPoolConfig;

public class PoolConfigInitial {
	private static Logger log = Logger.getLogger(PoolConfigInitial.class);
	private static JedisPoolConfig config = PoolConfigInitial.init();
	public static JedisPoolConfig getConfig() {
		return config;
	}

	//初始化基础配置
	private static JedisPoolConfig init() {
		JedisPoolConfig config = new JedisPoolConfig();
		try {
			Properties pro = getProperties();
			log.info("config : " + pro);
			//最大活动的对象个数
			config.setMaxTotal(Integer.valueOf(pro.getProperty("maxtotal")));
			//对象最大空闲时间
			config.setMaxIdle(Integer.valueOf(pro.getProperty("maxidle")));
			//获取对象时最大等待时间
			config.setMaxWaitMillis(Long.valueOf(pro.getProperty("maxwaitmillis")));
		} catch (Exception e) {
			log.error("Read redis config faild, so use default config.");
			log.error(e.getMessage(), e);
		}
		return config;
	}
	
	//配置文件读取
	private static Properties getProperties() throws Exception {
		InputStream inputStream = PoolConfigInitial.class.getClassLoader().getResourceAsStream("com/zjlp/face/jredis/jredis/redis-config.properties");   
		Properties pro = new Properties();
		pro.load(inputStream);
		return pro;
	}
}
