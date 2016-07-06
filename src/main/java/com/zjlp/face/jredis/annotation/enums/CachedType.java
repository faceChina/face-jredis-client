package com.zjlp.face.jredis.annotation.enums;

public enum CachedType {
	/** 业务缓存：低效*/
	LOW("low"),
	/** 业务缓存：高效*/
	COMMON("common");
	
	private CachedType(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static void main(String[] args) {
		
		System.out.println(CachedType.COMMON.getValue());
	}
	
}
