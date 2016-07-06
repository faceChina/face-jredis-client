package com.zjlp.face.jredis.annotation.operate.string;

import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;

import com.zjlp.face.jredis.Loggs;
import com.zjlp.face.jredis.annotation.operate.CachedOperator;
import com.zjlp.face.jredis.annotation.strategy.Configuration;
import com.zjlp.face.jredis.client.SerializeUtil;

public class OperateGetterAndSetter implements CachedOperator  {
	
	private static CachedOperator instance=new OperateGetterAndSetter();
	private OperateGetterAndSetter(){}
	public static CachedOperator getInstance(){
		return instance;
	}

	@Override
	public Object execute(ProceedingJoinPoint pjp,
			ShardedJedis shardedJedis, Configuration config)
			throws Throwable {
		String[] container=config.getContainer();
		Object[] args=pjp.getArgs();
		if(container.length>0&&args.length>0){
			shardedJedis.sadd(container[0], config.getKey()[0]);
		}
		String hitValue = shardedJedis.get(config.getKey()[0]);
		if (null != hitValue) {
			//Loggs.info("Hit key["+config.getKey()[0]+"] success!");
			return SerializeUtil.unserialize(hitValue);
		}
		Object obj = pjp.proceed();
		String result = "failure";
		if (config.getExpireTime() > 0) {
			result = shardedJedis.setex(config.getKey()[0],config.getExpireTime(), SerializeUtil.serialize(obj));
		}else{
			result = shardedJedis.setex(config.getKey()[0],config.getExpireTime(), SerializeUtil.serialize(obj));
		}
		Loggs.info("Hit failure,reset key["+config.getKey()[0]+"] is "+result+"!");
		return obj;
		
	}

}
