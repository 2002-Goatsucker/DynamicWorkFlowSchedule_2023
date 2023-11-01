package com.cloud.algorithm.utils.dmga;

import org.workflowsim.Task;

import java.util.ArrayList;
import java.util.List;

public class Tool {
	//处理机个数
	public static final int VmNum =6;
	//bestLCS的大小
	public static final int k =10;
	//任务数
	public static int TaskNum = 968;
	//迭代次数
	public static final int IterationNum =300;
	public static String datasetNameString="";
	//	//�ڵ�����
//	public static final int PointNum=100;
	//种群个数
	public static final int pNum=50;	
	public static List<Task> taskList = new ArrayList<Task>();	
	public static List<Task> getTaskList() {
		return taskList;
	}
	public static void setTaskList(List<Task> taskList) {
		Tool.taskList = taskList;
	}	
	//处理机列表
	public static List<Double> vmlist = new ArrayList<Double>();
	public static List<Double> getVmlist() {
		return vmlist;
	}
	public static void setVmlist(List<Double> vmlist) {
		Tool.vmlist = vmlist;
	}
	public static double getMeanMakespan(List<Task> taskList) {
		double totall=0.0;
		for(Task t:taskList ) {
			totall+=t.getCloudletLength();
		}
		return totall/taskList.size();
	}
	//每个处理机的功率
	public static double[] VmPower=new double[] {0.2,0.18,0.1,0.08,0.05};
	

	

}
