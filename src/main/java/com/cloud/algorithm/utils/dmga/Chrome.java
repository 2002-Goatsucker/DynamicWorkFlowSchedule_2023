package com.cloud.algorithm.utils.dmga;

class Chrome extends Individual {
	public int[] order=new int[Tool.TaskNum];
	public int[] assignment=new int[Tool.TaskNum];
	public double pc=0.8;
	public double pm=0.6;
}