package com.zjlp.face.jredis.annotation.key;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import com.zjlp.face.jredis.Loggs;
import com.zjlp.face.jredis.annotation.RedisCached;
import com.zjlp.face.jredis.annotation.enums.CachedMode;
import com.zjlp.face.jredis.annotation.enums.CachedName;
import com.zjlp.face.jredis.client.SerializeUtil;

public class CachedKeyNormal implements CachedKey {
	
	private Logger _logger = Logger.getLogger(this.getClass());
	
	private static CachedKeyNormal instance = new CachedKeyNormal();
	
	public static CachedKey getInstance(){
		return instance;
	}

	@Override
	public String[] getKey(ProceedingJoinPoint pjp, RedisCached redisCached)
			throws Exception {
		try {
			if(CachedMode.GET.equals(redisCached.mode())){
				String[] keys=redisCached.key();
				List<String> keyList = new LinkedList<String>();
				StringBuilder baseKeyBuilder = new StringBuilder();
				String serializeArgs="";
				Object[] objects = pjp.getArgs();
				if (null != objects && objects.length > 0) {
					String serialize = SerializeUtil.serialize(objects);
					serializeArgs=serialize;
				}
				if(CachedName.NORMAL.equals(redisCached.name())||keys.length==0){
					Class<?>[] c = pjp.getTarget().getClass().getInterfaces();
					for (Class<?> clazz : c) {
						baseKeyBuilder.append(clazz.getName()).append("@");
					}
					String methodName = pjp.getSignature().getName();
					baseKeyBuilder.append("_").append(methodName);
					if(keys.length>0){
						for(String key:keys){
							keyList.add(baseKeyBuilder.toString()+"_"+key+"_"+serializeArgs);
						}
					}else{
						keyList.add(baseKeyBuilder.toString()+serializeArgs);
					}
					return keyList.toArray(new String[keyList.size()]);
				}else{
					for(int i=0;i<keys.length;i++){
						keys[i]=keys[i]+"_"+serializeArgs;
					}
					return keys;
				}
				
			}else if(CachedMode.CLEAR.equals(redisCached.mode())){
				String[] keys=redisCached.key();
				List<String> keyList = new LinkedList<String>();
				StringBuilder baseKeyBuilder = new StringBuilder();
				Class<?>[] c = pjp.getTarget().getClass().getInterfaces();
				for (Class<?> clazz : c) {
					baseKeyBuilder.append(clazz.getName()).append("@");
				}
				Object[] objects = pjp.getArgs();
				String baseKey="";
				if(CachedName.NORMAL.equals(redisCached.name())||keys.length==0){
					baseKey=baseKeyBuilder.toString()+"_";
				}else{
					
				}
				if (null != objects && objects.length > 0) {
					Object obj=objects[0];
					for(int i=0;i<keys.length;i++){
						String[] argNames=keys[i].substring(keys[i].indexOf(":")+1).split("\\|");
						Object[] argValues=new Object[argNames.length];
						for(int j=0;j<argNames.length;j++){
							try{
								Field field=null;
								try{
									field=obj.getClass().getDeclaredField(argNames[j]);
								}catch(NoSuchFieldException fe){
									field=obj.getClass().getSuperclass().getDeclaredField(argNames[j]);
								}
								field.setAccessible(true);
								Object val=field.get(obj);
								argValues[j]=val;
							}catch(Exception e){
								Loggs.error(e);
							}
						}
						keyList.add(baseKey+keys[i].substring(0, keys[i].indexOf(":"))+"_"+SerializeUtil.serialize(argValues));
					}
				}
				return keyList.toArray(new String[keyList.size()]);
			}else if(CachedMode.UPDATE.equals(redisCached.mode())){
				String[] arr=redisCached.key();
				List<String> keyList = new LinkedList<String>();
				StringBuilder baseKeyBuilder = new StringBuilder();
				Class<?>[] c = pjp.getTarget().getClass().getInterfaces();
				for (Class<?> clazz : c) {
					baseKeyBuilder.append(clazz.getName()).append("@");
				}
				Object[] objects = pjp.getArgs();
				String baseKey="";
				if(CachedName.NORMAL.equals(redisCached.name())||arr.length==0){
					baseKey=baseKeyBuilder.toString()+"_";
				}else{
					
				}
				if (null != objects && objects.length > 0) {
					Object obj=objects[0];
					for(int i=0;i<arr.length;i++){
						String[] argNames=arr[i].substring(arr[i].indexOf(":")+1).split("\\|");
						Object[] argValues=new Object[argNames.length];
						for(int j=0;j<argNames.length;j++){
							try{
								Field field=null;
								try{
									field=obj.getClass().getDeclaredField(argNames[j]);
								}catch(NoSuchFieldException fe){
									field=obj.getClass().getSuperclass().getDeclaredField(argNames[j]);
								}
								field.setAccessible(true);
								Object val=field.get(obj);
								argValues[j]=val;
							}catch(Exception e){
								Loggs.error(e);
							}
						}
						keyList.add(baseKey+arr[i].substring(0, arr[i].indexOf(":"))+"_"+SerializeUtil.serialize(argValues));
					}
				}
				return keyList.toArray(new String[keyList.size()]);
			}
			return new String[]{};
		} catch (Exception e) {
			throw e;
		}
	}

}
