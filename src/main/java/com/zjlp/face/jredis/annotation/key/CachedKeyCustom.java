package com.zjlp.face.jredis.annotation.key;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import com.zjlp.face.jredis.annotation.RedisCached;

public class CachedKeyCustom implements CachedKey{
	
	private Logger _logger = Logger.getLogger(this.getClass());
	
	private static CachedKeyCustom instance = new CachedKeyCustom();
	
	private CachedKeyCustom(){}
	
	public static CachedKey getInstance(){
		return instance;
	}

	@Override
	public String[] getKey(ProceedingJoinPoint pjp, RedisCached cachedMapping)
			throws Exception {
		String[] baseKeys = cachedMapping.key();	
		List<String> keyList = new LinkedList<String>();
		if (baseKeys.length == 1 && baseKeys[0].equals("*")) {
			StringBuilder baseKeyBuilder = new StringBuilder();
			Class<?>[] c = pjp.getTarget().getClass().getInterfaces();
			for (Class<?> clazz : c) {
				baseKeyBuilder.append(clazz.getName()).append("@");
			}
		    if (_logger.isDebugEnabled()) {
		    	 _logger.debug("baseKey[Interfaces_All] = " + baseKeyBuilder.toString());
			}
		    keyList.add(baseKeyBuilder.toString());
		}else if (baseKeys.length == 1 && baseKeys[0].equals("*_*")) {
			StringBuilder baseKeyBuilder = new StringBuilder();
			Class<?>[] clazzs = pjp.getTarget().getClass().getInterfaces();
			for (Class<?> clazz : clazzs) {
				baseKeyBuilder.append(clazz.getName()).append("@");
			}
		    String methodName = pjp.getSignature().getName();
		    baseKeyBuilder.append("_").append(methodName);
		    if (_logger.isDebugEnabled()) {
		    	 _logger.debug("baseKey[Interfaces_Method_All] = " + baseKeyBuilder.toString());
			}
		    //_logger.info("baseKey[Interfaces_Method_All] = " + baseKeyBuilder.toString());
		    keyList.add(baseKeyBuilder.toString());
		}else{
			for (String baseKey : baseKeys) {
				keyList.add(baseKey);
			}
		}
		return keyList.toArray(new String[keyList.size()]);
	}

}
