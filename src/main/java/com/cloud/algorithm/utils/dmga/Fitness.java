package com.cloud.algorithm.utils.dmga;

import org.workflowsim.CondorVM;
import org.workflowsim.FileItem;
import org.workflowsim.Task;
import org.workflowsim.utils.Parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



class TaskTime {

    public double startTime;
    public double stopTime;
    public double totalExcuteTime;
    public boolean isMeet;
    public double energy;
    
}

class VM {

    public double VMBusyTime;
}

class Fitness{
	List availableVMs;
    List avaliableTasks;
    List avalibaleTaskSorted;
    List Tasks;
    public static int ID=0;
    public static double[] VmPower=new double[] {10,6,4,2,1,0.5};//{0.2,0.15,0.1,0.08,0.05};
    public List getVmList() {
        return availableVMs;
    }

    public List getTaskList() {
        return avaliableTasks;
    }

    public Fitness(List TaskList, List VMList) {
        availableVMs = VMList;
        avaliableTasks = TaskList;
    }

    public boolean checkDuplicate(ArrayList<Integer> list, int value) {
        return list.contains(value);
    }

    public double evaluate(Chrome chrome) {
        List<TaskTime> taskTime = new ArrayList<>();
        List<VM> vmTime = new ArrayList<>();
        List<Task> taskList=new ArrayList<Task>();
        double totalTime=0.0;
        double energy=0.0;
        for (Iterator it = getVmList().iterator(); it.hasNext();) {
            Object vmList = it.next();
            VM v = new VM();
            v.VMBusyTime = 0;
            vmTime.add(v);
        }
        for (int i=0;i<chrome.order.length;i++) {
        	taskList.add((Task) getTaskList().get(chrome.order[i]));
        }
        int p = 0;
        for (Iterator it = taskList.iterator(); it.hasNext();) {
        	Task task = (Task) it.next();
        	TaskTime t = new TaskTime();
        	taskTime.add(t);
        	
        }
        for (Iterator it = taskList.iterator(); it.hasNext();) {

            TaskTime t = new TaskTime();
            Task task = (Task) it.next();

            int pos = (int) chrome.assignment[p] ;
            CondorVM vm = (CondorVM) getVmList().get(pos);

            if (task.getParentList().isEmpty()) // no parrent
            {
            	if(task.getCloudletId()==1)
            		t.startTime = 0.0;
            	else
            		t.startTime=vmTime.get(pos).VMBusyTime;
                double timeToExecute = task.getCloudletLength() / vm.getCurrentRequestedTotalMips();
                t.stopTime = timeToExecute * 1.02;
                t.stopTime += getInputSize(task) / vm.getBw()+t.startTime;
                t.totalExcuteTime = t.stopTime - t.startTime;
                energy+=t.totalExcuteTime*VmPower[vm.getId()];
                t.isMeet = true;
                //taskTime.add(t);
                taskTime.set(task.getCloudletId(), t);
                totalTime+= t.totalExcuteTime;
                VM tmVM = new VM();
                tmVM.VMBusyTime = t.stopTime;
                vmTime.set(vm.getId(), tmVM);

            } else {

                //find max parrent time
                double maxTime = -1;
                for (Task parrentTask : task.getParentList()) {
                    if (taskTime.get(parrentTask.getCloudletId() ).stopTime > maxTime) {
                    	//Log.printLine(taskTime.get(parrentTask.getCloudletId() - 1).stopTime);
                        maxTime = taskTime.get(parrentTask.getCloudletId() - 1).stopTime;
                    }

                }

                double tmp = vmTime.get(vm.getId()).VMBusyTime;
                if (tmp > maxTime) {
                    maxTime = tmp;
                }

                double timeToExecute = (task.getCloudletLength() / vm.getCurrentRequestedTotalMips());
                TaskTime t2 = new TaskTime();
                t2.startTime = maxTime;
                t2.stopTime = t2.startTime + (timeToExecute) * 1.02;
                t2.totalExcuteTime = t2.stopTime - t2.startTime;
                energy+=t2.totalExcuteTime*VmPower[vm.getId()];
                double maxTransferTime = 0;
                for (Task parrentTask : task.getParentList()) {
                    if (pos != (int) Math.round(chrome.assignment[parrentTask.getCloudletId() - 1] - 1)) {
                        tmp = getOutputSize(parrentTask) / vm.getBw();

                        maxTransferTime += tmp;

                    }
                }
                totalTime+= t2.totalExcuteTime;
                double inputFileTime = (getInputSize(task) / vm.getBw());
                //System.out.println("ID::: "+task.getCloudletId()+"--Time ::: "+inputFileTime);
                //t2.stopTime += inputFileTime;
                t2.stopTime += maxTransferTime;
                taskTime.add(t2);

                VM tmVM = new VM();
                tmVM.VMBusyTime = t2.stopTime;
                vmTime.set(vm.getId(), tmVM);
                //vmTime.get(vm.getId()).VMBusyTime = t2.stopTime; 
            }

            p++;
        }

        //find max fitness 
        double maxTaskTime = -1;
        for (TaskTime task : taskTime) {

            if (task.stopTime > maxTaskTime) {
                maxTaskTime += task.stopTime;
            }
        }

        //maxTaskTime = taskTime.get(taskTime.size() - 1).stopTime;
        energy+=(maxTaskTime*5-totalTime)*0.02;
        //System.out.println("energy : "+energy);
        chrome.makespan=maxTaskTime;
        chrome.energy=energy;
//        System.out.println("position : "+ Arrays.toString(position));
        return (maxTaskTime) * -1.0;

    }

    private double getOutputSize(Task t) {
        double outputSize = 0;
        for (Iterator it = t.getFileList().iterator(); it.hasNext();) {
            FileItem f = (FileItem) it.next();

            if (f.getType() == Parameters.FileType.OUTPUT) {
                outputSize += f.getSize();
            }
        }
        return outputSize / 1000000;
    }

    private double getInputSize(Task t) {
        double inputSize = 0;
        for (Iterator it = t.getFileList().iterator(); it.hasNext();) {
            FileItem f = (FileItem) it.next();
            if (f.getType() == Parameters.FileType.INPUT) {
                inputSize += f.getSize();
            }
        }
        return inputSize / 1000000;
    }


}