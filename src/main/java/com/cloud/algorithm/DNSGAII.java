package com.cloud.algorithm;

import com.cloud.algorithm.change.InsAvailChange;
import com.cloud.algorithm.repair.CrashRandomRepair;
import com.cloud.algorithm.repair.CrashSimilarityRepair;
import com.cloud.algorithm.standard.Algorithm;
import com.cloud.algorithm.standard.Change;
import com.cloud.algorithm.standard.Repair;
import com.cloud.entity.*;
import com.cloud.utils.ChromosomeUtils;
import com.cloud.utils.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.*;

public class DNSGAII extends Algorithm {

    public int size;
    public int generation;
    public double mutationRate;
    public List<Chromosome> fa = new LinkedList<>();
    public List<Chromosome> son = new LinkedList<>();
    public List<Integer> accessibleIns = new LinkedList<>();
    public List<Integer> disabledIns = new LinkedList<>();
    public TaskGraph graph;
    public int insNum;
    public Task[] tasks;
    public List<List<Chromosome>> rank;
    public List<List<Chromosome>> all = new LinkedList<>();
    public Random random;
    public Change change;
    public Repair repair;

    public DNSGAII(String name) {
        super(name);
    }

    @Override
    public Result execute() {
        init();
        if(change==null) change = new InsAvailChange();
        if(repair==null) repair = new CrashSimilarityRepair();
        initPopulation();
        for(int i=0;i<generation;++i){
            if(change instanceof InsAvailChange){
                if(InsAvailChange.generations.contains(i)) {
                    change.change(this);
                    repair.repair(this);
                }
            }
            iterate();
        }

//        List<Double> hv = ChromosomeUtils.getHV(all);
        Result result = new Result();
        result.map.put("front", all.get(all.size()-1));
        return result;
    }

