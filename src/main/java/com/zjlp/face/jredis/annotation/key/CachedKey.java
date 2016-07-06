package com.zjlp.face.jredis.annotation.key;

import org.aspectj.lang.ProceedingJoinPoint;

import com.zjlp.face.jredis.annotation.RedisCached;

public interface CachedKey {
	
	public String[] getKey(ProceedingJoinPoint pjp,RedisCached redisCached) throws Exception ;
}
