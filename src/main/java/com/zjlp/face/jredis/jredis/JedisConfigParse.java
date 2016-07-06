package com.zjlp.face.jredis.jredis;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.zjlp.face.jredis.RedisNodeBean;
import com.zjlp.face.jredis.RedisServerBean;

/**
 * Reids.xml配置转换器
 * @author Lawrence
 *
 */
public class JedisConfigParse {

	private static final JedisConfigParse redisCONFIGPARSE = new JedisConfigParse();

	public static JedisConfigParse getInstatnce() {
		return redisCONFIGPARSE;
	}
	/**
	 * 加载Redis配置文件
	 * @return
	 * @throws DocumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 * @throws MalformedURLException
	 */
	public List<RedisNodeBean> parseXml() throws DocumentException,
	  InstantiationException, IllegalAccessException, ClassNotFoundException,
	  FileNotFoundException, MalformedURLException{
		SAXReader saxReader = new SAXReader();
	    URL filePath = getConfigFile();
	    if (filePath == null) {
	      throw new FileNotFoundException("redis.xml can't found");
	    }
	    Document document = saxReader.read(filePath);
	    Element element = document.getRootElement();
	    Element cacheNodes = element.element("nodes");
	    List<RedisNodeBean> instanceBeans = new ArrayList<RedisNodeBean>(4);
	    Iterator<?> nodeiterator = cacheNodes.elementIterator();
		while (nodeiterator.hasNext()) {
			RedisNodeBean bean = new RedisNodeBean();
			Element cacheNode = (Element)nodeiterator.next();
			bean.setNodeName(cacheNode.attribute("name").getValue());
			String password = cacheNode.attribute("passwd").getValue();
			if (null != password && !"".equals(password.trim())) {
				bean.setPassword(password);
			}
			bean.setTimeout(Integer.valueOf(cacheNode.element("timeout").getText()));
			List<RedisServerBean> serverList = new ArrayList<RedisServerBean>();
			Iterator<?> serverIterator = cacheNode.element("serverList").elementIterator();
			while (serverIterator.hasNext()) {
				String[] server = ((Element)serverIterator.next()).getText().split(":");
	    	  	RedisServerBean redisServerBean = new RedisServerBean();
		        redisServerBean.setIp(server[0]);
		        redisServerBean.setPort(server[1]);
		        serverList.add(redisServerBean);
			}
		    bean.setServerList(serverList);
		    instanceBeans.add(bean);
		}
	    return instanceBeans;
	}
	/**
	 * 获取配置文件
	 * @return
	 * @throws MalformedURLException
	 */
	private URL getConfigFile() throws MalformedURLException {
		URL filePath = PoolConfigInitial.class.getClassLoader().getResource("redis.xml");
		if (null == filePath) {
			filePath = PoolConfigInitial.class.getClassLoader().getResource("config/redis.xml");
		}
	    return filePath;
	}

}
