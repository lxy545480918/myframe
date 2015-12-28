package com.liu.util;

import com.liu.util.converter.ConversionUtils;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;
import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.Map;

public class BeanUtils {
	private final static Mapper dozer = DozerBeanMapperSingletonWrapper.getInstance();

	public static <T> T map(Object source, Class<T> destinationClass) {
		return dozer.map(source, destinationClass);
	}

	@SuppressWarnings("unchecked")
	public static <T> T map(Object source, Object dest) {
		dozer.map(source, dest);
		return (T) dest;
	}

	public static void copy(Object source, Object dest) {
		dozer.map(source, dest);
	}

	public static Object getProperty(Object bean, String nm) {
		Object val = null;
		val = MVEL.getProperty(nm, bean);
		return val;
	}

	public static <T> T getProperty(Object bean, String nm, Class<T> type) {
		Object val = getProperty(bean, nm);
		return ConversionUtils.convert(val, type);
	}

	public static void setProperty(Object bean, String nm, Object v) {
		MVEL.setProperty(bean, nm, v);
	}
	
	public static void setPropertyInMap(Object bean,String nm,Object v) throws Exception{
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("key", nm);
		vars.put("value", v);
		MVEL.eval("setProperty(key,value)", bean, vars);
	}

}
