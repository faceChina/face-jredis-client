package com.zjlp.face.jredis.annotation;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import com.zjlp.face.jredis.annotation.strategy.AopContext;
import com.zjlp.face.jredis.annotation.strategy.Configuration;

/**
 * XML配置式缓存方法切面
 * spring 配置文件里：
 * <code>
 * 	<bean id="redisCachedInterceptorForXml" class="com.zjlp.face.jredis.annotation.RedisCachedInterceptorForXml"/>
 *  <aop:aspectj-autoproxy />
 * 	<aop:config>
 *		<aop:aspect id="cachedAspect" ref="redisCachedInterceptorForXml">
 *			 <aop:around method="xmlAround" pointcut="execution(* com.zjlp.face.*.*(..)) and @annotation(redisCached)"/>
 *		</aop:aspect>
 *	</aop:config>
 *	</code>	
 * @author Lawrence
 */
public class RedisCachedInterceptorForXml {
	
	private Logger _logger = Logger.getLogger(this.getClass());
	/**
	 * 环绕通知
	 * @param pjp JoinPoint处理类
	 * @param cachedMapping annotation配置
	 * @return Object 返回操作对象
	 * @throws Throwable
	 */
	public Object xmlAround(ProceedingJoinPoint pjp,RedisCached redisCached) throws Throwable{
		/** 策略实现各类操作*/
		Configuration config = new Configuration(redisCached,pjp);
		AopContext context = config.builder();
		context.pringLog(config);
		Object obj = context.operate(pjp,config);
		return obj;
	}

	
}
