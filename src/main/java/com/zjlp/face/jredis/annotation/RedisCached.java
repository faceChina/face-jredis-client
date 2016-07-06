package com.zjlp.face.jredis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zjlp.face.jredis.annotation.enums.CachedMethod;
import com.zjlp.face.jredis.annotation.enums.CachedMode;
import com.zjlp.face.jredis.annotation.enums.CachedName;
import com.zjlp.face.jredis.annotation.enums.CachedType;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCached {
	
	/**
	 * 缓存Key
	 */
	String[] key() default {};
	
	CachedMode mode() default CachedMode.GET;
	
	CachedName name() default CachedName.CUSTOM;
	
	String[] prop() default {};
	
	String[] container() default {};
	
	/**
	 * 缓存方法
	 * @return
	 */
	CachedMethod method() default CachedMethod.GET;
	/**
	 * 缓存类型
	 * @return
	 */
	CachedType type() default CachedType.COMMON;
	
	/**
	 * 失效时间(单位：秒)
	 * 默认失效时间为 1 day
	 */
	int expireTime() default 86400;
}
