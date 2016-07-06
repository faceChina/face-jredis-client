package com.zjlp.face.jredis.annotation.client;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.zjlp.face.jredis.annotation.enums.CachedType;
import com.zjlp.face.jredis.annotation.strategy.Configuration;
import com.zjlp.face.jredis.client.SerializeUtil;
import com.zjlp.face.jredis.jredis.RedisClientUtils;

public class RedisClient implements CachedClient {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public static final RedisClient instance = new RedisClient();
	private RedisClient(){
	}

	public static RedisClient getInstance(){
		return instance;
	}

	public Object operate(ProceedingJoinPoint pjp, Configuration config)
			throws Throwable {
		ShardedJedisPool shardedJedisPool = RedisClientUtils.getJedisPool(config.getType().getValue());
		ShardedJedis shardedJedis =  null;
		try {
			shardedJedis = shardedJedisPool.getResource();
			return config.operateTo(pjp, shardedJedis, config);
		} catch (Exception e) {
			//logger.error(e.getMessage(), e);
			try{
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}catch(Exception e1){
				
			}
			logger.info("redis查询失败:"+e.getMessage()+",查询数据库!");
			return pjp.proceed();
		}finally{
			try{
				shardedJedisPool.returnResource(shardedJedis);
			}catch(Exception e){
				
			}
		}
	}
	public Object get(String key){
		ShardedJedisPool shardedJedisPool = RedisClientUtils.getJedisPool(CachedType.COMMON.getValue());
		ShardedJedis shardedJedis =  null;
		try {
			shardedJedis = shardedJedisPool.getResource();
			Object obj=SerializeUtil.unserialize(shardedJedis.get(key));
			//Loggs.print("key:"+key+":"+obj);
			return obj;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try{
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}catch(Exception e1){
				
			}
			return null;
		}finally{
			try{
				shardedJedisPool.returnResource(shardedJedis);
			}catch(Exception e){
				
			}
		}
	}
	public void pringLog(Configuration config) {
		/*StringBuilder sb = new StringBuilder();
		sb.append("start use Redis [").append(config.getType().getValue()).append("] lev cached! params List ->").append("keys : [");
		for (String key : config.getKey()) {
			sb.append(key).append(",");
		}
		sb.delete(sb.length()-1, sb.length());
		sb.append("] method : ").append(config.getMethod()).append(" expireTime : ").append(config.getExpireTime());
		logger.info(sb.toString());*/
	}

}
