package com.zjlp.face.jredis.jredis;

import java.util.Collection;
import java.util.Hashtable;

import redis.clients.jedis.ShardedJedisPool;
/**
 * Redis连接池
 * @author Lawrence
 *
 */
public class JedisClientPool extends Hashtable<String, ShardedJedisPool> {

	private static final long serialVersionUID = 39954086075003452L;
	
	/**
	 * 本类单例
	 */
	static JedisClientPool cacheClientPool = new JedisClientPool();

	public static JedisClientPool getInstance(){
	
	    return cacheClientPool;
	}

	public ShardedJedisPool getCacheClient(String nodeName){
		return (ShardedJedisPool)super.get(nodeName);
	}

	public Collection<ShardedJedisPool> getAllCacheClient(){
	    return super.values();
	}

}
