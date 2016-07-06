package com.zjlp.face.jredis.client;

public abstract class AbstractRedisDaoSupport<T> {

	public abstract T support() throws RuntimeException;
}
