package com.zjlp.face.jredis.annotation.key;

public class CachedKeyFactory {

	public static CachedKey createNormal(){
		return CachedKeyNormal.getInstance();
	}
	
	public static CachedKey createCustom(){
		return CachedKeyCustom.getInstance();
	}
}
