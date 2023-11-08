package com.cloud.entity;

import java.util.ArrayList;
import java.util.List;

public class CMSWCVM implements Cloneable{
    private int index;
    private int type;
    private double launchTime;
    private double shutdownTime;
    private List<Integer> taskList;    //储存task的index就可以了

    public CMSWCVM() {
        this.taskList = new ArrayList<>();
    }
    public CMSWCVM(int type, int index) {
        this.type = type;
        this.taskList = new ArrayList<>();
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Integer> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Integer> taskList) {
        this.taskList = taskList;
    }

    public double getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(double launchTime) {
        this.launchTime = launchTime;
    }

    public double getShutdownTime() {
        return shutdownTime;
    }

    public void setShutdownTime(double shutdownTime) {
        this.shutdownTime = shutdownTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public CMSWCVM clone() {
        CMSWCVM vm = new CMSWCVM();
        for(int t:this.taskList){
            vm.getTaskList().add(t);
        }
        vm.setType(type);
        vm.setShutdownTime(shutdownTime);
        vm.setLaunchTime(launchTime);
        vm.setIndex(index);
        return vm;
    }
}
