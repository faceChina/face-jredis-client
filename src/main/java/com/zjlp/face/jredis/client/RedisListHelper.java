package com.zjlp.face.jredis.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.zjlp.face.jredis.client.log.RedisLog;
import com.zjlp.face.jredis.jredis.RedisClientUtils;
import com.zjlp.face.util.page.Pagination;

public class RedisListHelper extends RedisObjectHelper{
	
	private static Logger _logger = Logger.getLogger(RedisStringHelper.class);
	private ShardedJedisPool pool = null;
	
	public RedisListHelper(){
		super(null);
	}
	public RedisListHelper(String name) {
		super(name);
		pool = RedisClientUtils.getJedisPool(super.getName());
	}
	
	//分页查找数据
	public <T> Pagination<T> findPage(final String key, final Pagination<T> pagination) {
		return findPage(key, pagination, null);
	}
	
	//分页查找数据
	public <T> Pagination<T> findPage(final String key, final Pagination<T> pagination, final AbstractRedisDaoSupport<Pagination<T>> daoSupport) {
		return new Executor<Pagination<T>>() {
			@Override
			Pagination<T> execute(ShardedJedis shardedJedis)
					throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return dealFail(daoSupport);
				}
				//redis查询
				Integer totalRow = 0;
				List<T> datas = null;
				try {
					Long len = shardedJedis.llen(key);
					totalRow = null == len ? 0 : len.intValue();
					if (0 != totalRow) {
						List<String> stringList = shardedJedis.lrange(key, pagination.getStart(), 
								pagination.getStart() + pagination.getPageSize() - 1);
						datas = SerializeUtil.unserializeList(stringList);
					}
				} catch (Exception e) {
					totalRow = 0;
					datas = null;
					_logger.error(e.getMessage(), e);
				}
				pagination.setTotalRow(totalRow);
				pagination.setDatas(datas);
				//数据库查询
				try {
					if (0 == totalRow) {
						return daoSupport.support();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return pagination;
			}
		}.start(pool);
	}
	
