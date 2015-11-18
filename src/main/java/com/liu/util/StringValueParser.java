package com.liu.util;

import com.liu.util.context.ContextUtils;
import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.ExpressionProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@SuppressWarnings("unchecked")
public class StringValueParser {

	public static boolean isStaticString(String str) {
		return str.charAt(0) != '%' && str.charAt(0) != '[';
	}

	public static <T> T parse(String str, Class<T> type) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		switch (str.charAt(0)) {
		case '%':
			str = str.trim();
			return ConversionUtils.convert(ContextUtils.get(str.substring(1)), type);

		case '[':
			str = str.trim();
			try {
				List<Object> exp = JSONUtils.parse(str, List.class);
				return ConversionUtils.convert(ExpressionProcessor.instance().run(exp), type);
			} catch (Exception e) {
				throw new IllegalStateException("error config args:" + str);
			}
		}
		return ConversionUtils.convert(str, type);
	}

}