    public void iterate(){
        doProduce();
        doSort();
        doEliminate();
        son.clear();

        for (Chromosome chromosome : fa) {
            ChromosomeUtils.refresh(chromosome, tasks);
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getBetter().clear();
            chromosome.getPoor().clear();
        }
        List<Chromosome> list = new ArrayList<>();
        for(int k=0;k<rank.get(0).size();++k){
            try {
                list.add(rank.get(0).get(k).clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        all.add(list);
    }

    public void initPopulation(){
        while (fa.size() < size) {
            Chromosome chromosome = ChromosomeUtils.getInitialChromosome(graph, accessibleIns, random);
            if (!fa.contains(chromosome)) fa.add(chromosome);
        }
    }
    //初始化种群大小，代数，变异率，任务图，初始化可用机器
    public void init() {
        int size = IOUtils.readIntProperties("dnsgaii-random", "population.size");
        int generation = IOUtils.readIntProperties("dnsgaii-random", "population.generation");
        double mutationRate = IOUtils.readDoubleProperties("dnsgaii-random", "population.mutation");
        this.size = size;
        this.generation = generation;
        this.mutationRate = mutationRate;
        input();
    }

    public void input() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("dnsgaii-random");
            int size= IOUtils.readIntProperties("dnsgaii-random","file.taskGraph.size");
            graph = new TaskGraph(size);
            insNum = size;
            tasks = new Task[size];
            for (int i = 0; i < size; ++i) {
                tasks[i] = new Task(i);
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(DNSGAII.class.getClassLoader().getResource(bundle.getString("file.taskGraph.path")));
            Element root = document.getRootElement();
            for (Element child : root.elements()) {
                if (child.getName().equals("child")) {
                    int ver2 = Integer.parseInt(child.attributeValue("ref").substring(2));
                    for (Element parent : child.elements()) {
                        int ver1 = Integer.parseInt(parent.attributeValue("ref").substring(2));
                        graph.addEdge(ver1, ver2);
                        tasks[ver1].getSuccessor().add(ver2);
                        tasks[ver2].getPredecessor().add(ver1);
                    }
                } else if (child.getName().equals("job")) {
                    int id = Integer.parseInt(child.attributeValue("id").substring(2));
                    double referTime = Double.parseDouble(child.attributeValue("runtime"));
                    long inputSize = 0;
                    long outputSize = 0;
                    for (Element element : child.elements()) {
                        if (element.attributeValue("link").equals("input"))
                            inputSize += Long.parseLong(element.attributeValue("size"));
                        else if (element.attributeValue("link").equals("output"))
                            outputSize += Long.parseLong(element.attributeValue("size"));
                    }
                    tasks[id].setInputSize(inputSize);
                    tasks[id].setOutputSize(outputSize);
                    tasks[id].setReferTime(referTime);
                }
            }

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

//    private void calculateDepth(Task[] tasks) {
//        int[] in = new int[tasks.length];
//        Queue<Task> queue = new ArrayDeque<>();
//        for(int i=0;i<tasks.length;++i){
//            in[i] = tasks[i].getPredecessor().size();
//            if(in[i]==0) {
//                queue.add(tasks[i]);
//                tasks[i].setDepth(0);
//            }
//        }
//
//        while (!queue.isEmpty()){
//            Task task = queue.poll();
//            for(int i:task.getSuccessor()){
//                Task t = tasks[i];
//                t.setDepth(Math.max(t.getDepth(), task.getDepth()+1));
//                queue.add(t);
//            }
//        }
//
//
//    }


    public void doProduce() {
        try {

            while (son.size() < fa.size()) {
                int num1 = random.nextInt(size);
                int num2 = random.nextInt(size);
                while (num1 == num2) {
                    num2 = random.nextInt(size);
                }

                Chromosome parent1 = fa.get(num1).clone();
                Chromosome parent2 = fa.get(num2).clone();
                Chromosome child1;
                Chromosome child2;

                List<Chromosome> childList = ChromosomeUtils.crossover(parent1, parent2, random);
                child1 = childList.get(0);
                child2 = childList.get(1);
                if (random.nextInt(10000) < mutationRate * 10000) {
                    child1 = ChromosomeUtils.mutate(child1, mutationRate, tasks, random, accessibleIns);
                    child2 = ChromosomeUtils.mutate(child2, mutationRate, tasks, random, accessibleIns);
                }
                ChromosomeUtils.refresh(child1, tasks);
                ChromosomeUtils.refresh(child2, tasks);

                son.add(child1);
                son.add(child2);
            }

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    public void doSort() {
        List<Chromosome> list = new LinkedList<>();
        list.addAll(fa);
        list.addAll(son);

        for (Chromosome chromosome : list) {
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getPoor().clear();
            chromosome.getBetter().clear();
//            DataUtils.refresh(chromosome);
        }
        for (int i = 0; i < list.size(); ++i) {
            Chromosome chromosome = list.get(i);
            for (int j = i + 1; j < list.size(); ++j) {
                Chromosome other = list.get(j);
                if (chromosome.getMakeSpan() >= other.getMakeSpan()
                        && chromosome.getCost() >= other.getCost()) {
                    if (chromosome.getMakeSpan() - other.getMakeSpan() > 0.0000000001
                            || chromosome.getCost() - other.getCost() > 0.0000000001) {
                        setBetterAndPoor(other, chromosome);
                    }
                }
                if (chromosome.getMakeSpan() <= other.getMakeSpan()
                        && chromosome.getCost() <= other.getCost()
                ) {
                    if ((chromosome.getMakeSpan() - other.getMakeSpan()) < -0.000000001
                            || chromosome.getCost() - other.getCost() < -0.000000001
                    ) {
                        setBetterAndPoor(chromosome, other);
                    }
                }
            }
        }
        rank = new LinkedList<>();
        while (hasBetter(list)) {
            LinkedList<Chromosome> rankList = new LinkedList<>();
            List<Chromosome> temp = new LinkedList<>();
            for (Chromosome chromosome : list) {
                if (chromosome.getBetterNum() == 0) {
                    chromosome.reduceBetter();
                    rankList.add(chromosome);
                    temp.add(chromosome);
                }
            }
            for (Chromosome chromosome : temp) {
                for (Chromosome worse : chromosome.getPoor()) {
                    worse.reduceBetter();
                }
            }
            rank.add(rankList);
        }
    }
    public void setBetterAndPoor(Chromosome better, Chromosome poor) {
        better.getPoor().add(poor);
        poor.getBetter().add(better);
        better.addPoor();
        poor.addBetter();
    }

    public boolean hasBetter(List<Chromosome> list) {
        for (Chromosome chromosome : list) {
            if (chromosome.getBetterNum() >= 0) return true;
        }
        return false;
    }

    public void doEliminate() {
        fa.clear();
        for (List<Chromosome> list : rank) {
            list.sort((o1, o2) -> {
                if (o1.getMakeSpan() - o2.getMakeSpan() > 0.000000001) return 1;
                else if (o1.getMakeSpan() - o2.getMakeSpan() < -0.000000001) return -1;
                return 0;
            });
            list.get(0).setCrowding(Double.MAX_VALUE);
            list.get(list.size() - 1).setCrowding(Double.MAX_VALUE);
            for (int i = 1; i < list.size() - 1; ++i) {
                list.get(i).setCrowding(Math.abs(list.get(i + 1).getMakeSpan() - list.get(i - 1).getMakeSpan()) * Math.abs(list.get(i + 1).getCost() - list.get(i - 1).getCost()));
            }
            list.sort((o1, o2) -> {
                double num = o1.getCrowding() - o2.getCrowding();
                if (num > 0) return -1;
                if (num < 0) return 1;
                return 0;
            });
        }
        for (List<Chromosome> list : rank) {
            for (Chromosome chromosome : list) {
                if (!fa.contains(chromosome)) fa.add(chromosome);
                if (fa.size() >= size) return;
            }
        }
    }
}
