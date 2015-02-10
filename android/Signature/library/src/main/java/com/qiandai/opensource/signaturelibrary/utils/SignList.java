package com.qiandai.opensource.signaturelibrary.utils;

import java.util.ArrayList;
import java.util.List;


public class SignList {
	/**
	 * 签名文件
	 */
	private List<Integer> signList;
	/**
	 * 保存笔迹的循环对列
	 */
	private LoopArray loopArray;

	/**
	 * 保存最近N次的输入
	 */
	private int size = 3;
	/**
	 * 最终的笔迹
	 */
	private int[] finalSign;
	public SignList(){
		initloopArray();
		signList=new ArrayList<Integer>();
	}
	
	public List<Integer> getSignList() {
		return signList;
	}

	public void setSignList(List<Integer> signList) {
		this.signList = signList;
	}

	public LoopArray getLoopArray() {
		return loopArray;
	}

	public void setLoopArray(LoopArray loopArray) {
		this.loopArray = loopArray;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int[] getFinalSign() {
		return finalSign;
	}

	public void setFinalSign(int[] finalSign) {
		this.finalSign = finalSign;
	}

	/**
	 * 保存清除掉的笔迹
	 * 并清空原列表
	 */
	public void saveCleanedSign() {
		signList.add(-1);
		signList.add(-1);

		int[] num=new int[signList.size()];
		for(int i=0;i<num.length;i++){
			num[i]= signList.get(i);
		}
		loopArray.add(num);
		signList.clear();
	}
	/**
	 * 保存最终的笔迹
	 */
	public void saveFinalSign() {
		signList.add(-1);
		signList.add(-1);

		finalSign=new int[signList.size()];
		for(int i=0;i<finalSign.length;i++){
			finalSign[i]= signList.get(i);
		}
		signList.clear();
	}
	/**
	 *  初始化保存笔迹的循环对列
	 */
	public void initloopArray(){
		loopArray = new LoopArray(size);
	}
}
