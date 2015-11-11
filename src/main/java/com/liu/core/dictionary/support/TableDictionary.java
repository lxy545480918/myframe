package com.liu.core.dictionary.support;

import nw.core.dictionary.CodeRule;
import nw.core.dictionary.DictionaryItem;
import nw.core.dictionary.SliceTypes;
import nw.core.schema.DataTypes;
import nw.util.ApplicationContextHolder;
import nw.util.JSONUtils;
import nw.util.exp.ExpressionProcessor;
import nw.util.exp.exception.ExprException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@SuppressWarnings("unchecked")
public class TableDictionary extends Dictionary{
	private static final long serialVersionUID = -997610255379902104L;
	private static final Logger LOGGER = LoggerFactory.getLogger(TableDictionary.class);
	protected CodeRule codeRule;
	protected String parent;
	protected String entityName;
	protected String keyField = "id";
	protected String textField = "text";
	protected String sortField;
	protected boolean ignoreSearchFieldExPrefix;
	protected String filter;
	protected String where = "";
	protected String iconCls;
	protected boolean supportRemote = true;
	protected String queryType = "Query";
	protected String searchFieldType="string";
	protected HashSet<String> folders;
	protected LinkedHashMap<String, String> propFields;
	protected boolean distinct = false;

	private String sessionFactory = "sessionFactory";
	
	@Override
	public void init() {
		if(!queryOnly && supportRemote){
			List<DictionaryItem> ls = initAllItems();
			for(DictionaryItem di:ls){
				addItem(di);
			}
			if(!StringUtils.isEmpty(parent)){
				initNodeToFolder(ls);       //设置节点的 folder为true
			}
		}
	}
	
