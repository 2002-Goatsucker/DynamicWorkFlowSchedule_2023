package com.cloud.entity;

import com.cloud.utils.ChromosomeUtils;

import java.util.*;

public class Chromosome implements Cloneable {

    public int ID;
    private int[] task;
    private int[] task2ins;
    private double cost;
    private double makeSpan;
    private double crowding;
    private double[] start = new double[100];

    private int region = -1;
    private double[] end = new double[100];
    public double[] launchTime=new double[ReadOnlyData.insNum];
    public double[] shutdownTime=new double[ReadOnlyData.insNum];

    private final List<Chromosome> better = new LinkedList<>();
    private final List<Chromosome> poor = new LinkedList<>();

    private int betterNum;
    private int poorNum;

    private List<Integer> existIns = new ArrayList<>();
    private List<Integer> unallocatedIns = new ArrayList<>();
    public Chromosome() {

    }

    public Chromosome(int[] order, int[] task2ins) {
        this.setTask(order);
        this.setTask2ins(task2ins);
        start = new double[order.length];
        end = new double[order.length];
    }

    public Chromosome(int[] order, int[] task2ins, List<Integer> accessibleIns) {
        this.setTask(order);
        this.setTask2ins(task2ins);
        start = new double[order.length];
        end = new double[order.length];
        this.unallocatedIns = new ArrayList<>(accessibleIns);
    }

    public double[] getStart() {
        return start;
    }

    public void setStart(double[] start) {
        this.start = start;
    }

    public double[] getEnd() {
        return end;
    }

    public void setEnd(double[] end) {
        this.end = end;
    }

    public void setBetterNum(int betterNum) {
        this.betterNum = betterNum;
    }

    public void setPoorNum(int poorNum) {
        this.poorNum = poorNum;
    }

    public int getBetterNum() {
        return betterNum;
    }

    public int getPoorNum() {
        return poorNum;
    }

    public void addBetter() {
        betterNum++;
    }

    public void addPoor() {
        poorNum++;
    }

    public void reduceBetter() {
        betterNum--;
    }

    public void reducePoor() {
        poorNum--;
    }
    public List<Chromosome> getBetter() {
        return better;
    }
    public List<Chromosome> getPoor() {
        return poor;
    }

    public int[] getTask() {
        return task;
    }

    public void setTask(int[] task) {
        this.task = task;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getMakeSpan() {
        return makeSpan;
    }

    public void setMakeSpan(double makeSpan) {
        this.makeSpan = makeSpan;
    }

    public int[] getTask2ins() {
        return task2ins;
    }

    public void setTask2ins(int[] task2ins) {
        this.task2ins = task2ins;
    }

    public List<Integer> getExistIns() {
        return existIns;
    }

    public List<Integer> getUnallocatedIns() {
        return unallocatedIns;
    }

    @Override
    public Chromosome clone() throws CloneNotSupportedException {
        super.clone();
        Chromosome chromosome = new Chromosome();
        chromosome.task = new int[task.length];
        chromosome.task2ins = new int[task2ins.length];
        chromosome.start=new double[start.length];
        chromosome.end=new double[end.length];
        chromosome.launchTime=new double[ReadOnlyData.insNum];
        chromosome.shutdownTime=new double[ReadOnlyData.insNum];
        chromosome.cost = cost;
        chromosome.makeSpan = makeSpan;
        chromosome.existIns = new ArrayList<>(this.existIns);
        chromosome.unallocatedIns = new ArrayList<>(this.unallocatedIns);
        System.arraycopy(task, 0, chromosome.task, 0, task.length);
        System.arraycopy(task2ins, 0, chromosome.task2ins, 0, task2ins.length);
        System.arraycopy(start, 0, chromosome.start, 0, start.length);
        System.arraycopy(end, 0, chromosome.end, 0, end.length);
        return chromosome;
    }

    public void print() {
        System.out.println("Order:           " + Arrays.toString(this.getTask()));
        System.out.println("Task to Instance:" + Arrays.toString(this.getTask2ins()));
    }

    @Override
    public boolean equals(Object obj) {
        Chromosome chromosome = (Chromosome) obj;
        for (int i = 0; i < chromosome.getTask().length; ++i) {
            if (chromosome.getTask()[i] != getTask()[i] || chromosome.getTask2ins()[i] == getTask2ins()[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (int) (cost + makeSpan);
    }




    public double getCrowding() {
        return crowding;
    }

    public void setCrowding(double crowding) {
        this.crowding = crowding;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public double SDE(double objValue, String objective) {
        // 实现SDE算法
        double shiftedValue = objValue;
        if (objective.equals("makespan")){
            if (objValue < this.makeSpan){
                shiftedValue = this.makeSpan;
                return shiftedValue;
            }
        } else if (objective.equals("cost")){
            if (objValue < this.cost){
                shiftedValue = this.cost;
                return shiftedValue;
            }
        }
        return shiftedValue;
    }
}
