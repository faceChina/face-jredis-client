package com.zjlp.face.jredis.client;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class SerializeUtil {

    /**
     * 序列化
     * @param object
     * @return
     */
    public static String serialize(Object object) {
        return JSON.toJSONString(object, SerializerFeature.WriteClassName);
    }

    /**
     * 反序列化对象
     * @param serializeString
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T unserialize(String serializeString) {
        return (T) JSON.parse(serializeString);
    }
    
    /**
     * 反序列化单个对象
     */
    public static <T>T unserializeObject(String serializeString,Class<T> clazz) {
        return JSONObject.parseObject(serializeString,clazz);
    }
    
    /**
     * 序列化List
     */
    public static String[] serializeList(List<Object> list) {
		String[] arr = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = SerializeUtil.serialize(list.get(i));
		}
        return arr;
    }

    /**
     * 反序列化数组
     * @param serializeString
     * @param elementType
     * @return
     */
    public static <T> List<T>  unserializeList(String serializeString,Class<T> elementType) {
        return JSONArray.parseArray(serializeString, elementType);
    }
    
    //数组反序列化
    public static <T> List<T> unserializeList(List<String> serializeStringList) {
    	if (null == serializeStringList || serializeStringList.isEmpty()) {
    		return null;
    	}
    	List<T> list = new ArrayList<T>();
    	for (String string : serializeStringList) {
    		if (null != string) {
    			T result = SerializeUtil.unserialize(string);
    			list.add(result);
    		} else {
    			list.add(null);
    		}
		}
    	return list;
    }
    
    public static void main(String[] args) {
		List list = new ArrayList();
		list.add("1231231");
		list.add("1231232");
		list.add("1231233");
		System.out.println(SerializeUtil.serialize(list));
		
	}
    
    
}
