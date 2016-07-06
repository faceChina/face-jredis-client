package com.zjlp.face.jredis;

import java.util.List;
/**
 * Redis节点Bean
 * @author Lawrence
 *
 */
public class RedisNodeBean {
	//节点名称
	private String nodeName;
	//访问密码
	private String password;
	//超时时间
	private Integer timeout;
	//服务器列表
	private List<RedisServerBean> serverList;

	public String getNodeName() {
		return this.nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public List<RedisServerBean> getServerList() {
		return serverList;
	}

	public void setServerList(List<RedisServerBean> serverList) {
		this.serverList = serverList;
	}
	
	
}
