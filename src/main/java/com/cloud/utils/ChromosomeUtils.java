package com.cloud.utils;

import com.cloud.entity.Chromosome;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Task;
import com.cloud.entity.TaskGraph;

import java.util.*;

import static com.cloud.entity.ReadOnlyData.insNum;

public class ChromosomeUtils {

    //获取初始化染色体，需要对应的图，需要可以使用的机器列表，一半概率随机选取ins，一半概率使用同一种机器
    public static Chromosome getInitialChromosome(TaskGraph graph, List<Integer> accessibleIns, Random random) {
        int[] order = graph.TopologicalSorting(random);
        int[] ins = new int[order.length];
        int num = random.nextInt(10);
        if (num < 5) {
            for (int i = 0; i < ins.length; ++i) {
                ins[i] = accessibleIns.get(random.nextInt(accessibleIns.size()));
            }
        } else {
            int insNum = accessibleIns.get(random.nextInt(accessibleIns.size()));
            Arrays.fill(ins, insNum);
        }
        return new Chromosome(order, ins);
    }

    public static List<Chromosome> crossover(Chromosome A, Chromosome B, Random random) {
        Chromosome chromosome1 = null;
        Chromosome chromosome2 = null;
        try {
            chromosome1 = (Chromosome) A.clone();
            chromosome2 = (Chromosome) B.clone();
            crossoverOrder(chromosome1, chromosome2, random);
            crossoverIns(chromosome1, chromosome2, random);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        List<Chromosome> list = new ArrayList<Chromosome>();
        list.add(chromosome1);
        list.add(chromosome2);
        return list;

    }


    public static void crossoverOrder(Chromosome A, Chromosome B, Random random) {

        //n is the number of tasks
        int n = A.getTask().length;
        //random is a random number generator
        //p is the cut position
        int p = random.nextInt(n);
        int cursorA = p + 1;
        int cursorB = p + 1;
        int[] orderA = new int[n];
        int[] orderB = new int[n];
        //
        for (int i = 0; i <= p; i++) {
            orderA[i] = B.getTask()[i];
            orderB[i] = A.getTask()[i];
        }
        for (int num : A.getTask()) {
            if (!isContains(orderA, 0, p, num)) {
                orderA[cursorA] = num;
                cursorA++;
            }
        }

        for (int num : B.getTask()) {
            if (!isContains(orderB, 0, p, num)) {
                if (cursorB >= 100) {
                    System.out.println();
                }
                orderB[cursorB] = num;
                cursorB++;
            }
        }

        A.setTask(orderA);
        B.setTask(orderB);
    }

    public static boolean isContains(int[] arr, int start, int end, int num) {
        for (int i = start; i <= end; ++i) {
            if (arr[i] == num) return true;
        }
        return false;
    }

    public static void crossoverIns(Chromosome A, Chromosome B, Random random) {
        int n = A.getTask().length;
        int p = random.nextInt(n);

        for (int i = 0; i < p; i++) {
            int task = A.getTask()[i];
            int temp = A.getTask2ins()[task];
            A.getTask2ins()[task] = B.getTask2ins()[task];
            B.getTask2ins()[task] = temp;
        }
    }

    public static Chromosome mutate(Chromosome c, double mutateRate, Task[] tasks, Random random, List<Integer> accessibleIns) {
        Chromosome chromosome = null;
        try {
            chromosome = (Chromosome) c.clone();
            double r1 = random.nextDouble(0, 1);
            double r2 = random.nextDouble(0, 1);
//            double r3 = random.nextDouble(0, 1);
            if (r1 < mutateRate) {
                mutateOrder(chromosome, tasks, random);
            }
            if (r2 < mutateRate) {
                mutateIns(chromosome, random, accessibleIns);
            }
//            if (r3 < rate){
//                mutateType(chromosome);
//            }

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assert chromosome != null;
        return chromosome;
    }

    public static void mutateOrder(Chromosome X, Task[] tasks, Random random) {
        int pos = random.nextInt(X.getTask().length);
        Chromosome nc = null;
        try {
            nc = (Chromosome) X.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        int n = nc.getTask().length;
        Task task = tasks[nc.getTask()[pos]].clone();
        int start = pos;
        int end = pos;
        while (start >= 0 && !task.getPredecessor().contains(tasks[nc.getTask()[start]].getIndex())) {
            start--;
        }
        while (end < n && !task.getSuccessor().contains(tasks[nc.getTask()[end]].getIndex())) {
            end++;
        }
        int posN = random.nextInt(end - start - 1) + start + 1;
        int temp = nc.getTask()[pos];
        if (posN < pos) {
            for (int i = pos; i > posN; i--) {
                nc.getTask()[i] = nc.getTask()[i - 1];
            }
        } else if (pos < posN) {
            for (int i = pos; i < posN; i++) {
                nc.getTask()[i] = nc.getTask()[i + 1];
            }
        }
        nc.getTask()[posN] = temp;
    }

    public static void mutateIns(Chromosome X, Random random, List<Integer> accessibleIns) {
        int number = X.getTask2ins().length;
        int p = random.nextInt(number);//generate the position where mutate occurs
        int instance = accessibleIns.get(random.nextInt(accessibleIns.size()));//m is the number of instances available
        X.getTask2ins()[p] = instance;
    }

    public static void refresh(Chromosome chromosome, Task[] tasks) {
        chromosome.setMakeSpan(getMakeSpan(chromosome, tasks));
        chromosome.setCost(getCost(chromosome));
    }

    public static double getCost(Chromosome chromosome) {
        double sum = 0;
        int[] ins_flags = new int[insNum];
        for (int ins_index : chromosome.getTask2ins()) {
            if (ins_flags[ins_index] == 0) {
                int type_index = ReadOnlyData.insToType.get(ins_index);
                int hours = (int) ((chromosome.shutdownTime[ins_index] - chromosome.launchTime[ins_index]) / 3600) + 1;
                sum += hours * ReadOnlyData.types[type_index].p;
                ins_flags[ins_index] = 1;
            }
        }
        return sum;
    }

    public static double getMakeSpan(Chromosome chromosome, Task[] tasks) {
        double[] availableTime = new double[ReadOnlyData.insNum];
        double exitTime = 0;
        for (int taskIndex : chromosome.getTask()) {
            Task task = tasks[taskIndex].clone();
            int insIndex = chromosome.getTask2ins()[taskIndex];
            int typeIndex = ReadOnlyData.insToType.get(insIndex);
            if (task.getPredecessor().size() == 0) {
                chromosome.getStart()[taskIndex] = Math.max(0, availableTime[insIndex]);
                chromosome.getEnd()[taskIndex] = chromosome.getStart()[taskIndex] + task.getReferTime() / ReadOnlyData.types[typeIndex].cu;
                availableTime[insIndex] = chromosome.getEnd()[taskIndex];
            } else {
                chromosome.getStart()[taskIndex] = Math.max(getStartTime(chromosome, taskIndex, tasks), availableTime[insIndex]);
                chromosome.getEnd()[taskIndex] = chromosome.getStart()[taskIndex] + tasks[taskIndex].getReferTime() / ReadOnlyData.types[typeIndex].cu;
                availableTime[insIndex] = chromosome.getEnd()[taskIndex];
            }
            if (chromosome.launchTime[insIndex] == 0) {
                chromosome.launchTime[insIndex] = chromosome.getStart()[taskIndex];
            }
            chromosome.shutdownTime[insIndex] = chromosome.getEnd()[taskIndex];
            if (task.getSuccessor().size() == 0) {
                exitTime = Math.max(exitTime, chromosome.getEnd()[taskIndex]);
            }
        }
        return exitTime;
    }


    public static double getStartTime(Chromosome chromosome, int taskIndex, Task[] tasks) {
        List<Integer> preTaskIndexes = tasks[taskIndex].getPredecessor();
        int n = preTaskIndexes.size();
        int instanceIndex = chromosome.getTask2ins()[taskIndex];
        int typeIndex = ReadOnlyData.insToType.get(instanceIndex);
        double[] communicationTime = new double[n];
        double[] after_communicationTime = new double[n];
        for (int i = 0; i < n; i++) {
            int preTaskIndex = preTaskIndexes.get(i);
            int preInstanceIndex = chromosome.getTask2ins()[preTaskIndex];
            int preTypeIndex = ReadOnlyData.insToType.get(preInstanceIndex);
            double bw_min = Math.min(ReadOnlyData.types[typeIndex].bw, ReadOnlyData.types[preTypeIndex].bw);
            communicationTime[i] = tasks[preTaskIndex].getOutputSize() / bw_min;
            after_communicationTime[i] = chromosome.getEnd()[preTaskIndex] + communicationTime[i];
        }
        double max_after_communication_time = 0;
        for (double time : after_communicationTime) {
            if (time > max_after_communication_time) {
                max_after_communication_time = time;
            }
        }
        return max_after_communication_time;
    }

    public static List<Double> getAllHV(List<List<double[]>> list, double maxMakeSpan, double minMakeSpan, double maxCost, double minCost) {
        List<Double> ans = new LinkedList<>();
        for (List<double[]> chromosomes : list) {
            chromosomes.sort(Comparator.comparingDouble(x -> x[0]));
            double makespan[] = new double[chromosomes.size()];
            double cost[] = new double[chromosomes.size()];
            //按照makespan的顺序计算HV
            for (int i = 0; i < chromosomes.size(); ++i) {
                double makespan_i = chromosomes.get(i)[0];
                double cost_i = chromosomes.get(i)[1];
                makespan[i] = (makespan_i - minMakeSpan) / (maxMakeSpan - minMakeSpan);
                cost[i] = (cost_i - minCost) / (maxCost - minCost);
            }
            double HV = (1.1 - makespan[0]) * (1.1 - cost[0]);
            for (int i = 1; i < makespan.length; ++i) {
                HV += (1.1 - makespan[i]) * (cost[i - 1] - cost[i]);
            }
            ans.add(HV);
        }
        return ans;
    }

    public static double getHV(List<double[]> chromosomes, double maxMakeSpan, double minMakeSpan, double maxCost, double minCost) {
        double ans = 0;
        chromosomes.sort(Comparator.comparingDouble(x -> x[0]));
        double makespan[] = new double[chromosomes.size()];
        double cost[] = new double[chromosomes.size()];
        //按照makespan的顺序计算HV
        for (int i = 0; i < chromosomes.size(); ++i) {
            double makespan_i = chromosomes.get(i)[0];
            double cost_i = chromosomes.get(i)[1];
            makespan[i] = (makespan_i - minMakeSpan) / (maxMakeSpan - minMakeSpan);
            cost[i] = (cost_i - minCost) / (maxCost - minCost);
        }
        double HV = (1.1 - makespan[0]) * (1.1 - cost[0]);
        for (int i = 1; i < makespan.length; ++i) {
            HV += (1.1 - makespan[i]) * (cost[i - 1] - cost[i]);
        }
        return HV;
    }

    public static List<Double> getHV(List<List<Chromosome>> list) {
        double maxMakeSpan = 0;
        double maxCost = 0;
        double minMakeSpan = Integer.MAX_VALUE;
        double minCost = Integer.MAX_VALUE;
        List<Double> ans = new LinkedList<>();
        //找到所有代中，最大和最小的值
        for (List<Chromosome> chromosomes : list) {
            for (Chromosome chromosome : chromosomes) {
                maxMakeSpan = Math.max(maxMakeSpan, chromosome.getMakeSpan());
                maxCost = Math.max(maxCost, chromosome.getCost());
                minMakeSpan = Math.min(minCost, chromosome.getMakeSpan());
                minCost = Math.min(minCost, chromosome.getCost());
            }
        }
        StringBuilder str = new StringBuilder();
        for (List<Chromosome> chromosomes : list) {
            chromosomes.sort(Comparator.comparingDouble(Chromosome::getMakeSpan));
            double makespan[] = new double[chromosomes.size()];
            double cost[] = new double[chromosomes.size()];
            //按照makespan的顺序计算HV
            for (int i = 0; i < chromosomes.size(); ++i) {
                double makespan_i = chromosomes.get(i).getMakeSpan();
                double cost_i = chromosomes.get(i).getCost();
                makespan[i] = (makespan_i - minMakeSpan) / (maxMakeSpan - minMakeSpan);
                cost[i] = (cost_i - minCost) / (maxCost - minCost);
            }
            double HV = (1.1 - makespan[0]) * (1.1 - cost[0]);
            for (int i = 1; i < makespan.length; ++i) {
                HV += (1.1 - makespan[i]) * (cost[i - 1] - cost[i]);
            }
            ans.add(HV);
        }
//        WriterUtils.write("src\\main\\resources\\output\\ParetoFront.txt",str.toString());
        return ans;
    }

    public static boolean checkTopology(Task[] tasks) {
        Set<Integer> set = new HashSet<>();
        for (Task task : tasks) {
            set.add(task.getIndex());
            for (Integer suc : task.getSuccessor()) {
                if (set.contains(suc)) {
                    return false;
                }
            }
        }
        return true;
    }
}
