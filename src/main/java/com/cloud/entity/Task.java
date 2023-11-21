package com.cloud.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Task implements Cloneable{
    private int index;
    private double inputSize;
    private double outputSize;
    private double referTime;
    private double startTime;
    private double finalTime;
    private List<Integer> successor;
    private List<Integer> predecessor;
    private double rank;
    private int depth;
    private int insType;
    public Task(int index){
        this.index = index;
        successor=new LinkedList<>();
        predecessor=new LinkedList<>();
        insType = -1;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getInputSize() {
        return inputSize;
    }

    public void setInputSize(double inputSize) {
        this.inputSize = inputSize;
    }

    public double getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(double outputSize) {
        this.outputSize = outputSize;
    }

    public double getReferTime() {
        return referTime;
    }

    public void setReferTime(double referTime) {
        this.referTime = referTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(double finalTime) {
        this.finalTime = finalTime;
    }

    public List<Integer> getSuccessor() {
        return successor;
    }

    public void setSuccessor(List<Integer> successor) {
        this.successor = successor;
    }

    public List<Integer> getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(List<Integer> predecessor) {
        this.predecessor = predecessor;
    }

    public List<Integer> getSuccessors() {
        return successor;
    }

    //********************关于CMSWC算法的部分，其他代码没动******************************
    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public int getInsType() {
        return insType;
    }

    public void setInsType(int insType) {
        this.insType = insType;
    }

    //******************************************************************************
    @Override
    public Task clone() {
        Task task=new Task(index);
        task.setInputSize(inputSize);
        task.setOutputSize(outputSize);
        task.setReferTime(referTime);
        task.setSuccessor(successor);
        task.setPredecessor(predecessor);
        //加上两个属性，不影响原代码
        task.setRank(rank);
        task.setInsType(insType);
        return task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return index == task.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}