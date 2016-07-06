package com.zjlp.face.jredis.annotation.operate.string;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;

import com.zjlp.face.jredis.annotation.operate.CachedOperator;
import com.zjlp.face.jredis.annotation.strategy.Configuration;
import com.zjlp.face.jredis.client.SerializeUtil;

public class OperateSetter implements CachedOperator {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public Object execute(ProceedingJoinPoint pjp,
			ShardedJedis shardedJedis, Configuration config)
			throws Throwable {
		Object obj = pjp.proceed();
		String result = "failure";
		if (config.getExpireTime() > 0) {
			result = shardedJedis.setex(config.getKey()[0],config.getExpireTime(), SerializeUtil.serialize(obj));
		}else{
			result = shardedJedis.setex(config.getKey()[0],86400, SerializeUtil.serialize(obj));
		}
		logger.info("Hit failure,reset key["+config.getKey()[0]+"] is "+result+"!");
		return obj;
	}

}
