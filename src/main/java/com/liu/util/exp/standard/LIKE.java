package com.liu.util.exp.standard;

import com.liu.util.context.Context;
import com.liu.util.context.ContextUtils;
import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class LIKE extends Expression {

	public LIKE() {
		symbol = "like";
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			Object lso = ls.get(1);
			String str1 = null;
			if (lso instanceof List) {
				str1 = ConversionUtils.convert(processor.run((List<?>) lso), String.class);
			} else {
				str1 = ConversionUtils.convert(lso, String.class);
			}

			lso = ls.get(2);
			String str2 = null;
			if (lso instanceof List) {
				str2 = ConversionUtils.convert(processor.run((List<?>) lso), String.class);
			} else {
				str2 = ConversionUtils.convert(lso, String.class);
			}

			if (!StringUtils.contains(str2, "%")) {
				str2 = str2 + "%";
			}

			Pattern pattern = Pattern.compile(str2.replaceAll("%", ".*"));
			return pattern.matcher(str1).find();
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		StringBuffer sb = new StringBuffer();
		Object lso = ls.get(1);
		String str1 = null;
		if (lso instanceof List) {
			str1 = processor.toString((List<?>) lso);
		} else {
			str1 = ConversionUtils.convert(lso, String.class);
		}
		sb.append(str1).append(" ").append(symbol).append(" ");

		lso = ls.get(2);
		String str2 = null;

		if (lso instanceof List) {
			str2 = processor.toString((List<?>) lso);
			Context ctx = ContextUtils.getContext();
			Boolean forPreparedStatement = ctx.get("$exp.forPreparedStatement", Boolean.class);
			if (forPreparedStatement != null && forPreparedStatement == true && str2.startsWith(":")) {
				sb.append(str2);
				HashMap<String, Object> parameters = ctx.get("$exp.statementParameters", HashMap.class);
				String key = str2.substring(1);
				String val = ConversionUtils.convert(parameters.get(key), String.class);
				if (!StringUtils.endsWith(val, "%")) {
					parameters.put(key, val + "%");
				}
			} else {
				if (!str2.startsWith("'")) {
					sb.append("'");
				}

				if (str2.endsWith("'")) {
					str2 = str2.substring(0, str2.length() - 1);
				}
				sb.append(str2);
				if (!StringUtils.contains(str2, "%")) {
					sb.append("%");
				}
				sb.append("'");
			}
		} else {
			str2 = ConversionUtils.convert(lso, String.class);
			sb.append("'").append(str2);
			if (!StringUtils.contains(str2, "%")) {
				sb.append("%");
			}
			sb.append("'");
		}

		return sb.toString();
	}

}
