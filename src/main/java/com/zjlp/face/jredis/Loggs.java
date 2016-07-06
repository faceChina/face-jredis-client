package com.zjlp.face.jredis;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


@SuppressWarnings("rawtypes")
public class Loggs {

    private static Logger log = Logger.getLogger(Loggs.class.getName());

    public static Logger getLog(){
        return log;
    }
    
    public static <T> T getBean(Class<T> clazz){
    	ApplicationContext	ac=new ClassPathXmlApplicationContext("spring-beans-test.xml");
    	return ac.getBean(clazz);
    }
    
    public static void info(Object o){
            log.info(String.valueOf(o));
    }

    public static void error(Exception e){
    }

    private static List<Field> getFields(Object o){
        List<Field> list = new ArrayList<Field>();
        Class cls = o.getClass();
        while(!cls.getSimpleName().equals(Object.class.getSimpleName())){
            Field[] fields = cls.getDeclaredFields();
            for(Field f : fields){
                list.add(f);
            }
            cls = cls.getSuperclass();
        }
        return list;
    }
	
	public static String print(Object o){
		if(null == o){
			info("对象o为null!!");
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		String name = o.getClass().getName();
		try{
			if(o instanceof List){
				sb.append("List:[\n");
				List list = (List) o;
				for(int i=0;i<list.size();i++){
					sb.append("  "+deal(list.get(i))+" ,\n");
				}
				sb.append("]");
			}else if(o instanceof Map){
				sb.append("Map:[\n");
				Map<Object,Object> map = (Map) o;
				for(Entry entry:map.entrySet()){
					sb.append(entry.getKey() + " = ");
					Object obj=entry.getValue();
					if(obj instanceof List){
						List list = (List) obj;
						sb.append("[\n");
						for(int i=0;i<list.size();i++){
							Object ob=list.get(i);
							sb.append(deal(ob)+" ,\n");
						}
						sb.append("],\n");
					}else{
						sb.append(deal(obj)+" ,");
					}
				}
				if(sb.charAt(sb.length()-1)==','){
					sb.deleteCharAt(sb.length()-1);
				}
				sb.append("]");
			}else if(name.startsWith("java.lang.") || name.startsWith("java.util.")){
				sb.append(String.valueOf(o));
			}else{
				List<Field> fields = getFields(o);
				sb.append(o.getClass().getSimpleName() + ":{");
				for(Field f : fields){
					if(f.getModifiers()>2)
						continue;
					try{
						f.setAccessible(true);
					}catch(Exception e){
						continue;
					}
					Object ob = f.get(o);
					if(ob instanceof List){
						sb.append(f.getName() + "=\"");
						sb.append("[\n");
						List list = (List) ob;
						List<Field> l = new ArrayList<Field>();
						if(list.size() > 0){
							Object obj = list.get(0);
							l = getFields(obj);
						}
						for(Object obj : list){
							sb.append("  " + obj.getClass().getSimpleName() + ":{");
							for(Field field : l){
								try{
									if(field.getModifiers()>2)
										continue;
									field.setAccessible(true);
									String value = String.valueOf(field.get(obj));
									if(null!=field.get(obj))
										sb.append(field.getName() + "=\'" + value + "\'  ,");
								}catch(Exception e){
									continue;
								}
							}
							if(sb.charAt(sb.length()-1)==','){
								sb.deleteCharAt(sb.length()-1);
							}
							sb.append("}\n");
						}
						sb.append("]");
						sb.append("\"  ,");
					}else{
						if(null!=f.get(o)){
							sb.append(f.getName() + "=\"");
							sb.append(String.valueOf(f.get(o)));
							sb.append("\"  ,");
						}
					}
				}
				sb.append("}");
			}
		}catch(Exception e){
			e.printStackTrace();
			error(e);
		}
		info(sb.toString());
		return sb.toString();
	}
	private static String deal(Object o) throws Exception{
		StringBuilder sb = new StringBuilder();
		String name = o.getClass().getName();
		if(name.startsWith("java.lang.") || name.startsWith("java.util.")){
			sb.append(String.valueOf(o));
		}else{
			List<Field> fields = getFields(o);
			sb.append(o.getClass().getSimpleName() + ":{");
			for(Field f : fields){
				if(f.getModifiers()>2)
					continue;
				try{
					f.setAccessible(true);
				}catch(Exception e){
					continue;
				}
				if(null!=f.get(o)){
					sb.append(f.getName() + "=\"");
					sb.append(String.valueOf(f.get(o)));
					sb.append("\", ");
				}
			}
			sb.append("}");
		}
		return sb.toString();
	}
}
