package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.algoutil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.reflect.ClassUtil;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.reflect.FieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map<String,Object>对象到javabean的转换类
 * @author zszhang
 *
 */
/**
 * @author zszhang
 *
 */
public final class Map2Object {
	private final static Logger logger = LoggerFactory.getLogger(Map2Object.class);
	
	/**
	 * 从Map<String,Object>对象转换到制定的类对象
	 * @param map
	 * @param clazz
	 * @return
	 */
	public  static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {
		if( null == map )		{
			logger.warn("map转换为类时，map为空，直接返回null值！");
			return null;
		}
		T obj = ClassUtil.createBean(clazz);
		if( null == obj )			{
			logger.warn("根据clazz创建对象失败，导致map2Object返回null值！");
			return null;
		}
		
		List<Field> fields = FieldUtil.getAllFields(clazz);
		for( Field field : fields ) {
			FieldUtil.setFieldValue(obj, field, map.get(field.getName()));
		}
		return obj;
	}
	
	/**
	 * 转换对象到Map对象中
	 * @param obj
	 * @return 转换好的Map对象或null
	 */
	public static<T>  Map<String, Object> object2Map(T obj) {
		if( null == obj )		return null;
		
		Map<String, Object> map = new HashMap<String, Object>();   
	
		List<Field> fields = FieldUtil.getAllFields(obj.getClass());
		for( Field field : fields ) {
			map.put(field.getName(), FieldUtil.getFieldValue(field, obj));
		}
		
		return map;
	}
}
