package com.liu.util.exp;

import com.liu.util.JSONUtils;
import com.liu.util.context.Context;
import com.liu.util.context.ContextUtils;
import com.liu.util.exp.exception.ExprException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionProcessor {
	private static final String BASE_LANG = "base";
	private static ConcurrentHashMap<String, ExpressionSet> languages = new ConcurrentHashMap<String, ExpressionSet>();
	private static ConcurrentHashMap<String, ExpressionProcessor> instances = new ConcurrentHashMap<String, ExpressionProcessor>();

	private String language;

	public ExpressionProcessor() {
		this(BASE_LANG);
	}

	private ExpressionProcessor(String lang) {
		language = lang;
		instances.put(language, this);
	}

	public static ExpressionProcessor instance(String lang) throws ExprException {
		if (lang == null) {
			return instance();
		}
		if (!languages.containsKey(lang)) {
			throw new ExprException("expr language[" + lang + "] is not found.");
		}
		ExpressionProcessor o = null;
		if (!instances.containsKey(lang)) {
			o = new ExpressionProcessor(lang);
		} else {
			o = instances.get(lang);
		}
		return o;
	}

	public static ExpressionProcessor instance() throws ExprException {
		return instance(BASE_LANG);
	}

	public void setExpressionSets(List<ExpressionSet> langs) {
		for (ExpressionSet lang : langs) {
			addExpressionSet(lang.getName(), lang);
		}
	}

	public void addExpressionSet(String nm, ExpressionSet es) {
		languages.put(nm, es);
	}

	public void addExpressionSet(ExpressionSet es) {
		addExpressionSet(BASE_LANG, es);
	}

	private Expression getExpression(String nm) {
		Expression expr = null;
		if (languages.containsKey(language)) {
			expr = languages.get(language).getExpression(nm);
		}
		if (expr == null) {
			expr = languages.get(BASE_LANG).getExpression(nm);
		}
		return expr;
	}

	private Expression lookup(List<?> ls) throws ExprException {
		if (ls == null || ls.isEmpty()) {
			throw new ExprException("expr list is empty.");
		}
		String nm = (String) ls.get(0);
		Expression expr = getExpression(nm);
		if (expr == null) {
			throw new ExprException("expr[" + nm + "] not found.");
		}
		return expr;
	}

	private List<?> parseStr(String exp) throws ExprException {
		try {
			List<?> ls = JSONUtils.parse(exp, List.class);
			return ls;
		} catch (Exception e) {
			throw new ExprException(e);
		}
	}

	public Object run(String exp) throws ExprException {
		return run(parseStr(exp));
	}

	public String toString(String exp) throws ExprException {
		return toString(parseStr(exp));
	}

	public Object run(List<?> ls) throws ExprException {
		return lookup(ls).run(ls, this);
	}

	public String toString(List<?> ls) throws ExprException {
		return lookup(ls).toString(ls, this);
	}
	
	public String toString(String exp, boolean forPreparedStatement) throws ExprException {
		configExpressionContext(forPreparedStatement);
		return toString(exp);
	}

	public String toString(List<?> ls, boolean forPreparedStatement) throws ExprException {
		configExpressionContext(forPreparedStatement);
		return toString(ls);
	}

	private void configExpressionContext(boolean forPreparedStatement) {
		ExpressionContextBean bean;
		if (ContextUtils.hasKey(Context.EXP_BEAN)) {
			bean = ContextUtils.get(Context.EXP_BEAN, ExpressionContextBean.class);
			bean.clearPatameters();
		} else {
			bean = new ExpressionContextBean();
			ContextUtils.put(Context.EXP_BEAN, bean);
		}
		bean.setForPreparedStatement(forPreparedStatement);
	}

}
