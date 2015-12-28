package com.liu.util.exp.standard;

import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;

import java.util.List;

public class CONCAT extends Expression {

	public CONCAT() {
		symbol = "concat";
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {

		try {
			StringBuffer sb = new StringBuffer();
			for (int i = 1, size = ls.size(); i < size; i++) {
				Object o = ls.get(i);
				if (o instanceof List) {
					o = processor.run((List<Object>) o);
				}
				sb.append(ConversionUtils.convert(o, String.class));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}

	}

	@Override
	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		return "(" + super.toString(ls, processor) + ")";
	}

}
