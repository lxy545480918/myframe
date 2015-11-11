package com.liu.core.dictionary;

public class SliceTypes {
	/**
	 * 所有节点
	 */
	public static final int ALL = 0;
	/**
	 * 所有叶子节点
	 */
	public static final int ALL_LEAF = 1;
	/**
	 * 所有文件夹节点
	 */
	public static final int ALL_FOLDER = 2;
	/**
	 * 所有子级节点
	 */
	public static final int CHILD_ALL = 3;
	/**
	 * 所有子级叶子节点
	 */
	public static final int CHILD_LEAF = 4;
	/**
	 * 所有子级文件夹节点
	 */
	public static final int CHILD_FOLDER = 5;

	/**
	 * 跨层获取子节点
	 */
	public static final int CHILD_LAYER = 6;
}
