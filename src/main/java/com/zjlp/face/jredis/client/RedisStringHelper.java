package com.zjlp.face.jredis.client;

import org.apache.log4j.Logger;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.zjlp.face.jredis.client.log.RedisLog;
import com.zjlp.face.jredis.jredis.RedisClientUtils;

public class RedisStringHelper extends RedisObjectHelper{

	private static Logger _logger = Logger.getLogger(RedisStringHelper.class);
	private ShardedJedisPool pool = null;
	public RedisStringHelper() {
		super(null);
	}
	public RedisStringHelper(String name) {
		super(name);
		pool = RedisClientUtils.getJedisPool(super.getName());
	}
	
	public Long decrease(final String key) {
		return decrease(key, null);
	}
	
	public Long decrease(final String key, final Integer timeout) {
		return new Executor<Long>() {
			@Override
			Long execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return 0l;
				}
				Long newvalue = shardedJedis.decr(key);
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return newvalue;
			}
		}.start(pool);
	}
	
	public Long decreaseBy(final String key, final Integer step) {
		return decreaseBy(key, step, null);
	}
	
	public Long decreaseBy(final String key, final Integer step, final Integer timeout) {
		return new Executor<Long>() {
			@Override
			Long execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return 0l;
				}
				Long newvalue = shardedJedis.decrBy(key, step);
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return newvalue;
			}
		}.start(pool);
	}
	
	public <T> T get(final String key){
	    return new Executor<T>() {
			@Override
			T execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return null;
				}
				try {
					_logger.info(RedisLog.getString("Redis.run","String"));
					String result = shardedJedis.get(key);
					if (null != result) {
						_logger.info(RedisLog.getString("Redis.hit","String", key));
						return SerializeUtil.unserialize(result);
					}
					_logger.info(RedisLog.getString("Redis.miss","String", key));
				} catch (Exception e) {
					e.printStackTrace();
					_logger.error(e.getMessage(),e);
				}
				return null;
			}
		}.start(pool);
	}
	//取值失败，回调取值
	public <T> T get(final String key, final AbstractRedisDaoSupport<T> daoSupport){
	    return new Executor<T>() {
			@Override
			T execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return dealFail(daoSupport);
				}
				try {
					String result = shardedJedis.get(key);
					if (null != result) {
						return SerializeUtil.unserialize(result);
					}
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
				}
				return daoSupport.support();
			}
		}.start(pool);
	}
	
	public <T> T getAndSet(final String key, final AbstractRedisDaoSupport<T> daoSupport){
		return getAndSet(key, null, daoSupport);
	}
	
	public <T> T getAndSet(final String key, final Integer timeout , 
			final AbstractRedisDaoSupport<T> daoSupport){
		return new Executor<T>() {
			@Override
			T execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return dealFail(daoSupport);
				}
				T t = null;
				try {
					String result = shardedJedis.get(key);
					t = SerializeUtil.unserialize(result);
					if (null != t) {
						return t;
					}
				} catch (Exception e) {
					_logger.error(e.getMessage(), e);
				}
				try {
					t = daoSupport.support();
				} catch (Exception e) {
					_logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
				//redis操作
				try {
					shardedJedis.set(key, SerializeUtil.serialize(t));
					if (null != timeout) {
						shardedJedis.expire(key, timeout);
					}
				} catch (Exception e) {
					_logger.error(e.getMessage(), e);
				}
				return t;
			}
		}.start(pool);
	}
	
	public Long increase(final String key) {
		return increase(key, null);
	}
	
	public Long increase(final String key, final Integer timeout) {
		return new Executor<Long>() {
			@Override
			Long execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return null;
				}
				Long newvalue = shardedJedis.incr(key);
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return newvalue;
			}
		}.start(pool);
	}
	
	public Long increaseBy(final String key, final Integer step) {
		return increaseBy(key, step, null);
	}
	
	public Long increaseBy(final String key, final Integer step, final Integer timeout) {
		return new Executor<Long>() {
			@Override
			Long execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return 0l;
				}
				Long newvalue = shardedJedis.incrBy(key, step);
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return newvalue;
			}
		}.start(pool);
	}
	
	public <T> T set(final String key, final AbstractRedisDaoSupport<T> daoSupport){
		return set(key, null, daoSupport);
	}
	
	public <T> T set(final String key, final Integer timeout, 
			final AbstractRedisDaoSupport<T> daoSupport){
		return new Executor<T>() {
			@Override
			T execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return dealFail(daoSupport);
				}
				T value = null;
				try {
					_logger.info(RedisLog.getString("Redis.run", "String"));
					daoSupport.support();
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
					throw new RuntimeException(e);
				}
				//redis操作
				try {
					shardedJedis.set(key, SerializeUtil.serialize(value));
					if (null != timeout) {
						shardedJedis.expire(key, timeout);
					}
					return value;
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
					return null;
				}
			}
		}.start(pool);
	}
	
	public <T> boolean set(final String key, final T value) {
		return set(key, null, value);
	}
	
	public <T> boolean set(final String key, final Integer timeout, final T value) {
		return new Executor<Boolean>() {
			@Override
			Boolean execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return false;
				}
				shardedJedis.set(key, SerializeUtil.serialize(value));
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return true;
			}
		}.start(pool);
	}
	
	public <T> T dealFail(AbstractRedisDaoSupport<T> daoSupport) {
		if (null != daoSupport) {
			return daoSupport.support();
		}
		return null;
	}

}
