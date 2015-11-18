package com.liu.core.schema;

import com.liu.util.JSONUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryIndicator implements Serializable {
    private static final long serialVersionUID = 2542057660051407081L;
    private String id;
    private String render;
    private String parentKey;
    private Integer slice;
    private List<?> filter;
    private boolean internal;
    private Map<String, String> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRender() {
        return render;
    }

    public void setRender(String render) {
        this.render = render;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public Integer getSlice() {
        return slice;
    }

    public void setSlice(Integer slice) {
        this.slice = slice;
    }

    public List<?> getFilter() {
        return filter;
    }

    public void setFilter(String sFilter) {
        this.filter = JSONUtils.parse(sFilter, List.class);
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public Map<String, String> getProperties(){
        return properties;
    }

    public void setProperty(String nm, String v){
        if(properties == null){
            properties = new HashMap<>();
        }
        properties.put(nm, v);
    }

}
