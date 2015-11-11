package com.liu.util.converter;

import com.liu.util.converter.support.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Set;

public class ConversionUtils {
	private static ConfigurableConversionService conversion = new DefaultConversionService();

	static {
		conversion.addConverter(new LongToDate());
		conversion.addConverter(new DateToLong());
		conversion.addConverter(new DateToNumber());
		conversion.addConverter(new DateToString());
		conversion.addConverter(new IntegerToBoolean());
		conversion.addConverter(new StringToDate());
		conversion.addConverter(new StringToSQLDate());
		conversion.addConverter(new StringToSQLTime());
		conversion.addConverter(new StringToTimestamp());
		conversion.addConverter(new StringToMap());
		conversion.addConverter(new StringToList());
		conversion.addConverter(new StringToDocument());
		conversion.addConverter(new StringToElement());
		conversion.addConverter(new StringToInetSocketAddress());
		conversion.addConverter(new DocumentToString());
		conversion.addConverter(new ElementToString());
		conversion.addConverter(new ElementToObject());
		conversion.addConverter(new ObjectToElement());
		conversion.addConverter(new MapToObject());
		conversion.addConverter(new ObjectToMap());
	}

	@SuppressWarnings("rawtypes")
	public void setConverters(Set<Converter> converters) {
		for (Converter c : converters) {
			conversion.addConverter(c);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object source, Class<T> targetType) {
		if (targetType.isInstance(source)) {
			return (T) source;
		}
		return conversion.convert(source, targetType);
	}

	public static boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		return conversion.canConvert(sourceType, targetType);
	}

}
