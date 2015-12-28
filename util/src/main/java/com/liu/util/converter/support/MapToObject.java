package com.liu.util.converter.support;

import com.liu.util.BeanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MapToObject implements GenericConverter {
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if(sourceType.isMap()){
			try {
				Object target = targetType.getType().newInstance();
				Map<String,Object> map = (Map<String,Object>)source;
				
				BeanUtils.copy(map, target);
				

				return target;
			} 
			catch(Exception e){
				
        		throw new IllegalStateException("falied to convert map to bean",e);
        	}
		}
		else{
			throw new IllegalStateException("source object must be a map");
		}
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> set = new HashSet<ConvertiblePair>();
		set.add(new ConvertiblePair(Map.class,Object.class));
		return set;
	}

}
