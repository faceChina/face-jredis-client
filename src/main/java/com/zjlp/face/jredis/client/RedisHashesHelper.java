package com.zjlp.face.jredis.client;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.zjlp.face.jredis.jredis.RedisClientUtils;

public class RedisHashesHelper extends RedisObjectHelper {

	private static final Long L_1 = 1L;
	private static final Integer I_1 = 1;
	private ShardedJedisPool pool = null;
	
	public RedisHashesHelper(){
		super(null);
	}
	public RedisHashesHelper(String name) {
		super(name);
		pool = RedisClientUtils.getJedisPool(super.getName());
	}

	@Deprecated
	public Long hincrBy(final String key, final String field, final long value, final Integer timeout){
		return new Executor<Long>() {
			@Override
			Long execute(ShardedJedis shardedJedis) throws RuntimeException {
				Long result = shardedJedis.hincrBy(key, field, value);
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return result;
			}
		}.start(pool);
	}
	
	@Deprecated
	public Long hincrBy(final String key, final String field, final long value){
		return this.hincrBy(key, field, value, null);
	}
	
	@Deprecated
	public Long hincrBy(final String key, final String field){
		return this.hincrBy(key, field, L_1, null);
	}
	
	public Long hincrByInteger(final String key, final String field, final int value, final Integer timeout){
		return new Executor<Long>() {
			@Override
			Long execute(ShardedJedis shardedJedis) throws RuntimeException {
				Long result = shardedJedis.hincrBy(key, field, value);
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return result;
			}
		}.start(pool);
	}
	
	public Long hincrByInteger(final String key, final String field, final int value){
		return this.hincrByInteger(key, field, value, null);
	}
	
	public Long hincrByInteger(final String key, final String field){
		return this.hincrByInteger(key, field, I_1, null);
	}
	
	public <T> T hget(final String key, final String field){
		return new Executor<T>() {
			@Override
			T execute(ShardedJedis shardedJedis) throws RuntimeException {
				String obj = shardedJedis.hget(key, field);
				if (null != obj) {
					return SerializeUtil.unserialize(obj);
				}
				return null;
			}
		}.start(pool);
	}
	
	public <T> void hset(final String key, final String field, final T value, final Integer timeout){
		new Executor<T>(){
			@Override
			T execute(ShardedJedis shardedJedis) throws RuntimeException {
				shardedJedis.hset(key, field, SerializeUtil.serialize(value));
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return null;
			}
		}.start(pool);
	}
	
	public <T> void hset(final String key, final String field, final T value){
		this.hset(key, field, value, null);
	}
}
