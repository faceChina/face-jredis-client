package com.zjlp.face.jredis.annotation.strategy;

import org.aspectj.lang.ProceedingJoinPoint;

import com.zjlp.face.jredis.annotation.client.CachedClient;

/**
 * Memcached上下文
 * @author Lawrence
 */
public interface AopContext {
	/**
	 * 操作缓存
	 * @param pjp
	 * @param config
	 * @return
	 * @throws Throwable
	 */
	public Object operate(ProceedingJoinPoint pjp, Configuration config) throws Throwable ;
	/**
	 * 打印日志
	 * @param config
	 */
	public void pringLog(Configuration config);
	
	/**
	 * 传入一个Client实现，提供切换缓存方案的方法
	 * @param client
	 */
	public void change(CachedClient client);
}
