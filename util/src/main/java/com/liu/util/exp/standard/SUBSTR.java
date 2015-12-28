package com.liu.util.exp.standard;

import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;

import java.util.List;

public class SUBSTR extends Expression {

	public SUBSTR() {
		symbol = "substring";
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			String str = (String) processor.run((List<?>) ls.get(1));
			int start = ConversionUtils.convert(ls.get(2), Integer.class);
			if (ls.size() == 4) {
				int end = ConversionUtils.convert(ls.get(3), Integer.class);
				return str.substring(start, end);
			} else {
				return str.substring(start);
			}

		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

	@Override
	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			String str = processor.toString((List<?>) ls.get(1));
			int start = ConversionUtils.convert(ls.get(2), Integer.class);

			StringBuffer sb = new StringBuffer(symbol).append("(").append(str).append(",").append(start);
			if (ls.size() == 4) {
				int end = ConversionUtils.convert(ls.get(3), Integer.class);
				sb.append(",").append(end);
			}
			sb.append(")");
			return sb.toString();
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

}
