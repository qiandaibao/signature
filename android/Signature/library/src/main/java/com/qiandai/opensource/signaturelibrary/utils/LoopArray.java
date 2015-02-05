package com.qiandai.opensource.signaturelibrary.utils;

/**
 * 循环队列
 */
public class LoopArray {

	private int size = 0;

	/**
	 * 最早的对象的下标
	 */
	private int head = -1;
	/**
	 * 最后一个对象的下标
	 */
	private int end = -1;

	private Object[] mArray = null;

	private int total = 0;
	
	public LoopArray(int size) {
		this.size = size;
		mArray = new Object[size];
	}

	public void add(Object obj) {
		total++;
		if (head == -1) {
			head = 0;
		}
		end = (end + 1) % size;
		mArray[end] = obj;

		if (end != head) {
			return;
		} else {
			int tem = (head + 1) % size;
			if (mArray[tem] != null) {
				head = tem;
			}
		}
	}

	public Object[] getAll() {
		Object[] result = new Object[total%size];
		for (int i = 0; i < result.length; i++) {
			result[i] = mArray[(head + i) % size];
		}
		return result;
	}

	public boolean isEmpty() {
		if (head == -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 清空原有数据
	 */
	public void clear() {
		for (int i = 0; i < size; i++) {
			mArray[i] = null;
		}
	}

}