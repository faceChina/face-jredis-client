package com.zjlp.face.jredis.annotation.client;

import org.aspectj.lang.ProceedingJoinPoint;

import com.zjlp.face.jredis.annotation.strategy.Configuration;

public interface CachedClient {
	/**
	 * 操作缓存
	 * @param pjp
	 * @param config
	 * @return
	 * @throws Throwable
	 */
	public Object operate(ProceedingJoinPoint pjp, Configuration config)  throws Throwable;
	/**
	 * 打印日志
	 * @param config
	 */
	public void pringLog(Configuration config) ;
}
