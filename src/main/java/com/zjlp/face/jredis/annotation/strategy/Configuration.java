package com.zjlp.face.jredis.annotation.strategy;

import java.lang.reflect.Field;

import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;

import com.zjlp.face.jredis.Loggs;
import com.zjlp.face.jredis.annotation.RedisCached;
import com.zjlp.face.jredis.annotation.client.RedisClient;
import com.zjlp.face.jredis.annotation.enums.CachedMethod;
import com.zjlp.face.jredis.annotation.enums.CachedMode;
import com.zjlp.face.jredis.annotation.enums.CachedType;
import com.zjlp.face.jredis.annotation.key.CachedKey;
import com.zjlp.face.jredis.annotation.key.CachedKeyFactory;
import com.zjlp.face.jredis.annotation.operate.CachedOperator;
import com.zjlp.face.jredis.annotation.operate.OperateClear;
import com.zjlp.face.jredis.annotation.operate.OperateUpdate;
import com.zjlp.face.jredis.annotation.operate.list.OperateLrange;
import com.zjlp.face.jredis.annotation.operate.list.OperateLrangeAndRpush;
import com.zjlp.face.jredis.annotation.operate.string.OperateGetter;
import com.zjlp.face.jredis.annotation.operate.string.OperateGetterAndSetter;
import com.zjlp.face.jredis.annotation.operate.string.OperateSetter;

public class Configuration {
	
	//类型 String list
	private CachedType type;
	private CachedMode mode;
	//缓存键
	private String[] key;
	private String[] prop;
	//失效时间 (单位 ：秒)
	private int expireTime;
	//操作方法
	private CachedMethod method;
	private String[] container;
	//操作器
	private CachedOperator cachedOperator;

	/**
	 * 简单key直接设置构造器
	 * @Title:  
	 * @Description:
	 * @prop redisCached
	 */
	public Configuration(RedisCached redisCached) {
		this.type = redisCached.type();
		this.mode=redisCached.mode();
		this.key = redisCached.key();
		this.expireTime = redisCached.expireTime();
		this.method = redisCached.method();
		this.container=redisCached.container();
		this.prop=redisCached.prop();
		this.setCachedOperator(redisCached.method());
	}
	
	/**
	 * 复杂Key构造器
	 * @Title:  
	 * @Description:
	 * @prop redisCached
	 * @prop pjp
	 * @throws Exception 
	 */
	public Configuration(RedisCached redisCached, ProceedingJoinPoint pjp) throws Exception {
		this.type = redisCached.type();
		this.mode=redisCached.mode();
		this.expireTime = redisCached.expireTime();
		this.method = redisCached.method();
		this.prop=redisCached.prop();
		this.container=this.setContainer(redisCached, pjp);
		this.setCachedOperator(redisCached.method());
		this.key=this.setKey(redisCached, pjp);
	}

	public String[] setContainer(RedisCached redisCached, ProceedingJoinPoint pjp) {
		String[] cont = redisCached.container();
		String[] arr = new String[cont.length];
		if(cont.length > 0 && pjp.getArgs().length > 0){
			Object obj = pjp.getArgs()[0];
			for(int i = 0;i < cont.length;i++){
				String str = cont[i];
				if(str.indexOf(":")!=-1){
					String key = str.substring(0, str.indexOf(":"));
					String prop = str.substring(str.indexOf(":")+1);
					try{
						Field field =null;
						try{
							field=obj.getClass().getDeclaredField(prop);
						}catch(NoSuchFieldException fe){
							field=obj.getClass().getSuperclass().getDeclaredField(prop);
						}
						field.setAccessible(true);
						Object val = field.get(obj);
						arr[i] = key + "_" + val;
					}catch(Exception e){
						Loggs.error(e);
					}
				}else{
					arr[i]=str+"_"+obj.toString();
				}
			}
		}
		return arr;
	}
	public String[] setKey(RedisCached redisCached, ProceedingJoinPoint pjp) throws Exception{
		String[] baseKeys = redisCached.key();
		CachedKey cachedKey = null;
		if (true || null  == baseKeys || 0 >= baseKeys.length) {
			cachedKey = CachedKeyFactory.createNormal();
		}else{
			cachedKey = CachedKeyFactory.createCustom();
		}
		return cachedKey.getKey(pjp, redisCached);
	}
	
	
	public AopContext builder(){
		return new AnnotationAopContext(RedisClient.getInstance());
	}
	
	
	public void setCachedOperator(CachedMethod method) {
		if(CachedMode.CLEAR.equals(this.mode)){
			this.cachedOperator = OperateClear.getInstance();
		}else if(CachedMode.UPDATE.equals(this.mode)){
			this.cachedOperator=OperateUpdate.getInstance();
		}else{
			if (CachedMethod.SET.equals(method) ) {
				this.cachedOperator = new OperateSetter();
			}else if (CachedMethod.GET_SET.equals(method) ) {
				this.cachedOperator = OperateGetterAndSetter.getInstance();
			}else if (CachedMethod.LRANGE.equals(method) ) {
				this.cachedOperator = new OperateLrange();
			}else if (CachedMethod.LRANGE_RPUSH.equals(method) ) {
				this.cachedOperator = OperateLrangeAndRpush.getInstance();
			}else {
				this.cachedOperator = new OperateGetter();
			}
		}
	}
	
	public Object operateTo(ProceedingJoinPoint pjp, ShardedJedis cacheClient,
			Configuration config) throws Throwable {
			return cachedOperator.execute(pjp, cacheClient, config);
	}
	
	public CachedType getType() {
		return type;
	}

	public void setType(CachedType type) {
		this.type = type;
	}

	public String[] getKey() {
		return key;
	}

	public void setKey(String[] key) {
		this.key = key;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	public CachedMethod getMethod() {
		return method;
	}

	public void setMethod(CachedMethod method) {
		this.method = method;
	}

	public String[] getContainer() {
		return container;
	}

	public void setContainer(String[] container) {
		this.container = container;
	}

	public String[] getProp() {
		return prop;
	}

	public void setProp(String[] prop) {
		this.prop = prop;
	}
	
}
