package com.liu.util.exp.standard;

import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;

import java.util.List;

@SuppressWarnings("rawtypes")
public class ISNULL extends Expression {
	public ISNULL() {
		name = "isNull";
	}

	public Object run(List ls, ExpressionProcessor processor) throws ExprException {
		Object lso = ls.get(1);
		if (lso instanceof List) {
			lso = processor.run((List<?>) lso);
		}
		return lso == null;
	}

	public String toString(List ls, ExpressionProcessor processor) throws ExprException {
		Object lso = ls.get(1);
		if (lso instanceof List) {
			lso = processor.toString((List<?>) lso);
		}
		StringBuffer sb = new StringBuffer(ConversionUtils.convert(lso, String.class));
		sb.append(" is null");
		return sb.toString();
	}

}