	public List<DictionaryItem> initAllItems(){
		List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
		SessionFactory sf = ApplicationContextHolder.getBean(sessionFactory, SessionFactory.class);
		Session ss = null;
		try{
			ss = sf.openSession();
			String sql = spellSql();
			Query q = createQuery(ss, sql);
			List<Object[]> records = q.list();
			for(Iterator<Object[]> it = records.iterator(); it.hasNext();){
				Object[] r = it.next();
				DictionaryItem dictionaryItem = parseDicItem(r);
				ls.add(dictionaryItem);
			}
		}catch(Exception e){
			LOGGER.error("init dic[{}] items failed, db error \r ", id, e);
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return ls;
	}

	@Override
	public List<DictionaryItem> getSlice(String parentKey,int sliceType,String query) {
		if(StringUtils.isEmpty(query)){
			switch(sliceType){
				case SliceTypes.ALL:
					return getAllItems(parentKey);
				case SliceTypes.ALL_LEAF:	// for not layer dic
					return getAllLeaf(parentKey);
				case SliceTypes.CHILD_ALL:	//	for layer dic
					return getAllChild(parentKey);
				default:
					return null;
			}
		}else{
			return query(parentKey,query);
		}
	}

	/**
	 * 用qs去查询字典项，不带符号则默认按照searchField去模糊查询，如果searchField没有设置
	 * 则默认为textField。如果qs以.开头则按照searchFieldEx去查询，以/开头则以keyField去查询
	 * @param parentKey
	 * @param qs
	 * @return
	 */
	
	public List<DictionaryItem> query(String parentKey,String qs) {
		List<DictionaryItem> ls = new ArrayList<>();
		String curSF = ignoreSearchFieldExPrefix?searchFieldEx:searchField;
		qs = qs.toLowerCase();
		if(qs.charAt(0) == searchKeySymbol){
			curSF = keyField;
			qs = qs.substring(1);
		}
		else if(qs.charAt(0) == searchExSymbol){
			curSF = searchFieldEx;
			qs = qs.substring(1);
		}
		SessionFactory sf = ApplicationContextHolder.getBean(sessionFactory, SessionFactory.class);
		Session ss = null;
		try{
			ss = sf.openSession();
			StringBuffer condition = new StringBuffer("lower(").append(curSF).append(")");
			if(qs.startsWith("=")){
				qs = qs.substring(1);
				if(curSF.equals(searchField)&& DataTypes.isNumberType(searchFieldType)){
					condition = new StringBuffer(curSF).append("=").append(qs);
				}else{
					condition.append("='").append(qs).append("'");
				}
			}
			else{
				condition.append(" like '").append(qs).append("%'");
			}
			if(!StringUtils.isEmpty(parentKey)){
				condition.append(" and substring(").append(searchField).append(",1,").append(parentKey.length())
					.append(")='").append(parentKey).append("'");
			}
			String sql = spellSql(condition.toString());
			Query q = createQuery(ss, sql);
			List<Object[]> records = q.list();
			int rowCount = records.size();
			for(int i = 0;i < rowCount; i ++){
				Object[] r = records.get(i);
				DictionaryItem di = parseDicItem(r);
				ls.add(di);
			}
		}
		catch(Exception e){
			LOGGER.error("query failed from table dic:{}", id, e);
		}
		finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return ls;
	}
	
	protected List<DictionaryItem> getAllItemsFromCacheList(String parentKey){
		List<DictionaryItem> list = itemsList();
		if(StringUtils.isEmpty(parentKey)){
			return list;
		}
		List<DictionaryItem> ls = new ArrayList<>();
		for(DictionaryItem di:list){
			String p = (String) di.getProperty("parent");
			if(!StringUtils.isEmpty(p) && p.contains(parentKey)){
				ls.add(di);
			}
		}
		return ls;
	}
	
	protected List<DictionaryItem> getAllChildFromCacheList(String parentKey){
		return getAllChildFromCacheList(parentKey, itemsList());
	}
	
	protected List<DictionaryItem> getAllChildFromCacheList(String parentKey, List<DictionaryItem> itemList){
		if(codeRule == null && StringUtils.isEmpty(parent)){
			return itemList;
		}	
		List<DictionaryItem> ls = new ArrayList<>();
		if(StringUtils.isEmpty(parentKey)){
			if(codeRule != null){
				int firstCodeLength = codeRule.getLayerLength(0);
				for(DictionaryItem di:itemList){
					if(di.getKey().length() == firstCodeLength){
						ls.add(di);
					}
				}
			}else{
				if(!StringUtils.isEmpty(parent)){
					for(DictionaryItem di : itemList){
						String p = (String) di.getProperty("parent");
						if(StringUtils.isEmpty(p) || getItem(p)==null){
							ls.add(di);
						}
					}
				}
			}
			return ls;
		}
		for(DictionaryItem di:itemList){
			String p = (String) di.getProperty("parent");
			if(!StringUtils.isEmpty(p) && parentKey.equals(p)){
				ls.add(di);
			}
		}
		return ls;
	}
	
	protected List<DictionaryItem> getAllLeafFromCacheList(String parentKey){
		List<DictionaryItem> list = itemsList();
		if(StringUtils.isEmpty(parentKey)){
			return list;
		}
		List<DictionaryItem> ls = new ArrayList<>();
		for(DictionaryItem di:list){
			String p = (String) di.getProperty("parent");
			String f = (String) di.getProperty("folder");
			if(StringUtils.isEmpty(f) && !StringUtils.isEmpty(p) && p.contains(parentKey)){
				ls.add(di);
			}
		}
		return ls;
	}
	
	
	/**
	 * 用于非树形下拉框的请求，忽略codeRule的配置
	 * @param parentKey
	 * @return
	 */
	protected List<DictionaryItem> getAllLeaf(String parentKey){
		if(!queryOnly){
			checkItems();
			return getAllLeafFromCacheList(parentKey);
		}
		return initAllItems();
	}
	
	protected List<DictionaryItem> getAllItems(String parentKey){
		if(!queryOnly){
			checkItems();
			return getAllItemsFromCacheList(parentKey);
		}
		List<DictionaryItem> ls = new ArrayList<>();
		if(codeRule == null){
			return ls;
		}		
		if(StringUtils.isEmpty(parentKey)){
			ls.addAll(initAllItems());
		}
		else{
			int curLayer = codeRule.indexOfLayer(parentKey);
			if(curLayer == -1  || curLayer == codeRule.getLayerCount() - 1){
				return ls;
			}
			int curLen = codeRule.getLayerLength(curLayer);
			String condition = new StringBuffer("substring(").append(keyField).append(",1,:curLen)=:parentKey").toString();
			String sql = spellSql(condition);
			SessionFactory sf = ApplicationContextHolder.getBean(sessionFactory, SessionFactory.class);
			Session ss = null;
			try{
				ss = sf.openSession();
				Query q = createQuery(ss, sql);
				q.setInteger("curLen", curLen);
				q.setString("parentKey", parentKey);
				List<Object[]> records = q.list();
				for(Object[] r : records){
					ls.add(parseDicItem(r));
				}
			}
			catch(Exception e){
				LOGGER.error("Get dictionary {} item failed.", id, e);
			}
			finally{
				if(ss.isOpen()){
					ss.close();
				}
			}
			
		}
		return ls;
	}
	
	protected List<DictionaryItem> getAllChild(String parentKey){
		if(!queryOnly){
			checkItems();
			return getAllChildFromCacheList(parentKey);
		}
		if(codeRule == null){
			if(!StringUtils.isEmpty(parent)){
				if(StringUtils.isEmpty(parentKey)){
					synchronized(this){
						List<DictionaryItem> itemList = initAllItems();
						initNodeToFolder(itemList);
						List<DictionaryItem> ls = getAllChildFromCacheList(parentKey, itemList);
						return ls;
					}
				}else{
					return getItemsFromDBByParentKey(parentKey);
				}
			}
			return initAllItems();
		}
		List<DictionaryItem> ls = new ArrayList<>();
		SessionFactory sf = ApplicationContextHolder.getBean(sessionFactory, SessionFactory.class);
		Session ss = null;
		try{
			ss = sf.openSession();
			Query q  = null;
			if(StringUtils.isEmpty(parentKey)){
				String condition = new StringBuffer("length(").append(keyField).append(")=:len").toString();
				String sql = spellSql(condition);
				q = createQuery(ss, sql);
				q.setInteger("len", codeRule.getLayerLength(0));
			}
			else{
				int curLayer = codeRule.indexOfLayer(parentKey);
				if(curLayer == -1  || curLayer == codeRule.getLayerCount() - 1){
					return ls;
				}
				int curLen = codeRule.getLayerLength(curLayer);
				int nextLen = codeRule.getLayerLength(curLayer + 1);
				String condition = new StringBuffer("length(").append(keyField).append(")=:nextLen and substring(")
						.append(keyField).append(",1,:curLen)=:parentKey").toString();
				String sql = spellSql(condition);
				q = createQuery(ss, sql);
				q.setInteger("curLen", curLen);
				q.setInteger("nextLen",nextLen);
				q.setString("parentKey", parentKey);
			}
			List<Object[]> records = q.list();
			for(Object[] r: records){
				ls.add(parseDicItem(r));
			}
		}
		catch(Exception e){
			LOGGER.error("Failed to initialize dictionary {}.", id, e);
		}
		finally{
			if(ss.isOpen()){
				ss.close();
			}
		}
		return ls;
	}
	
	protected String spellSql(){
		return spellSql(null);
	}
	
	protected String spellSql(String condition){
		StringBuffer props = new StringBuffer();
		if(propFields != null){
			Set<String> fields = propFields.keySet();
			for (Object fld : fields) {
				props.append(",").append(fld);
			}
		}
		StringBuffer sql = new StringBuffer("select ").append(distinct?"distinct ":"");
		sql.append(keyField).append(",").append(textField).append(props);
		if(!StringUtils.isEmpty(parent)){
			sql.append(",").append(parent);
		}		
		sql.append(" from ").append(entityName);
		if(queryOnly){
			try {
				setFilter(filter);	// init every time.
			} catch (ExprException e) {
				
			}
		}
		String cnd = where;
		if(!StringUtils.isEmpty(condition)){
			cnd = StringUtils.isEmpty(where)?" where ":where+" and ";
			cnd += condition;
		}
		sql.append(cnd);
		if(sortField != null){
			sql.append(" order by ").append(sortField);
		}
		return sql.toString();
	}
	
	protected DictionaryItem parseDicItem(Object[] r){
		String key = String.valueOf(r[0]);
		String text = String.valueOf(r[1]);				
		DictionaryItem dictionaryItem = new DictionaryItem(key,text);
		if(propFields != null){
			Set<String> ps = propFields.keySet();
			int i = 2;
			for(String p:ps){
				if(r[i] != null){
					dictionaryItem.setProperty(StringUtils.isEmpty(propFields.get(p))?p:propFields.get(p), r[i]);
				}
				i++;
			}
		}
		if(!StringUtils.isEmpty(iconCls)){
			dictionaryItem.setProperty("iconCls",iconCls);
		}
		if(codeRule != null){
			String parentKey = codeRule.getParentKey(key);
			dictionaryItem.setProperty("parent", parentKey);
			if(codeRule.isLeaf(key)){
				dictionaryItem.setLeaf(true);
			}
		}else{
			if(!StringUtils.isEmpty(parent)){
				String parentKey = r[r.length-1]==null?null:String.valueOf(r[r.length-1]);
				if(!StringUtils.isEmpty(parentKey)){
					dictionaryItem.setProperty("parent", parentKey);
				}
			}else{
				dictionaryItem.setLeaf(true);
			}
		}
		return dictionaryItem;
	}
	
//	public static void reloadDicsByEntryName(String entryName){
//		Document defineDoc = Dictionaries.instance().getDefineDoc();
//		Element define = defineDoc.getRootElement();
//		List<Element> els = define.selectNodes("dic[@class='TableDictionary' or @class='ComposeDictionary']");
//		for(Element e : els){
//			String id = e.attributeValue("id");
//			Document doc = Dictionaries.instance().getDic(id).getDefineDoc();
//			if(doc == null){
//				continue;
//			}
//			Element root = doc.getRootElement();
//			if(entryName.equals(root.attributeValue("entry"))){
//				Dictionaries.instance().reload(id);
//				LOGGER.info("dic[" + id + "] reload.");
//			}
//		}		
//	}
	
	public List<DictionaryItem> getItemFromDBByText(String text){
		List<DictionaryItem> dicItemList = null;
		String con = new StringBuffer(textField).append("=:text").toString();
		String sql = spellSql(con);
		SessionFactory sf = ApplicationContextHolder.getBean(sessionFactory, SessionFactory.class);
		Session ss = null;
		try{
			ss = sf.openSession();
			Query q  = createQuery(ss, sql);
			q.setString("text", text);
			List<Object[]> l = q.list();
			if(l.size()>0){
				dicItemList=new ArrayList<>();
				for(int i=0;i<l.size();i++){
					dicItemList.add(parseDicItem(l.get(i)));
				}
			}
		}catch (Exception e) {
			LOGGER.error("get {} dicItem by text["+text+"] failed.", id, e);
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return dicItemList;
	}
	
	public DictionaryItem getItemFromDB(String key){
		DictionaryItem dictionaryItem = null;
		String con = new StringBuffer(keyField).append("=:key").toString();
		String sql = spellSql(con);
		SessionFactory sf = ApplicationContextHolder.getBean(sessionFactory, SessionFactory.class);
		Session ss = null;
		try{
			ss = sf.openSession();
			Query q  = createQuery(ss, sql);
			q.setString("key", key);
			List<Object[]> l = q.list();
			if(l.size()>0){
				dictionaryItem = parseDicItem(l.get(0));
			}
		}catch (Exception e) {
			LOGGER.error("get {} dicItem by key["+key+"] failed.", id, e);
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return dictionaryItem;
	}
	
	public List<DictionaryItem> getItemsFromDB(String keys){
		List<DictionaryItem> list = new ArrayList<>();
		String con = new StringBuffer(keyField).append(" in (").append(keys).append(")").toString();
		String sql = spellSql(con);
		SessionFactory sf = ApplicationContextHolder.getBean(sessionFactory, SessionFactory.class);
		Session ss = null;
		try{
			ss = sf.openSession();
			Query q  = createQuery(ss, sql);
			List<Object[]> l = q.list();
			if(l.size()>0){
				for(Object[] o:l){
					DictionaryItem dictionaryItem = parseDicItem(o);
					list.add(dictionaryItem);
				}
			}
		}catch (Exception e) {
			LOGGER.error("get {} dicItems by keys["+keys+"] failed.", id, e);
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return list;
	}
	
	public List<DictionaryItem> getItemsFromDBByParentKey(String parentKey){
		List<DictionaryItem> list = new ArrayList<>();
		String p = parent;
		int index = parent.toLowerCase().lastIndexOf(" as ");
		if(index > -1){
			p = parent.substring(0, index);
		}
		String con = new StringBuffer(p).append("='").append(parentKey).append("'").toString();
		String sql = spellSql(con);
		SessionFactory sf = ApplicationContextHolder.getBean(sessionFactory, SessionFactory.class);
		Session ss = null;
		try{
			ss = sf.openSession();
			Query q  = createQuery(ss, sql);
			List<Object[]> l = q.list();
			if(l.size()>0){
				for(Object[] o:l){
					DictionaryItem dictionaryItem = parseDicItem(o);
					//dictionaryItem.setProperty("folder", "true");
					if(!folders.contains(dictionaryItem.getKey())){
						dictionaryItem.setLeaf(true);
					}
					list.add(dictionaryItem);
				}
			}
		}catch (Exception e) {
			LOGGER.error("get {} dicItems by parentKey["+parentKey+"] failed.", id, e);
		}finally{
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
		return list;
	}
	
	private Query createQuery(Session ss, String q){
		if("SQLQuery".equals(queryType)){
			return ss.createSQLQuery(q);
		}else{
			return ss.createQuery(q);
		}
	}
	
	@Override
	public List<String> getKey(String text) {
		if(!queryOnly){
			checkItems();
			return super.getKey(text);
		}else{
			List<String> list = new ArrayList<String>();
			List<DictionaryItem> li = getItemFromDBByText(text);
			if(li !=null){
				for(DictionaryItem d:li){
					list.add(d.getKey());
				}
			}
			return list;
		}
	}
	
	@Override
	public String getText(String key){
		if(!queryOnly){
			DictionaryItem di = getItem(key);
			return di==null?"":di.getText();
		}else{
			DictionaryItem di = getItemFromDB(key);
			if(di != null){
				return di.getText();
			}
			return "";
		}
	}
	
	public String getWholeText(String key, int includeParentMinLen){
		StringBuffer text = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		sb.append(",'").append(key).append("'");
		String pkey = codeRule.getParentKey(key);
		while (!StringUtils.isEmpty(pkey) && pkey.length() >= includeParentMinLen){
			sb.append(",'").append(pkey).append("'");
			pkey = codeRule.getParentKey(pkey);
		}
		List<DictionaryItem> list = getItemsFromDB(sb.substring(1));
		Collections.sort(list, new Comparator<DictionaryItem>(){
			public int compare(DictionaryItem d1, DictionaryItem d2) {
				if(d1.getKey().length()>d2.getKey().length()){
					return 1;
				}
				return -1;
			}
		});	//排序 把父类排前面
		for(int i=0;i<list.size();i++){
			DictionaryItem di = list.get(i);
			if((i+1)<list.size()){
				DictionaryItem dd = list.get(i+1);
				if(dd.getText().contains(di.getText())){
					di.setProperty("___removeText___", "1");
				}
			}
		}
		for(int i=0;i<list.size();i++){
			DictionaryItem di = list.get(i);
			if(!di.getProperty("___removeText___").equals("1")){
				text.append(di.getText());
			}
		}
		return text.toString();
	}
	
	public boolean hasCodeRule(){
		return codeRule!=null;
	}
	
	private void initNodeToFolder(List<DictionaryItem> list){
		folders = new HashSet<String>();
		for(Iterator<DictionaryItem> it=list.iterator();it.hasNext();){
			DictionaryItem di = it.next();
			String p = (String) di.getProperty("parent");
			DictionaryItem pdi = getItem(p);
			if(pdi != null){
				folders.add(pdi.getKey());
			}
		}
		for (DictionaryItem dictionaryItem : list) {
			if(!folders.contains(dictionaryItem.getKey())){
				dictionaryItem.setLeaf(true);
			}
		}
	}
	
	@Override
	public DictionaryItem getItem(String key) {
		if(!queryOnly){
			checkItems();
			return super.getItem(key);
		}else{
			return getItemFromDB(key);
		}
	}
	
	private void checkItems() {
		if(!supportRemote && getItems().size() == 0){
			List<DictionaryItem> ls = initAllItems();
			for(DictionaryItem di:ls){
				addItem(di);
			}
			if(!StringUtils.isEmpty(parent)){
				initNodeToFolder(ls);       //设置节点的 folder为true
			}	
		}
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public void setSearchFieldType(String searchFieldType){
		this.searchFieldType=searchFieldType;
	}
	
	public void setEntityName(String entityName){
		this.entityName = entityName;
	}
	
	public String getEntityName(){
		return entityName;
	}
	public String getParent() {
		return parent;
	}
	
	public String getKeyField() {
		return keyField;
	}

	public String getTextField() {
		return textField;
	}

	public String getSortField() {
		return sortField;
	}

	public String getIconCls() {
		return iconCls;
	}
	
	public String getFilter() {
		return filter;
	}
	
	@Deprecated
	public void setEntry(String entry) {
		setEntityName(entry);
	}
	
	@Deprecated
	public String getEntry(){
		return getEntityName();
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public void setIgnoreSearchFieldExPrefix(boolean ignoreSearchFieldExPrefix) {
		this.ignoreSearchFieldExPrefix = ignoreSearchFieldExPrefix;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	public boolean isSupportRemote() {
		return supportRemote;
	}
	
	public void setSupportRemote(boolean supportRemote) {
		this.supportRemote = supportRemote;
	}
	
	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	public void setCodeRule(String sCodeRule) {
		if(!StringUtils.isEmpty(sCodeRule)){
			codeRule = new CodeRule(sCodeRule);
		}
	}
	
	public void setFilter(String filter) throws ExprException{
		if(!StringUtils.isEmpty(filter)){
			this.filter = filter;
			List<?> exp =  JSONUtils.parse(filter, List.class);
			where  = " where " + ExpressionProcessor.instance().toString(exp);
		}
	}
	
	public void setPropField(String nm, String v){
		if(propFields == null){
			propFields = new LinkedHashMap<>();
		}
		propFields.put(nm, v);
	}

}
