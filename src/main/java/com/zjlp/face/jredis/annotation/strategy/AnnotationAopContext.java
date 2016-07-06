package com.zjlp.face.jredis.annotation.strategy;

import org.aspectj.lang.ProceedingJoinPoint;

import com.zjlp.face.jredis.annotation.client.CachedClient;
import com.zjlp.face.jredis.annotation.client.RedisClient;

/**
 * Aop Annotation 上下文
 * 使用Annotation实现上下文
 * @author Lawrence
 */
public class AnnotationAopContext implements AopContext {
	
	private CachedClient client;
	
	public AnnotationAopContext(CachedClient client){
		if (null == client) {
			this.client = RedisClient.getInstance();
		}else{
			this.client = client;
		}
	}
	
	public Object operate(ProceedingJoinPoint pjp, Configuration config) throws Throwable {
		return this.client.operate(pjp, config);
	}
	public Object expire(ProceedingJoinPoint pjp, Configuration config) throws Throwable {
		return this.client.operate(pjp, config);
	}

	public void pringLog(Configuration config) {
		this.client.pringLog(config);
	}
	
	public void change(CachedClient client){
		this.client =client;
	}
}
