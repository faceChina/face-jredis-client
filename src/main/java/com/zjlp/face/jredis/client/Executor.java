package com.zjlp.face.jredis.client;

import org.apache.log4j.Logger;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

//redis执行体
public abstract class Executor<T> {
	
	private Logger log = Logger.getLogger(getClass());

	public T start(ShardedJedisPool pool) throws RuntimeException {
		ShardedJedis shardedJedis = null;
		boolean broken = false;
		try{
			if (null != pool) {
				shardedJedis = pool.getResource();
			}
			T t = execute(shardedJedis);
			releaseResource(pool, shardedJedis, broken);
			return t;
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage(), e);
			broken = true;
			releaseResource(pool, shardedJedis, broken);
			return null;
		}
	}
	
	private void releaseResource(ShardedJedisPool pool, ShardedJedis shardedJedis, boolean broken) {
	    if (pool != null) {
	      if (broken) {
	    	  pool.returnBrokenResource(shardedJedis);
	      } else {
	    	  pool.returnResource(shardedJedis);
	      }
	    }
	  }
	
	abstract T execute(ShardedJedis shardedJedis) throws RuntimeException;
}
