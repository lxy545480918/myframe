package com.liu.core.dictionary;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author wuk
 *	数据库字典的层级定义类
 */
public class CodeRule implements Serializable{
	/**
	 * 规则的字符串描述 如 4,2,3,3 为4位+2位+3位+3位的规则，例如杭州市3301 上城区330101 
	 * 再往下就是 330101001，330101001001 叶节点为最大长度4+2+3+3=12位
	 */
	private String codeDefine;
	/**
	 * 以4,2,3,3为例，保存4,6,9,12数字的数组
	 */
	private int[] layerLens;
	private String[] define;
	/**
	 * key为总长度如 4,6,9,12，text为所在层数 1,2,3,4
	 */
	private HashMap<Integer,Integer> layersMapping;
	/**
	 * 总层数
	 */
	private int count;
	/**
	 * 最大长度，即所有的层数的数字定义之和
	 */
	private int maxLen;
	
	public CodeRule(String cd){
		codeDefine = cd;
		define = cd.split(",");
		count = define.length;
		layerLens = new int[count];
		layersMapping = new HashMap<Integer,Integer>();
		int i = 0;
		for(String s : define){
			int len = Integer.parseInt(s);
			maxLen += len;
			if(i == 0){
				layerLens[i] = len;
				layersMapping.put(len,i);
			}
			else{
				layerLens[i] = layerLens[i - 1] + len;
				layersMapping.put(layerLens[i - 1] + len,i);
			}
			i ++;
		}
	}
	
	public int getLayerCount(){
		return count;
	}
	
	public int getLayerLength(int i){
		if(i < count){
			return layerLens[i];
		}
		return 0;
	}
	
	public boolean isLeaf(String key){
		return key.length() == maxLen;
	}
	
	public int indexOfLayer(String key){
		if(StringUtils.isEmpty(key)){
			return -1;
		}
		int keySize = key.length();
		if(layersMapping.containsKey(keySize)){
			return layersMapping.get(keySize);
		}
		return -1;
	}
	
	public String getParentKey(String key){
		if(StringUtils.isEmpty(key)){
			return "";
		}
		int index = indexOfLayer(key);
		if(index < 1){
			return "";
		}
		index--;
		return key.substring(0, layerLens[index]);
	}
	
	public int getNextLength(int length) {
		for (int i = 0; i < layerLens.length; i++) {
			if (layerLens[i] == length) {
				if (i == layerLens.length - 1) {
					return layerLens[i];
				}
				return layerLens[i + 1];
			}
		}
		return -1;
	}

	public int getParentLength(int length) {
		for (int i = 0; i < layerLens.length; i++) {
			if (layerLens[i] == length) {
				if (i == 0) {
					return layerLens[i];
				}
				return layerLens[i - 1];
			}
		}
		return -1;
	}
	
	public String toString(){
		return codeDefine;
	}
}
