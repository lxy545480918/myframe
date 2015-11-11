package com.liu.core.dictionary;

import com.liu.core.controller.Configurable;
import com.liu.core.controller.support.AbstractConfigurableLoader;
import com.liu.core.dictionary.support.XMLDictionary;
import com.liu.util.converter.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryLocalLoader extends AbstractConfigurableLoader<Dictionary> {
	private static final String DEFAULT_DIC_PACKAGE = "nw.core.dictionary.support.";

	public DictionaryLocalLoader() {
		postfix = ".dic";
	}


	@SuppressWarnings("unchecked")
	private static void setupProperties(Configurable o, Element el) {
		List<Element> ls = el.selectNodes("properties/p");
		for (Element p : ls) {
			String nm = p.attributeValue("name");
			List<Attribute> attrs = p.attributes();
			if (attrs.size() > 1) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (Attribute attr : attrs) {
					map.put(attr.getName(), attr.getValue());
				}
				o.setProperty(nm, map);
			} else {
				String v = p.getTextTrim();
				o.setProperty(nm, v);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Dictionary parseDocument(String id, Document doc, long lastModi) {
		Element root = doc.getRootElement();
		if (root == null) {
			return null;
		}
		String className = root.attributeValue("class", "XMLDictionary");
		try {
			if (!className.contains(".")) {
				className = StringUtils.join(DEFAULT_DIC_PACKAGE, className);
			}
			Class<Dictionary> clz = (Class<Dictionary>) Class.forName(className);
			Dictionary dic = ConversionUtils.convert(root, clz);
			dic.setId(id);
			dic.setLastModify(lastModi);
			setupProperties(dic, root);
			List<Element> els = root.selectNodes("//item");
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
				dic.addItem(item);
			}
			if (dic instanceof XMLDictionary) {
				((XMLDictionary) dic).setDefineDoc(doc);
			}
			dic.init();
			return dic;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public Dictionary createInstanceFormDoc(String id, Document doc, long lastModi) {
		return parseDocument(id, doc, lastModi);
	}
}
