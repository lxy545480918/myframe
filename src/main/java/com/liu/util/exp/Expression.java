package com.liu.util.exp;

import com.liu.util.exp.exception.ExprException;

import java.util.List;

public abstract class Expression {
	protected String symbol;
	protected String name;
	protected boolean needBrackets = false;

	public abstract Object run(List<?> ls, ExpressionProcessor processor) throws ExprException;

	public String getName() {
		if (name != null) {
			return name;
		} else {
			name = this.getClass().getSimpleName().toLowerCase();
		}
		return name;
	}

	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			StringBuffer sb = new StringBuffer();
			if (needBrackets) {
				sb.append("(");
			}
			for (int i = 1, size = ls.size(); i < size; i++) {
				if (i > 1) {
					sb.append(" ").append(symbol).append(" ");
				}
				Object lso = ls.get(i);
				sb.append(ExpressionUtils.toString(lso, processor));
			}
			if (needBrackets) {
				sb.append(")");
			}
			return sb.toString();
		} catch (Exception e) {
			throw new ExprException(e);
		}
	}
}
