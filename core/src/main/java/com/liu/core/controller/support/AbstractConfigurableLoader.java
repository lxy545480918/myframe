package com.liu.core.controller.support;

import com.liu.core.controller.Configurable;
import com.liu.core.controller.ConfigurableLoader;
import com.liu.core.resource.ResourceCenter;
import com.liu.util.BeanUtils;
import com.liu.util.JSONUtils;
import com.liu.util.context.ContextUtils;
import com.liu.util.xml.XMLHelper;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractConfigurableLoader<T extends Configurable> implements ConfigurableLoader<T> {
	protected String postfix = ".xml";

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public String getPostfix() {
		return postfix;
	}

	@Override
	public T load(String id) {
		String path = id.replaceAll("\\.", "/") + postfix;
		try {
			Resource r = ResourceCenter.load(ResourceUtils.CLASSPATH_URL_PREFIX, path);
			Document doc = XMLHelper.getDocument(r.getInputStream());
			return createInstanceFormDoc(id, doc, r.lastModified());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public abstract T createInstanceFormDoc(String id, Document doc, long lastModi);

	protected void setupProperties(Object o, Element el) {
		List<Element> ls = el.selectNodes("properties/p");
		try {
			for (Element p : ls) {
				Object value = parseToObject(p.getTextTrim());
				if (value == null) {
					continue;
				}
				BeanUtils.setPropertyInMap(o, p.attributeValue("name"), value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object parseToObject(String s) {
		if (StringUtils.isEmpty(s)) {
			return null;
		}
		Object v = null;
		switch (s.charAt(0)) {
		case '%':
			v = ContextUtils.get(s.substring(1));
			break;
		case '[':
			v = JSONUtils.parse(s, List.class);
			break;
		case '{':
			v = JSONUtils.parse(s, HashMap.class);
			break;
		default:
			v = s;
			break;
		}
		return v;
	}
}
