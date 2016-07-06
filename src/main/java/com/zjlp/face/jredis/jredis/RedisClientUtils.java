package com.zjlp.face.jredis.jredis;


import redis.clients.jedis.ShardedJedisPool;
/**
 * Redis客户端工具类
 * @author Lawrence
 *
 */
public class RedisClientUtils {

	public static ShardedJedisPool getJedisPool(String name){
		return (ShardedJedisPool)JedisClientPool.getInstance().get(name);
	}

	public static ShardedJedisPool getCommonJedisPool() {
		return getJedisPool("common");
	}
}
