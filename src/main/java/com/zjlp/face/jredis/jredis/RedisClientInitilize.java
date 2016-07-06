package com.zjlp.face.jredis.jredis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import com.zjlp.face.jredis.RedisNodeBean;
import com.zjlp.face.jredis.RedisServerBean;
/**
 * 客户端初始化类
 * @author Lawrence
 *
 */
public class RedisClientInitilize {
	
	/** 本类单例 */
	private static final RedisClientInitilize cacheClientInitilize = new RedisClientInitilize();

	public static RedisClientInitilize getInstance() {
		return cacheClientInitilize;
	}

	private static final Logger logger = LoggerFactory.getLogger(RedisClientInitilize.class);
	
	/** 读取配置类 */
	private JedisConfigParse jedisConfigParse = JedisConfigParse.getInstatnce();
	
	/**
	 * 初始化方法
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void cacheClientPoolInitialized() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException, DocumentException {
		//獲得Redis客戶端
		JedisClientPool cacheClientPool = JedisClientPool.getInstance();
		//解析服务器中Redis的缓存文件配置
		List<RedisNodeBean> jedisNodeBeanList = this.jedisConfigParse.parseXml();
		//便利节点信息
		for (RedisNodeBean jedisNodeBean : jedisNodeBeanList) {
			//Reids碎片
			List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
			//装载服务
			for (RedisServerBean server : jedisNodeBean.getServerList()) {
				JedisShardInfo js = new JedisShardInfo(server.getIp(), 
						Integer.valueOf(server.getPort()), jedisNodeBean.getTimeout());
				js.setPassword(jedisNodeBean.getPassword());
				shards.add(js);
			}
			// 创建切片连接池
			ShardedJedisPool pool = new ShardedJedisPool(PoolConfigInitial.getConfig(), 
					shards, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
			//推入
			cacheClientPool.put(jedisNodeBean.getNodeName(), pool);
			logger.warn("Add a redisClientPool in pool:"+ jedisNodeBean.getNodeName());
		}
	}
	/**
	 * 销毁方法
	 * @throws IOException
	 */
	public void cacheClientPoolDestroyed() throws IOException {
		JedisClientPool pool = JedisClientPool.getInstance();
		for (ShardedJedisPool client : pool.getAllCacheClient()) {
			client.destroy();
		}
		logger.warn("Shutdown jedisClientPool");
	}
}
