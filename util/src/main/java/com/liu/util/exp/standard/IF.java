package com.liu.util.exp.standard;

import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;

import java.util.List;

public class IF extends Expression {

	public IF() {
		symbol = "if";
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			boolean status = (Boolean) processor.run((List<?>) ls.get(1));
			Object result = null;
			if (status) {
				result = ls.get(2);
				if (result instanceof List) {
					result = processor.run((List<?>) result);
				}
			} else {
				result = ls.get(3);
				if (result instanceof List) {
					result = processor.run((List<?>) result);
				}
			}
			return result;
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

	@Override
	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			StringBuffer sb = new StringBuffer(processor.toString((List<?>) ls.get(1)));
			sb.append(" ? ");

			Object lso = ls.get(2);
			if (lso instanceof List) {
				sb.append(processor.toString((List<?>) lso));
			} else {
				sb.append(ConversionUtils.convert(lso, String.class));
			}
			sb.append(" : ");
			lso = ls.get(3);
			if (lso instanceof List) {
				sb.append(processor.toString((List<?>) ls.get(3)));
			} else {
				sb.append(ConversionUtils.convert(lso, String.class));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

}
