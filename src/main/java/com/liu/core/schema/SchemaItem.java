package com.liu.core.schema;

import com.liu.core.dictionary.Dictionary;
import com.liu.core.dictionary.DictionaryController;
import com.liu.util.StringValueParser;
import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.ExpressionProcessor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class SchemaItem implements Serializable {
    private static final long serialVersionUID = 4557229734515290036L;

    private String id;
    private String name;
    private boolean pkey;
    private String strategy = Schema.KEY_GEN_AUTO;
    private String type;
    private DictionaryIndicator dic;
    private Object defaultValue;
    private Integer length;
    private Integer precision;
    private Object maxValue;
    private Object minValue;
    private boolean allowBlank = true;
    private List<?> exp;
    private int displayMode = DisplayModes.ALL;
    private boolean hidden;
    private boolean update = true;


    private HashMap<String, Object> properties;

    public SchemaItem(){

    }

    public SchemaItem(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isCodedValue(){
        return !(dic == null);
    }

    public Boolean isEvalValue(){
        return (exp != null);
    }

    public boolean isAllowBlank() {
        return allowBlank;
    }

    public void setAllowBlank(boolean allowBlank) {
        this.allowBlank = allowBlank;
    }

    public String getType() {
        if(StringUtils.isEmpty(type)){
            return DataTypes.STRING;
        }
        return type;
    }

    private Class<?> getTypeClass(){
        return DataTypes.getTypeClass(getType());
    }

    public void setType(String type) {
        if(!DataTypes.isSupportType(type)){
            throw new IllegalArgumentException("type[" + type + "] is unsupported");
        }
        this.type = StringUtils.uncapitalize(type);
    }

    public void setExp(List<Object> exp) {
        this.exp = exp;
    }

    public Integer getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(Integer displayMode) {
        this.displayMode = displayMode;
    }

    public void setDisplay(Integer displayMode) {
        setDisplayMode(displayMode);
    }

    public Integer getDisplay() {
        return getDisplayMode();
    }

    public DictionaryIndicator getDic() {
        return dic;
    }

    public void setDic(DictionaryIndicator dic) {
        this.dic = dic;
    }

    public boolean isPkey() {
        return pkey;
    }

    public void setPkey(boolean pkey) {
        this.pkey = pkey;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Object getDefaultValue() {
        if(defaultValue == null){
            return defaultValue;
        }
        if(isCodedValue()){
            HashMap<String,String> obj = new HashMap<>();
            String key = ConversionUtils.convert(parseConfigValue(defaultValue), String.class);
            String text =  toDisplayValue(key);
            obj.put("key", key);
            obj.put("text", text);
            return obj;
        }
        return parseConfigValue(defaultValue);
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue =defaultValue;
    }

    public Object getMaxValue() {
        return parseConfigValue(maxValue);
    }

    public void setMaxValue(Object maxValue) {
        this.maxValue = maxValue;
    }

    public Object getMinValue() {
        return parseConfigValue(minValue);
    }

    public void setMinValue(Object minValue) {
        this.minValue = minValue;
    }

    private Object parseConfigValue(Object v){
        Object val = v;
        if(v instanceof String){
            val = StringValueParser.parse((String)v, getTypeClass());
        }
        else{
            val = ConversionUtils.convert(v, getTypeClass());
        }
        return val;
    }

    public String toDisplayValue(Object v){
        String key = ConversionUtils.convert(v, String.class);
        if(isCodedValue() && !StringUtils.isEmpty(key)){
            Dictionary d = DictionaryController.instance().get(dic.getId());
            String text = "";
            if(key.indexOf(",") == -1){
                text = d.getText(key);
            }else{
                String[] keys = key.split(",");
                StringBuffer sb = new StringBuffer();
                for(String s : keys){
                    sb.append(",").append(d.getText(s));
                }
                text = sb.substring(1);
            }
            return text;
        }
        return key;
    }

    public Object toPersistValue(Object source){
        return DataTypes.toTypeValue(getType(),source);
    }

    public Object eval(){
        if(!isEvalValue()){
            return null;
        }
        return toPersistValue(ExpressionProcessor.instance().run(exp));
    }

    public Object eval(String lang){
        if(exp != null){
            return toPersistValue(ExpressionProcessor.instance(lang).run(exp));
        }
        return null;
    }

    public void setProperty(String nm, Object v){
        if(properties == null){
            properties = new HashMap<String, Object>();
        }
        properties.put(nm, v);
    }

    public Object getProperty(String nm){
        if(properties == null){
            return null;
        }
        return properties.get(nm);
    }

    public HashMap<String, Object> getProperties(){
        if(properties != null && properties.isEmpty()){
            return null;
        }
        return properties;
    }

    public boolean hasProperty(String nm){
        if(properties != null){
            return properties.containsKey(nm);
        }
        return false;
    }

    public void setUpdate(boolean canUpdate){
        this.update = canUpdate;
    }

    public boolean isUpdate(){
        if(pkey || !update){
            return false;
        }
        return true;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
