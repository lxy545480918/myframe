package com.liu.util.exp.standard;

import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;

import java.util.HashSet;
import java.util.List;

public class IN extends Expression {

	public IN() {
		symbol = "in";
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			Object o = processor.run((List<?>) ls.get(1));
			List<?> rang = (List<?>) ls.get(2);
			if (rang.get(0).equals("$")) {
				rang = (List<?>) processor.run(rang);
			}
			HashSet<Object> set = new HashSet<Object>();
			set.addAll(rang);
			return set.contains(o);
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

	@Override
	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			Object o = ls.get(1);
			StringBuffer sb = new StringBuffer();
			if (o instanceof List) {
				List<?> ls1 = (List<?>) o;
				sb.append(processor.toString(ls1));
			} else {
				sb.append((String) o);
			}

			sb.append(" ").append(symbol).append("(");
			List<?> rang = (List<?>) ls.get(2);
			if (rang.get(0).equals("$")) {
				String s = processor.toString(rang);
				sb.append(s);
			} else {
				for (int i = 0, size = rang.size(); i < size; i++) {
					if (i > 0) {
						sb.append(",");
					}
					Object r = rang.get(i);
					String s = ConversionUtils.convert(r, String.class);
					if (r instanceof Number) {
						sb.append(s);
					} else {
						sb.append("'").append(s).append("'");
					}
				}
			}
			return sb.append(")").toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExprException(e.getMessage());
		}
	}

}
