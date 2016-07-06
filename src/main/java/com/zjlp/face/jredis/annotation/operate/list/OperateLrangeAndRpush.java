package com.zjlp.face.jredis.annotation.operate.list;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;

import com.zjlp.face.jredis.Loggs;
import com.zjlp.face.jredis.annotation.operate.CachedOperator;
import com.zjlp.face.jredis.annotation.strategy.Configuration;
import com.zjlp.face.jredis.client.SerializeUtil;

public class OperateLrangeAndRpush implements CachedOperator {

	private static CachedOperator instance=new OperateLrangeAndRpush();
	private OperateLrangeAndRpush(){}
	public static CachedOperator getInstance(){
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ProceedingJoinPoint pjp,
			ShardedJedis shardedJedis, Configuration config)
			throws Throwable {
		String[] container=config.getContainer();
		Object[] args=pjp.getArgs();
		if(container.length>0&&args.length>0){
			shardedJedis.sadd(container[0], config.getKey()[0]);
		}
		List<String> hitValue = shardedJedis.lrange(config.getKey()[0], 0, Integer.MAX_VALUE);
		if (null != hitValue && !hitValue.isEmpty()) {
			//Loggs.info("Hit key["+config.getKey()[0]+"] success!");
			return SerializeUtil.unserializeList(hitValue);
		}else{
			Loggs.info("Hit key["+config.getKey()[0]+"] failure!");
		}
		
		Object obj=pjp.proceed();
		//Loggs.print(obj);
		shardedJedis.rpush(config.getKey()[0], SerializeUtil.serializeList((List<Object>)obj));
		return obj;
	}

}
