package com.zjlp.face.jredis.annotation.operate;

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;

import com.zjlp.face.jredis.Loggs;
import com.zjlp.face.jredis.annotation.strategy.Configuration;

public class OperateClear implements CachedOperator  {
	
	private static CachedOperator instance=new OperateClear();
	private OperateClear(){}
	public static CachedOperator getInstance(){
		return instance;
	}

	@Override
	public Object execute(ProceedingJoinPoint pjp,
			ShardedJedis shardedJedis, Configuration config)
			throws Throwable {
		String[] keys=config.getKey();
		String[] container=config.getContainer();
		for(String key:container){
			Set<String> set=shardedJedis.smembers(key);
			for(String str:set){
				shardedJedis.expire(str, 0);
				Loggs.info("####delete key:"+str);
			}
			shardedJedis.expire(key, 0);
			Loggs.info("####delete key:"+key);
		}
		for(String key:keys){
			shardedJedis.expire(key, 0);
			Loggs.info("####delete key:"+key);
		}
		return pjp.proceed();
	}

}