	//根据索引取值
	public <T> T get(final String key, final int index) {
		return new Executor<T>() {
			@Override
			T execute(ShardedJedis shardedJedis) {
				try {
					if (null != shardedJedis) {
						String result = shardedJedis.lindex(key, index);
						if (null != result) {
							return SerializeUtil.unserialize(result);
						}
					}
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
				}
				return null;
			}
		}.start(pool);
	}
	
	
	//根据索引取值
	public <T> T get(final String key, final int index, 
			final AbstractRedisDaoSupport<T> daoSupport) {
		return new Executor<T>() {
			@Override
			T execute(ShardedJedis shardedJedis) {
				//获取连接失败
				if (null == shardedJedis) {
					return dealFail(daoSupport);
				}
				try {
					String result = shardedJedis.lindex(key, index);
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
	
	/**
	 * 从列表中获取所有元素
	* @Title: find
	* @Description:
	* @param key
	* @return
	* @return List<T>    返回类型
	* @throws
	* @author wxn  
	* @date 2015年5月22日 下午1:56:54
	 */
	public  <T> List<T> list(final String key){
		return list(key, ConstantsUtil.default_expire_time, null);
	}
	
	public <T> List<T> list(final String key, final AbstractRedisDaoSupport<List<T>> daoSupport) {
		return list(key, ConstantsUtil.default_expire_time, daoSupport);
	}
	
	public <T> List<T> list(final String key, final Integer timeout, final AbstractRedisDaoSupport<List<T>> daoSupport) {
		return new Executor<List<T>>() {
			@Override
			List<T> execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return dealFail(daoSupport);
				}
				List<T> resultList = null;
				//redis取值
				try {
					List<String> serializeStringList = shardedJedis.lrange(key, 0, -1);
					resultList = SerializeUtil.unserializeList(serializeStringList);
					if (null != resultList && !resultList.isEmpty()) {
						return resultList;
					}
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
				}
				if (null == daoSupport) return resultList; 
				//数据库取值
				try {
					resultList = daoSupport.support();
					if (null == resultList || resultList.isEmpty()) {
						return resultList;
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				//redis设值
				try {
					String[] arr = new String[resultList.size()];
					for (int i = 0; i < resultList.size(); i++) {
						arr[i] = SerializeUtil.serialize(resultList.get(i));
					}
					shardedJedis.rpush(key, arr);
					if (null != timeout) {
						shardedJedis.expire(key, timeout);
					}
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
				}
				return resultList;
			}
		}.start(pool);
	}
	
	/**
	 * 获得队列(List)的长度
	* @Title: llen
	* @Description:
	* @param key
	* @return Long    返回类型
	* @author wxn  
	* @date 2015年5月22日 下午2:03:19
	 */
	public Long llen(final String key) {
		return new Executor<Long>() {
			@Override
			Long execute(ShardedJedis shardedJedis) {
				//获取连接失败
				if (null == shardedJedis) {
					return null;
				}
				Long result = null;
				try {
					_logger.info(RedisLog.getString("Redis.run","String"));
					_logger.info(RedisLog.getString("Redis.miss","String", key));
					result = shardedJedis.llen(key);
					_logger.info(RedisLog.getString("Redis.set","String",key));
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
				}
				return result;
			}
		}.start(pool);
	}
	/**
	 * 从队列的左边入队一个元素
	* @Title: lpush
	* @Description:
	* @param key
	* @param value
	* @return boolean    返回类型
	* @author wxn  
	* @date 2015年5月22日 下午1:55:47
	 */
	public <T> boolean lpush(final String key, final Integer timeout, final T value) {
		return (Boolean) new Executor<Boolean>() {
			@Override
			Boolean execute(ShardedJedis shardedJedis) {
				//获取连接失败
				if (null == shardedJedis) {
					return false;
				}
				shardedJedis.lpush(key, SerializeUtil.serialize(value));
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return true;
			}
		}.start(pool);
	}
	public <T> boolean lpush(final String key, final T value) {
		return lpush(key, ConstantsUtil.default_expire_time, value);
	}
	
	/**
	 * 从列表中获取指定返回的元素
	* @Title: lrange
	* @Description:
	* @param key
	* @param start
	* @param end
	* @return List<T>    返回类型
	* @author wxn  
	* @date 2015年5月22日 下午2:05:17
	 */
	public <T> List<T> lrange(final String key, final long start, final long end) {
		return new Executor<List<T>>() {
			@Override
			List<T> execute(ShardedJedis shardedJedis) {
				//获取连接失败
				if (null == shardedJedis) {
					return null;
				}
				List<String> result = shardedJedis.lrange(key, start, end);
				if (null == result || result.isEmpty()) {
					_logger.info(new StringBuilder("ListRedisClient:rpushlrange ").append("the key[").append(key).append("]'s list is empty."));
					return null;
				}
				List<T> list = new ArrayList<T>();
				for (String string : result) {
					T t = SerializeUtil.unserialize(string);
					list.add(t);
				}
				return list;
			}
		}.start(pool);
	}
	
	
	
	/**
	 * 从列表中删除元素
	* @Title: lrem
	* @Description:
	* @param key
	* @param value
	* @return
	* @return boolean    返回类型
	* @throws
	* @author wxn  
	* @date 2015年5月22日 下午2:04:39
	 */
	public <T> boolean lrem(final String key, final T value) {
		return new Executor<Boolean>() {
			@Override
			Boolean execute(ShardedJedis shardedJedis) {
				//获取连接失败
				if (null == shardedJedis) {
					return false;
				}
				try {
					_logger.info(RedisLog.getString("Redis.run","List"));
					_logger.info(RedisLog.getString("Redis.miss","List", key));
					shardedJedis.lrem(key, 0, SerializeUtil.serialize(value));
					_logger.info(RedisLog.getString("Redis.rem","List",key));
					return true;
				} catch (Exception e) {
					_logger.error(e.getMessage(), e);
				}
				return false;
			}
		}.start(pool);
	}

	//统一保存
	public <T> boolean pushAll(final String key, final Integer timeout, final List<T> list) {
		return new Executor<Boolean>() {
			@Override
			Boolean execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return false;
				}
				String[] arr = new String[list.size()];
				for (int i = 0; i < list.size(); i++) {
					arr[i] = SerializeUtil.serialize(list.get(i));
				}
				shardedJedis.rpush(key, arr);
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return true;
			}
		}.start(pool);
	}

	//统一保存
	public <T> boolean pushAll(final String key, final List<T> list) {
		return pushAll(key, ConstantsUtil.default_expire_time, list);
	}
	
	/**
	 * 从队列的右边入队一个元素
	* @Title: rpush
	* @Description:
	* @param key
	* @param value
	* @return boolean    返回类型
	* @author wxn  
	* @date 2015年5月22日 下午1:56:31
	 */
	public <T> boolean rpush(final String key, final Integer timeout, final T value) {
		return (Boolean) new Executor<Boolean>() {
			@Override
			Boolean execute(ShardedJedis shardedJedis) {
				//获取连接失败
				if (null == shardedJedis) {
					return false;
				}
				shardedJedis.rpush(key, SerializeUtil.serialize(value));
				if (null != timeout) {
					shardedJedis.expire(key, timeout);
				}
				return true;
			}
		}.start(pool);
	}
	
	public <T> boolean rpush(final String key, final T value) {
		return rpush(key, ConstantsUtil.default_expire_time, value);
	}
	
	/**
	 * 从队列的左边入队多个元素
	* @Title: setLAll
	* @Description:
	* @param key
	* @param daoSupport
	* @return List<T>    返回类型
	* @author wxn  
	* @date 2015年5月22日 下午1:57:54
	 */
	public  <T> List<T> setLAll(final String key, final AbstractRedisDaoSupport<List<T>> daoSupport){
		return setLAll(key, ConstantsUtil.default_expire_time, daoSupport);
	}
	
	/**
	 * 从队列的左边入队多个元素并指定时效
	* @Title: setLAll
	* @Description:
	* @param key
	* @param timeout
	* @param daoSupport
	* @return List<T>    返回类型
	* @author wxn  
	* @date 2015年5月22日 下午1:58:54
	 */
	public <T> List<T> setLAll(final String key, final Integer timeout, 
			final AbstractRedisDaoSupport<List<T>> daoSupport){
		return new Executor<List<T>>() {
			@Override
			List<T> execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return dealFail(daoSupport);
				}
				List<T> list = null;
				try {
					_logger.info(RedisLog.getString("Redis.run", "List"));
					list = daoSupport.support();
					if (null == list || list.isEmpty()) return null;
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
					throw new RuntimeException(e);
				}
				//redis操作
				try {
					for (T value : list) {
						shardedJedis.lpush(key, SerializeUtil.serialize(value));
					}
					if (null != timeout) {
						shardedJedis.expire(key, timeout);
					}
					_logger.info(RedisLog.getString("Redis.set","List",key));
					return list;
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
				}
				return daoSupport.support();
			}
		}.start(pool);
	}
	
