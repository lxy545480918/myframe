package com.liu.mvc.controller.core;

import com.liu.core.dictionary.Dictionary;
import com.liu.core.dictionary.DictionaryController;
import com.liu.core.dictionary.DictionaryItem;
import com.liu.util.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class DicLocator {

	@RequestMapping(value = "/**/{id}.dic", method = RequestMethod.GET)
	public List<DictionaryItem> get(@PathVariable String id, @RequestParam(required = false) String node,
			@RequestParam(required = false, defaultValue = "3") Integer sliceType, @RequestParam(required = false) String query, @RequestParam(value = "filter",required = false) String filter) {
		Dictionary dic = DictionaryController.instance().get(id);
		if (dic == null) {
			return null;
		}
		if("root".equals(node) || "0".equals(node)){
			node = null;
		}
		List<DictionaryItem> items = dic.getSlice(node, sliceType, query);
		if(!StringUtils.isEmpty(filter)){
			Map<String, String> map = JSONUtils.parse(filter, Map.class);
			List<DictionaryItem> filterItems = new ArrayList<>();
			for(DictionaryItem item:items){
				boolean fit = true;
				for(String k:map.keySet()) {
					String v = map.get(k);
					if(!v.equals(item.getProperty(k))){
						fit = false;
						break;
					}
				}
				if(fit){
					filterItems.add(item);
				}
			}
			return filterItems;
		}
		return items;
	}

}
