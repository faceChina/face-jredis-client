package com.zjlp.face.jredis.annotation.operate;

import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;

import com.zjlp.face.jredis.annotation.strategy.Configuration;
/**
 * 缓存操作类
 * 此处使用策略模式，封装缓存各类操作算法
 * @author Lawrence
 */
public interface CachedOperator {
	/**
	 * 操作缓存
	 * @param pjp
	 * @param config
	 * @return
	 * @throws Throwable
	 */
	 public Object execute(ProceedingJoinPoint pjp, ShardedJedis cacheClient,
			 Configuration config) throws Throwable;

}