	/**
	 * 从队列的右边入队多个元素
	* @Title: setRAll
	* @Description:
	* @param key
	* @param daoSupport
	* @return
	* @return List<T>    返回类型
	* @throws
	* @author wxn  
	* @date 2015年5月22日 下午1:58:37
	 */
	public <T> List<T> setRAll(final String key, 
			final AbstractRedisDaoSupport<List<T>> daoSupport){
		return setRAll(key, ConstantsUtil.default_expire_time, daoSupport);
	}
	
	/**
	 * 从队列的右边入队多个元素并指定时效
	* @Title: setLAll
	* @Description:
	* @param key
	* @param timeout
	* @param daoSupport
	* @return List<T>    返回类型
	* @author wxn  
	* @date 2015年5月22日 下午1:58:54
	 */
	public <T> List<T> setRAll(final String key, final Integer timeout, 
			final AbstractRedisDaoSupport<List<T>> daoSupport){
		return new Executor<List<T>>() {
			@Override
			List<T> execute(ShardedJedis shardedJedis) throws RuntimeException {
				//获取连接失败
				if (null == shardedJedis) {
					return dealFail(daoSupport);
				}
				List<T> list = null;
				try {
					list =  (List<T>) daoSupport.support();
					if (null == list || list.isEmpty()) {
						return null;
					}
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
					throw new RuntimeException(e);
				}
				//redis操作
				try {
					for (T value : list) {
						shardedJedis.rpush(key, SerializeUtil.serialize(value));
					}
					if (null != timeout) {
						shardedJedis.expire(key, timeout);
					}
					return list;
				} catch (Exception e) {
					_logger.error(e.getMessage(),e);
					return null;
				}
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
