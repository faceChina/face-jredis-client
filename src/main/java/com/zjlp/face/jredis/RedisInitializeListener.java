package com.zjlp.face.jredis;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zjlp.face.jredis.jredis.RedisClientInitilize;

/**
 * Redis监听器
 * 	在WEB.xml中配置启用Redis
 * 	<listener>
 *			<listener-class>com.wgj.redis.RedisInitializeListener</listener-class>
 *	</listener>
 * @author Lawrence
 *
 */
public class RedisInitializeListener  implements ServletContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisInitializeListener.class);

	/**
	 * 閿�瘉鏂规硶
	 */
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		
	}
	
	/**
	 * 鍒濆鍖栧鎴风閾炬帴姹狅紙鍏ュ彛锛�
	 */
	public void contextInitialized(ServletContextEvent servletContextEvent) {
	  try {
	      RedisClientInitilize.getInstance().cacheClientPoolInitialized();
	    } catch (DocumentException e) {
	      logger.error(e.getMessage(), e);
	    } catch (InstantiationException e) {
	      logger.error(e.getMessage(), e);
	    } catch (IllegalAccessException e) {
	      logger.error(e.getMessage(), e);
	    } catch (ClassNotFoundException e) {
	      logger.error(e.getMessage(), e);
	    } catch (IOException e) {
	      logger.error(e.getMessage(), e);
	    }
	}

}
