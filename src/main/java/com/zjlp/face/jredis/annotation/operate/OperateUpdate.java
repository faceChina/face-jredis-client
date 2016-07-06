package com.zjlp.face.jredis.annotation.operate;

import java.lang.reflect.Field;

import org.aspectj.lang.ProceedingJoinPoint;

import redis.clients.jedis.ShardedJedis;

import com.zjlp.face.jredis.Loggs;
import com.zjlp.face.jredis.annotation.strategy.Configuration;
import com.zjlp.face.jredis.client.SerializeUtil;

public class OperateUpdate implements CachedOperator  {
	
	private static CachedOperator instance=new OperateUpdate();
	private OperateUpdate(){}
	public static CachedOperator getInstance(){
		return instance;
	}

	@Override
	public Object execute(ProceedingJoinPoint pjp,
			ShardedJedis shardedJedis, Configuration config)
			throws Throwable {
		String[] keys=config.getKey();
		String[] props=config.getProp();
		Object[] objects = pjp.getArgs();
		if (null!=objects && objects.length > 0 && props.length>0) {
			Object obj=objects[0];
			try{
				Object cache=SerializeUtil.unserialize(shardedJedis.get(keys[0]));
				if(null!=cache){
					for(String prop:props){
						Field field=null;
						try{
							field=obj.getClass().getDeclaredField(prop);
						}catch(NoSuchFieldException fe){
							field=obj.getClass().getSuperclass().getDeclaredField(prop);
						}
						field.setAccessible(true);
						Object val=field.get(obj);
						if(null!=val){
							Field f=null;
							try{
								f=obj.getClass().getDeclaredField(prop);
							}catch(NoSuchFieldException fe){
								f=obj.getClass().getSuperclass().getDeclaredField(prop);
							}
							f.setAccessible(true);
							f.set(cache, val);
						}
					}
					String serialize=SerializeUtil.serialize(cache);
					shardedJedis.set(keys[0], serialize);
					Loggs.info("####UPDATE key:"+keys[0]+":"+serialize);
				}
			}catch(Exception e){
				Loggs.error(e);
			}
		}
		
		return pjp.proceed();
	}

}
