package com.zjlp.face.jredis.annotation.operate.string;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;

import com.zjlp.face.jredis.Loggs;
import com.zjlp.face.jredis.annotation.operate.CachedOperator;
import com.zjlp.face.jredis.annotation.strategy.Configuration;
import com.zjlp.face.jredis.client.SerializeUtil;

public class OperateGetter implements CachedOperator {
	

	private Logger logger = Logger.getLogger(this.getClass());
	@Override
	public Object execute(ProceedingJoinPoint pjp,
			ShardedJedis shardedJedis, Configuration config) throws Throwable
			 {
		String hitValue = shardedJedis.get(config.getKey()[0]);
		if (null != hitValue) {
			Loggs.info("Hit key["+config.getKey()[0]+"] success!");
			return SerializeUtil.unserialize(hitValue);
		}
		return pjp.proceed();
	}

}
