package com.liu.core.dictionary.support;

import com.liu.core.dictionary.DictionaryItem;
import com.liu.util.ApplicationContextHolder;
import com.liu.util.converter.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class CustomDictionary extends XMLDictionary {
	private static final long serialVersionUID = -29426695300041656L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomDictionary.class);
	private String className;
	private String method;
	private String bean;

	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}

	public void setClassName(String clazz) {
		this.className = clazz;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getClassName() {
		return className;
	}

	public String getMethod() {
		return method;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		try {
			Object obj = null;
			if (!StringUtils.isEmpty(bean)) {
				obj = ApplicationContextHolder.getBean(bean);
			} else {
				obj = Class.forName(className).newInstance();
			}
			Method m = obj.getClass().getMethod(method);
			Document newDefineDoc = (Document) m.invoke(obj);
			List<Element> els = newDefineDoc.getRootElement().selectNodes("//item");
			for (Element el : els) {
				DictionaryItem item = ConversionUtils.convert(el, DictionaryItem.class);
				if (el.elements("item").size() == 0 && !"false".equals(el.attributeValue("leaf"))) {
					item.setLeaf(true);
				}
				Element parent = el.getParent();
				if (parent != null) {
					String pKey = parent.attributeValue("key", "");
					item.setProperty("parent", pKey);
				}
				addItem(item);
			}
			setDefineDoc(newDefineDoc);
		} catch (Exception e) {
			LOGGER.error("get custom dic[{}] for class[{}], method[{}] occur error.", id, className, method, e);
		}
	}

}
