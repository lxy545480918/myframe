package com.liu.core.schema;

import com.google.common.collect.Maps;
import com.liu.core.controller.support.AbstractConfigurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Schema extends AbstractConfigurable{
    private static final long serialVersionUID = -271602734048406147L;

    public static final String KEY_GEN_AUTO = "identity";
    public static final String KEY_GEN_ASSIGN = "assigned";

    private String name;
    private String mapping;
    private String sort;
    private String pkey;
    private Map<String,SchemaItem> items = Maps.newLinkedHashMap();

    public Schema(){

    }

    public Schema(String id){
        this.id = id;
    }

    public void addItem(SchemaItem it){
        items.put(it.getId(), it);
    }

    public void removeItem(String id){
        items.remove(id);
    }

    public SchemaItem getItem(String id){
        return items.get(id);
    }

    public List<SchemaItem> getItems(){
        List<SchemaItem> sis = new ArrayList<>();
        sis.addAll(items.values());
        return sis;
    }

    public int getSize(){
        return items.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPkey() {
        return pkey;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }
}
