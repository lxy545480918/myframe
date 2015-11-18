package com.liu.util.exp;

import java.util.HashMap;
import java.util.Map;

public class ExpressionContextBean {
	private boolean forPreparedStatement = false;
	private Map<String, Object> statementParameters;

	public boolean isForPreparedStatement() {
		return forPreparedStatement;
	}

	public void setForPreparedStatement(boolean forPreparedStatement) {
		this.forPreparedStatement = forPreparedStatement;
	}

	public void setParameter(String nm, Object val) {
		if (statementParameters == null) {
			statementParameters = new HashMap<String, Object>();
		}
		statementParameters.put(nm, val);
	}

	public Map<String, Object> getStatementParameters() {
		if (statementParameters == null) {
			statementParameters = new HashMap<String, Object>();
		}
		return statementParameters;
	}

	public void clearPatameters() {
		if (statementParameters != null) {
			statementParameters.clear();
		}
	}

}
