package com.cloud.entity;

import com.cloud.algorithm.CMSWC;

import java.util.*;

import static com.cloud.entity.ReadOnlyData.types;

public class CMSWCSolution implements Cloneable{
    private Task[] unsortedTasks;
    private double cost;
    private double makeSpan;
    private List<List<CMSWCVM>> insPool;
    private double crowdingDist;
    private List<Integer> assignedTask;
    private Set<Integer> assignedType;
    private int[] assignedQuantity;
    public CMSWCSolution(){}

    public CMSWCSolution(Task[] tasks) {
        unsortedTasks = new Task[tasks.length];
        assignedTask = new ArrayList<>();
        assignedType = new HashSet<>();
        assignedQuantity = new int[types.length];
        for (int i = 0; i < tasks.length; i++) {
            unsortedTasks[i] = tasks[i].clone();
        }
        insPool = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            insPool.add(new ArrayList<>());
        }

        cost = 0;
        makeSpan = 0;
    }
    public Task[] getTasks() {
        return unsortedTasks;
    }
    public void setTasks(Task[] tasks) {
        this.unsortedTasks = tasks;
    }
    public List<List<CMSWCVM>> getInsPool() {
        return insPool;
    }
    public void setInsPool(List<List<CMSWCVM>> insPool) {
        this.insPool = insPool;
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

    public Task[] getUnsortedTasks() {
        return unsortedTasks;
    }

    public void setUnsortedTasks(Task[] unsortedTasks) {
        this.unsortedTasks = unsortedTasks;
    }

    public double getCrowdingDist() {
        return crowdingDist;
    }

    public void setCrowdingDist(double crowdingDist) {
        this.crowdingDist = crowdingDist;
    }

    public List<Integer> getAssignedTask() {
        return assignedTask;
    }

    public void setAssignedTask(List<Integer> assignedTask) {
        this.assignedTask = assignedTask;
    }

    public Set<Integer> getAssignedType() {
        return assignedType;
    }

    public void setAssignedType(Set<Integer> assignedType) {
        this.assignedType = assignedType;
    }

    public int[] getAssignedQuantity() {
        return assignedQuantity;
    }

    public void setAssignedQuantity(int[] assignedQuantity) {
        this.assignedQuantity = assignedQuantity;
    }

    public void update() {
        makeSpan = calculateMakeSpan();
        cost =  calculateCost();
    }

    public double calculateMakeSpan(){
        for (int i = 0; i < insPool.size(); i++) {
            for (CMSWCVM vm: insPool.get(i)){
                for (int j = 0; j < vm.getTaskList().size(); j++) {
                    getTaskFinishTime(vm, j);
                }
                // 将当前vm的关机时间修改为该任务的结束时间+传输时间，而当前vm的开机时间为当前vm的taskList中第一个任务的开始时间
                vm.setLaunchTime(unsortedTasks[vm.getTaskList().get(0)].getStartTime());
                vm.setShutdownTime(unsortedTasks[vm.getTaskList().get(vm.getTaskList().size() - 1)].getFinalTime());
            }
        }
        return findLastShutdownTime();
    }
    //TODO:递归

    /**
     *
     * @param vm: 当前计算的task在哪个vm之中执行
     * @param index: 当前的task在vm任务列表中的下标
     * @return 当前任务的执行时间
     */
    public double getTaskFinishTime(CMSWCVM vm, int index){
        if(index==-1) return 0;
        int taskIndex = vm.getTaskList().get(index);
        //机器前面的节点，不是前置节点
        if(unsortedTasks[taskIndex].getFinalTime()!=0) return unsortedTasks[taskIndex].getFinalTime();
        if(unsortedTasks[taskIndex].getPredecessor().isEmpty() && index==0){
            unsortedTasks[taskIndex].setFinalTime(unsortedTasks[taskIndex].getReferTime() / types[unsortedTasks[taskIndex].getInsType()].cu);
            return unsortedTasks[taskIndex].getReferTime() / types[unsortedTasks[taskIndex].getInsType()].cu;
        }
        double max = 0;
        for(int pre: unsortedTasks[taskIndex].getPredecessor()){
            MachineAndTaskIndex machineAndTaskIndex = getMachineAndTaskIndex(pre);
            double trans = unsortedTasks[pre].getOutputSize()/Math.min(types[unsortedTasks[pre].getInsType()].bw,types[unsortedTasks[taskIndex].getInsType()].bw);
            max = Math.max(max, getTaskFinishTime(machineAndTaskIndex.vm, machineAndTaskIndex.taskIndex) + trans);
        }
        unsortedTasks[taskIndex].setStartTime(Math.max(max, getTaskFinishTime(vm, index-1)));
        unsortedTasks[taskIndex].setFinalTime(unsortedTasks[taskIndex].getStartTime() + unsortedTasks[taskIndex].getReferTime() / types[unsortedTasks[taskIndex].getInsType()].cu);
        return unsortedTasks[taskIndex].getFinalTime();
    }


    public MachineAndTaskIndex getMachineAndTaskIndex(int task){
        for(CMSWCVM vm: insPool.get(unsortedTasks[task].getInsType())){
            for(int i=0;i<vm.getTaskList().size();++i){
                if(vm.getTaskList().get(i)==task){
                    return new MachineAndTaskIndex(vm, i);
                }
            }
        }
        return null;
    }


    private double findLastShutdownTime() {
        double max = 0;
        for (int i = 0; i < insPool.size(); i++) {
            for (CMSWCVM vm: insPool.get(i)){
                max = Math.max(max, vm.getShutdownTime());
            }
        }
        return max;
    }

    public double calculateCost(){
        double sum = 0;
        for (int i = 0; i < insPool.size(); i++) {
            for (CMSWCVM vm: insPool.get(i)){
                int hours = (int) ((vm.getShutdownTime() - vm.getLaunchTime()) / 3600) + 1;
                sum += hours * ReadOnlyData.types[vm.getType()].p;
            }
        }

        return sum;
    }

    @Override
    public CMSWCSolution clone() throws CloneNotSupportedException {
        super.clone();
        CMSWCSolution solution = new CMSWCSolution(this.unsortedTasks);
        solution.setCost(this.cost);
        solution.setMakeSpan(this.makeSpan);
        solution.assignedTask.addAll(this.assignedTask);
        solution.assignedType.addAll(this.assignedType);
        solution.assignedQuantity = Arrays.copyOf(this.assignedQuantity,this.assignedQuantity.length);
        for (int i = 0; i < insPool.size(); i++) {
            for (CMSWCVM vm: insPool.get(i)){
                solution.getInsPool().get(i).add(vm.clone());
            }
        }
        return solution;
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

    public void repair(CMSWC cmswc) {
        List<CMSWCVM> crashedVM = new ArrayList<>();
        List<CMSWCVM> newVMs = new ArrayList<>();
        for (List<CMSWCVM> vms: insPool){
            for (CMSWCVM vm : vms) {
                if (cmswc.disabledIns.contains(vm.getIndex())) {
                    crashedVM.add(vm);
                    int type = cmswc.accessibleIns.get(ReadOnlyData.random.nextInt(cmswc.accessibleIns.size())) / 10;
                    int insIndex = type * 10 + assignedQuantity[type];
                    while(cmswc.disabledIns.contains(insIndex)){
                        insIndex++;
                    }
                    assignedQuantity[type] += 1;
                    CMSWCVM newIns = new CMSWCVM(type,insIndex);
                    newVMs.add(newIns);
                    assignedType.add(type);
                    for (int t : vm.getTaskList()) {
                        newIns.getTaskList().add(t);
                        unsortedTasks[t].setInsType(type);
                    }
                }
            }
        }
        for (CMSWCVM newVM : newVMs) {
            insPool.get(newVM.getType()).add(newVM);
        }
        for (CMSWCVM vm : crashedVM) {
            insPool.get(vm.getType()).remove(vm);
            if (insPool.get(vm.getType()).size() == 0){
                assignedType.remove(vm.getType());
            }
        }
        update();
    }

    static class MachineAndTaskIndex{
        CMSWCVM vm;
        int taskIndex = -1;
        public MachineAndTaskIndex(CMSWCVM vm, int taskIndex) {
            this.vm = vm;
            this.taskIndex = taskIndex;
        }
        public MachineAndTaskIndex(){}
    }


}
