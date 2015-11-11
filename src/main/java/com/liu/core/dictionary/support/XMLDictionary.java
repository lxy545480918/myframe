package com.liu.core.dictionary.support;

import com.liu.core.dictionary.Dictionary;
import com.liu.core.dictionary.DictionaryItem;
import com.liu.core.dictionary.SliceTypes;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class XMLDictionary extends Dictionary {

	private static final long serialVersionUID = -596194603210170948L;
	private static final String LEAF_CNDS = "count(./*) = 0";
	private static final String FOLDER_CNDS = "count(./*) > 0";
	private Document defineDoc;

	public XMLDictionary() {

	}

	public XMLDictionary(String id) {
		setId(id);
	}

	public void setDefineDoc(Document doc) {
		this.defineDoc = doc;
	}

	public Document getDefineDoc() {
		return defineDoc;
	}

	@Override
	public List<DictionaryItem> getSlice(String parentKey, int sliceType, String query) {
		List<DictionaryItem> result = null;
		switch (sliceType) {
		case SliceTypes.ALL_FOLDER:
			result = getAllFolder(parentKey, query);
			break;

		case SliceTypes.ALL_LEAF:
			result = getAllLeaf(parentKey, query);
			break;

		case SliceTypes.CHILD_ALL:
			result = getAllChild(parentKey, query);
			break;

		case SliceTypes.CHILD_FOLDER:
			result = getChildFolder(parentKey, query);
			break;

		case SliceTypes.CHILD_LEAF:
			result = getChildLeaf(parentKey, query);
			break;

		default:
			result = getAllItems(parentKey, query);
		}

		return result;
	}

	private void linkQueryXPath(StringBuffer xpath, String query, String exCnd) {
		if (!StringUtils.isEmpty(query)) {
			xpath.append("contains(lower-case(@");
			char first = query.charAt(0);
			if (first == searchKeySymbol) {
				xpath.append("key").append("),lower-case('").append(query.substring(1)).append("')");
			} else if (first == searchExSymbol) {
				xpath.append(searchFieldEx).append("),lower-case('").append(query.substring(1)).append("')");
			} else {
				xpath.append(searchField).append("),lower-case('").append(query).append("')");
			}
			xpath.append(")");
			if (!StringUtils.isEmpty(exCnd)) {
				xpath.append(" and ").append(exCnd);
			}
		} else {
			if (!StringUtils.isEmpty(exCnd)) {
				xpath.append(exCnd);
			}
		}
	}

	private List<DictionaryItem> toDictionaryItemList(List<Element> ls) {
		List<DictionaryItem> result = new ArrayList<DictionaryItem>();

		for (Element el : ls) {
			String key = el.attributeValue("key");
			result.add(items.get(key));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<DictionaryItem> getAllItems(String parentKey, String query) {
		if (defineDoc == null) {
			return null;
		}
		Element define = defineDoc.getRootElement();

		StringBuffer xpath = new StringBuffer();
		if (!StringUtils.isEmpty(parentKey)) {
			xpath.append("//item[@key='").append(parentKey).append("']");
		}

		if (!StringUtils.isEmpty(query)) {
			xpath.append("//item[");
			linkQueryXPath(xpath, query, null);
			xpath.append("]");
		} else {
			xpath.append("//item");
		}

		List<Element> ls = define.selectNodes(xpath.toString());
		return toDictionaryItemList(ls);
	}

	@SuppressWarnings("unchecked")
	private List<DictionaryItem> getAllChild(String parentKey, String query) {
		if (defineDoc == null) {
			return null;
		}
		Element define = defineDoc.getRootElement();

		StringBuffer xpath = new StringBuffer();
		if (!StringUtils.isEmpty(parentKey)) {
			xpath.append("//item[@key='").append(parentKey).append("']");
		} else {
			xpath.append(".");
		}

		if (!StringUtils.isEmpty(query)) {
			xpath.append("/item[");
			linkQueryXPath(xpath, query, null);
			xpath.append("]");
		} else {
			xpath.append("/item");
		}
		List<Element> ls = define.selectNodes(xpath.toString());
		return toDictionaryItemList(ls);
	}

	@SuppressWarnings("unchecked")
	private List<DictionaryItem> getAllLeaf(String parentKey, String query) {
		if (defineDoc == null) {
			return null;
		}
		Element define = defineDoc.getRootElement();
		StringBuffer xpath = new StringBuffer();
		if (!StringUtils.isEmpty(parentKey)) {
			xpath.append("//item[@key='").append(parentKey).append("']/item[");
		} else {
			xpath.append("//item[");
		}
		linkQueryXPath(xpath, query, LEAF_CNDS);
		xpath.append("]");

		List<Element> ls = define.selectNodes(xpath.toString());
		return toDictionaryItemList(ls);
	}

	@SuppressWarnings("unchecked")
	private List<DictionaryItem> getAllFolder(String parentKey, String query) {
		if (defineDoc == null) {
			return null;
		}
		Element define = defineDoc.getRootElement();

		StringBuffer xpath = new StringBuffer();
		if (!StringUtils.isEmpty(parentKey)) {
			xpath.append("//item[@key='").append(parentKey).append("']//item[");
		} else {
			xpath.append("//item[");
		}
		linkQueryXPath(xpath, query, FOLDER_CNDS);
		xpath.append("]");
		List<Element> ls = define.selectNodes(xpath.toString());
		return toDictionaryItemList(ls);
	}

	@SuppressWarnings("unchecked")
	private List<DictionaryItem> getChildFolder(String parentKey, String query) {
		if (defineDoc == null) {
			return null;
		}
		Element define = defineDoc.getRootElement();
		StringBuffer xpath = new StringBuffer();
		if (!StringUtils.isEmpty(parentKey)) {
			xpath.append("//item[@key='").append(parentKey).append("']/item[");
		} else {
			xpath.append("item[");
		}
		linkQueryXPath(xpath, query, FOLDER_CNDS);
		xpath.append("]");
		List<Element> ls = define.selectNodes(xpath.toString());
		return toDictionaryItemList(ls);
	}

	@SuppressWarnings("unchecked")
	private List<DictionaryItem> getChildLeaf(String parentKey, String query) {
		if (defineDoc == null) {
			return null;
		}
		Element define = defineDoc.getRootElement();

		StringBuffer xpath = new StringBuffer();
		if (!StringUtils.isEmpty(parentKey)) {
			xpath.append("//item[@key='").append(parentKey).append("']/item[");
		} else {
			xpath.append("item[");
		}
		linkQueryXPath(xpath, query, LEAF_CNDS);
		xpath.append("]");
		List<Element> ls = define.selectNodes(xpath.toString());
		return toDictionaryItemList(ls);
	}

}
