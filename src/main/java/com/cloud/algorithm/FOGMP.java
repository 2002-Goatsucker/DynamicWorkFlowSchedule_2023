package com.cloud.algorithm;

import com.cloud.Application;
import com.cloud.algorithm.change.InsAvailChange;
import com.cloud.algorithm.repair.CrashSimilarityRepair;
import com.cloud.algorithm.standard.Algorithm;
import com.cloud.algorithm.standard.Change;
import com.cloud.algorithm.standard.Repair;
import com.cloud.entity.Chromosome;
import com.cloud.entity.Result;
import com.cloud.entity.Task;
import com.cloud.entity.TaskGraph;
import com.cloud.utils.ChromosomeUtils;
import com.cloud.utils.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.*;

public class FOGMP extends Algorithm {


    public int size;
    public int generation;
    public double mutationRate;
    public List<Chromosome> ca = new ArrayList<>();
    public List<Chromosome> da = new ArrayList<>();
    public List<Integer> accessibleIns = new LinkedList<>();
    public List<Integer> disabledIns = new LinkedList<>();
    public TaskGraph graph;
    public int insNum;
    public Task[] tasks;
    public List<List<Chromosome>> daRank = new ArrayList<>();
    public List<List<Chromosome>> caRank = new ArrayList<>();

    public List<List<Chromosome>> all = new LinkedList<>();
    public Random random;
    public Change change;
    public Repair repair;
    public DNSGAII dnsgaii = new DNSGAII("");


    public FOGMP(String name, Random random) {
        super(name);
        Application.initIns(dnsgaii.accessibleIns);
        this.random = random;
        dnsgaii.random = random;
        dnsgaii.init();
        if(dnsgaii.change==null) dnsgaii.change = new InsAvailChange();
        if(dnsgaii.repair==null) dnsgaii.repair = new CrashSimilarityRepair();
        dnsgaii.initPopulation();
    }

    @Override
    public Result execute() {
        init();
        if (change == null) change = new InsAvailChange();
        if (repair == null) repair = new CrashSimilarityRepair();
        boolean isChange = false;
        initPopulation();
        for(int i=0;i<generation;++i){
            if(change instanceof InsAvailChange){
                if(InsAvailChange.generations.contains(i)) {
                    change.change(this);
                    repair.repair(this);
                }
            }
            List<Chromosome> front1 = daIterate();
            List<Chromosome> front2 = caIterate();
            List<Chromosome> front = new ArrayList<>(front1);
            front.addAll(front2);
            all.add(getParetoFront(front));
        }
//        for (int x = 0; x < generation; ++x) {
//            if (change instanceof InsAvailChange) {
//                if (InsAvailChange.generations.contains(x)) {
//                    change.change(this);
//                    if(ca.isEmpty()) {
//                        for (List<Chromosome> list : dnsgaii.rank) {
//                            list.sort((o1, o2) -> {
//                                if (o1.getMakeSpan() - o2.getMakeSpan() > 0.000000001) return 1;
//                                else if (o1.getMakeSpan() - o2.getMakeSpan() < -0.000000001) return -1;
//                                return 0;
//                            });
//                            list.get(0).setCrowding(Double.MAX_VALUE);
//                            list.get(list.size() - 1).setCrowding(Double.MAX_VALUE);
//                            for (int i = 1; i < list.size() - 1; ++i) {
//                                list.get(i).setCrowding(Math.abs(list.get(i + 1).getMakeSpan() - list.get(i - 1).getMakeSpan()) * Math.abs(list.get(i + 1).getCost() - list.get(i - 1).getCost()));
//                            }
//                            list.sort((o1, o2) -> {
//                                double num = o1.getCrowding() - o2.getCrowding();
//                                if (num > 0) return -1;
//                                if (num < 0) return 1;
//                                return 0;
//                            });
//                        }
//                        c:
//                        for (List<Chromosome> list : dnsgaii.rank) {
//                            for (Chromosome chromosome : list) {
//                                if (!ca.contains(chromosome)) ca.add(chromosome);
//                                if (ca.size() >= size / 2) break c;
//                            }
//                        }
//                    }
//                    if(da.isEmpty()){
//                        while (da.size() < size / 2) {
//                            Chromosome chromosome = ChromosomeUtils.getInitialChromosome(graph, accessibleIns, random);
//                            if (!da.contains(chromosome)) da.add(chromosome);
//                        }
//                    }
//                    repair.repair(this);
//                    isChange = true;
//                }
//            }
//            if(isChange) {
//                List<Chromosome> front1 = daIterate();
//                List<Chromosome> front2 = caIterate();
//                List<Chromosome> front = new ArrayList<>(front1);
//                front.addAll(front2);
//                all.add(getParetoFront(front));
//            }else {
//                dnsgaii.iterate();
//                List<Chromosome> list = new ArrayList<>();
//                for(int k=0;k<dnsgaii.rank.get(0).size();++k){
//                    try {
//                        list.add(dnsgaii.rank.get(0).get(k).clone());
//                    } catch (CloneNotSupportedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                all.add(list);
//            }
//        }

//        List<Double> hv = ChromosomeUtils.getHV(all);
        Result result = new Result();
        List<List<double[]>> fronts = new ArrayList<>();
        for (List<Chromosome> list : all) {
            List<double[]> front = new ArrayList<>();
            for (Chromosome chromosome : list) {
                front.add(new double[]{chromosome.getMakeSpan(), chromosome.getCost()});
            }
            fronts.add(front);
        }
        result.map.put("fronts", fronts);
        System.out.println("------FOGMP FINISH------");
        return result;
    }


    public void initPopulation() {
        while (da.size() < size / 2) {
            int[] order = graph.TopologicalSorting(random);
            int[] ins = new int[order.length];
            for (int i = 0; i < ins.length; ++i) {
                ins[i] = accessibleIns.get(random.nextInt(accessibleIns.size()));
            }
            Chromosome chromosome = new Chromosome(order, ins);
            if (!da.contains(chromosome)) da.add(chromosome);
        }
        while (ca.size() < size / 2) {
            Chromosome chromosome = ChromosomeUtils.getInitialChromosome(graph, accessibleIns, random);
            if (!ca.contains(chromosome)) ca.add(chromosome);
        }
    }

    public List<Chromosome> caIterate() {
        List<Chromosome> son = caReproduce();
        sorted(ca, son, caRank);
        eliminate(ca, caRank);
        for (Chromosome chromosome : ca) {
            ChromosomeUtils.refresh(chromosome, tasks);
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getBetter().clear();
            chromosome.getPoor().clear();
        }
        List<Chromosome> list = new ArrayList<>();
        for (int k = 0; k < caRank.get(0).size(); ++k) {
            try {
                list.add(caRank.get(0).get(k).clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    public List<Chromosome> caReproduce() {
        List<Chromosome> son = new ArrayList<>();
        try {

            while (son.size() < size / 2) {
                int num1 = random.nextInt(ca.size());
                int num2 = random.nextInt(ca.size());
                while (num1 == num2) {
                    num2 = random.nextInt(ca.size());
                }

                Chromosome parent1 = ca.get(num1).clone();
                Chromosome parent2 = ca.get(num2).clone();
                if (random.nextInt(100) < 0.1 * 100) {
                    parent1 = da.get(random.nextInt(da.size())).clone();
                }
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
        return son;
    }


    public List<Chromosome> daIterate() {
        List<List<Chromosome>> region = divideRegion();

        List<Chromosome> son = daReproduce(region);
        sorted(da, son, daRank);
        eliminate(da, daRank);
        for (Chromosome chromosome : da) {
            ChromosomeUtils.refresh(chromosome, tasks);
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getBetter().clear();
            chromosome.getPoor().clear();
        }
        List<Chromosome> list = new ArrayList<>();
        for (int k = 0; k < daRank.get(0).size(); ++k) {
            try {
                list.add(daRank.get(0).get(k).clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    public List<List<Chromosome>> divideRegion() {
        List<List<Chromosome>> region = new ArrayList<>();
        List<Chromosome> region0 = new ArrayList<>();
        List<Chromosome> region1 = new ArrayList<>();
        List<Chromosome> region2 = new ArrayList<>();
        List<Chromosome> region3 = new ArrayList<>();
        List<Chromosome> region4 = new ArrayList<>();

        double maxMakeSpan = 0;
        double minMakeSpan = Double.MAX_VALUE;
        double maxCost = 0;
        double minCost = Double.MAX_VALUE;

        for (Chromosome chromosome : da) {
            ChromosomeUtils.refresh(chromosome, tasks);
            maxMakeSpan = Math.max(chromosome.getMakeSpan(), maxMakeSpan);
            maxCost = Math.max(chromosome.getCost(), maxCost);
            minMakeSpan = Math.min(chromosome.getMakeSpan(), minMakeSpan);
            minCost = Math.min(chromosome.getCost(), minCost);
        }

        for (Chromosome chromosome : da) {
            //计算点到五个分区向量的距离，就近原则划分分区
            double makespan = (chromosome.getMakeSpan() - minMakeSpan) / (maxMakeSpan - minMakeSpan);
            double cost = (chromosome.getCost() - minCost) / (maxCost - minCost);
            double d0 = makespan;
            double d1 = Math.abs((Math.sqrt(2) + 1) * makespan - cost) / Math.sqrt(4 + 2 * Math.sqrt(2));
            double d2 = Math.abs(makespan - cost) / Math.sqrt(2);
            double d3 = Math.abs(makespan - (Math.sqrt(2) + 1) * cost) / Math.sqrt(4 + 2 * Math.sqrt(2));
            double d4 = cost;
            if (d0 <= d1 && d0 <= d2 && d0 <= d3 && d0 <= d4) {
                chromosome.setRegion(0);
                region0.add(chromosome);
            } else if (d1 <= d0 && d1 <= d2 && d1 <= d3 && d1 <= d4) {
                chromosome.setRegion(1);
                region1.add(chromosome);
            } else if (d2 <= d1 && d2 <= d0 && d2 <= d3 && d2 <= d4) {
                chromosome.setRegion(2);
                region2.add(chromosome);
            } else if (d3 <= d1 && d3 <= d2 && d3 <= d0 && d3 <= d4) {
                chromosome.setRegion(3);
                region3.add(chromosome);
            } else if (d4 <= d1 && d4 <= d2 && d4 <= d3 && d4 <= d0) {
                chromosome.setRegion(4);
                region4.add(chromosome);
            }
        }
        region.add(region0);
        region.add(region1);
        region.add(region2);
        region.add(region3);
        region.add(region4);
        return region;
    }

    public List<Chromosome> daReproduce(List<List<Chromosome>> region) {
        List<Chromosome> son = new ArrayList<>();
        List<List<Chromosome>> regionFront = new ArrayList<>();
        regionFront.add(getParetoFront(region.get(0)));
        regionFront.add(getParetoFront(region.get(1)));
        regionFront.add(getParetoFront(region.get(2)));
        regionFront.add(getParetoFront(region.get(3)));
        regionFront.add(getParetoFront(region.get(4)));
        List<List<Integer>> fo0 = getFeatureOrder(regionFront.get(0));
        List<List<Integer>> fo1 = getFeatureOrder(regionFront.get(1));
        List<List<Integer>> fo2 = getFeatureOrder(regionFront.get(2));
        List<List<Integer>> fo3 = getFeatureOrder(regionFront.get(3));
        List<List<Integer>> fo4 = getFeatureOrder(regionFront.get(4));

        while (son.size() < size / 2) {
//            outerReproduce(ca,ca,son);
            if (random.nextInt(100) < 100 * 0.9) {
                Chromosome chromosome1 = da.get(random.nextInt(da.size()));
                Chromosome chromosome2 = da.get(random.nextInt(da.size()));
                if(chromosome1.getRegion()==chromosome2.getRegion()){
                    List<List<Integer>> fo;
                    switch (chromosome1.getRegion()){
                        case 0:
                            fo = fo0;
                            break;
                        case 1:
                            fo = fo1;
                            break;
                        case 2:
                            fo = fo2;
                            break;
                        case 3:
                            fo = fo3;
                            break;
                        case 4:
                            fo = fo4;
                            break;
                        default:
                            throw new RuntimeException("region 数值异常");
                    }
                    List<Integer> f = new ArrayList<>();
                    if (fo.size() > 0) f = fo.get(random.nextInt(fo.size()));
                    innerReproduce(chromosome1,chromosome2,son,f);
                }else {
                    outerReproduce(chromosome1,chromosome2,son);
                }
//                if (son.size() < size / 2) {
//                    List<Integer> fo = new ArrayList<>();
//                    if (fo0.size() > 0) fo = fo0.get(random.nextInt(fo0.size()));
//                    innerReproduce(region.get(0), son, fo);
//                }
//                if (son.size() < size / 2) {
//                    List<Integer> fo = new ArrayList<>();
//                    if (fo1.size() > 0) fo = fo1.get(random.nextInt(fo1.size()));
//                    innerReproduce(region.get(1), son, fo);
//                }
//                if (son.size() < size / 2) {
//                    List<Integer> fo = new ArrayList<>();
//                    if (fo2.size() > 0) fo = fo2.get(random.nextInt(fo2.size()));
//                    innerReproduce(region.get(2), son, fo);
//                }
//                if (son.size() < size / 2) {
//                    List<Integer> fo = new ArrayList<>();
//                    if (fo3.size() > 0) fo = fo3.get(random.nextInt(fo3.size()));
//                    innerReproduce(region.get(3), son, fo);
//                }
//                if (son.size() < size / 2) {
//                    List<Integer> fo = new ArrayList<>();
//                    if (fo4.size() > 0) fo = fo4.get(random.nextInt(fo4.size()));
//                    innerReproduce(region.get(4), son, fo);
//                }
//                if (son.size() < size / 2) {
//                    int r = random.nextInt(region.size());
//                    while (r == 0) {
//                        r = random.nextInt(region.size());
//                    }
//                    outerReproduce(region.get(0), region.get(r), son);
//                }
//                if (son.size() < size / 2) {
//                    int r = random.nextInt(region.size());
//                    while (r == 1) {
//                        r = random.nextInt(region.size());
//                    }
//                    outerReproduce(region.get(1), region.get(r), son);
//                }
//                if (son.size() < size / 2) {
//                    int r = random.nextInt(region.size());
//                    while (r == 2) {
//                        r = random.nextInt(region.size());
//                    }
//                    outerReproduce(region.get(2), region.get(r), son);
//                }
//                if (son.size() < size / 2) {
//                    int r = random.nextInt(region.size());
//                    while (r == 3) {
//                        r = random.nextInt(region.size());
//                    }
//                    outerReproduce(region.get(3), region.get(r), son);
//                }
//                if (son.size() < size / 2) {
//                    int r = random.nextInt(region.size());
//                    while (r == 4) {
//                        r = random.nextInt(region.size());
//                    }
//                    outerReproduce(region.get(4), region.get(4), son);
//                }
            } else {
                try {
                    Chromosome chromosome1 = ca.get(random.nextInt(ca.size())).clone();
                    Chromosome chromosome2 = da.get(random.nextInt(da.size())).clone();
                    Chromosome child1;
                    Chromosome child2;
                    List<Chromosome> childList = ChromosomeUtils.crossover(chromosome1, chromosome2, random);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return son;
    }


    public void sorted(List<Chromosome> fa, List<Chromosome> son, List<List<Chromosome>> rank) {
        List<Chromosome> list = new LinkedList<>();
        list.addAll(fa);
        list.addAll(son);

        for (Chromosome chromosome : list) {
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getPoor().clear();
            chromosome.getBetter().clear();
            ChromosomeUtils.refresh(chromosome, tasks);
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
        rank.clear();
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

    public void eliminate(List<Chromosome> fa, List<List<Chromosome>> rank) {
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

    //辅助方法
    public boolean hasBetter(List<Chromosome> list) {
        for (Chromosome chromosome : list) {
            if (chromosome.getBetterNum() >= 0) return true;
        }
        return false;
    }

    public void innerReproduce(Chromosome chromosome1, Chromosome chromosome2, List<Chromosome> son, List<Integer> fo){
        List<Chromosome> children = ChromosomeUtils.crossover(chromosome1, chromosome2, random);
        Chromosome child1 = children.get(0);
        Chromosome child2 = children.get(1);
        if (random.nextInt(10000) < mutationRate * 10000) {
            child1 = ChromosomeUtils.mutate(child1, mutationRate, tasks, random, accessibleIns);
            child2 = ChromosomeUtils.mutate(child2, mutationRate, tasks, random, accessibleIns);
        }
        if (!fo.isEmpty()) {
            TaskGraph foGraph = graph.clone();
            for (int i = 0; i < fo.size() - 1; ++i) {
                foGraph.addEdge(fo.get(i), fo.get(i + 1));
            }

            int[] newOrder1 = foGraph.TopologicalSorting(random);
            int[] newOrder2 = foGraph.TopologicalSorting(random);
            child1.setTask(newOrder1);
            child2.setTask(newOrder2);
        }

        ChromosomeUtils.refresh(child1, tasks);
        ChromosomeUtils.refresh(child2, tasks);
        //TODO: 测试一下需不需要将FO对应的机器也更改为同一个机器
        int ins1 = accessibleIns.get(random.nextInt(accessibleIns.size()));
        int ins2 = accessibleIns.get(random.nextInt(accessibleIns.size()));
        for(int i=0;i<child1.getTask().length;++i){
            if(fo.contains(child1.getTask()[i])) child1.getTask2ins()[child1.getTask()[i]] = ins1;
            if(fo.contains(child2.getTask()[i])) child2.getTask2ins()[child2.getTask()[i]] = ins2;
        }

        son.add(child1);
        son.add(child2);
    }


    //每个区域内部进行一次reproduce
    public void innerReproduce(List<Chromosome> fa, List<Chromosome> son, List<Integer> fo) {
        if (fa.size() <= 1) return;
        Chromosome chromosome1 = fa.get(random.nextInt(fa.size()));
        Chromosome chromosome2 = fa.get(random.nextInt(fa.size()));
        innerReproduce(chromosome1, chromosome2,son,fo);
    }

    public void outerReproduce(Chromosome chromosome1, Chromosome chromosome2, List<Chromosome> son){
        List<Chromosome> children = ChromosomeUtils.crossover(chromosome1, chromosome2, random);
        Chromosome child1 = children.get(0);
        Chromosome child2 = children.get(1);
        if (random.nextInt(10000) < mutationRate * 10000) {
            child1 = ChromosomeUtils.mutate(child1, mutationRate, tasks, random, accessibleIns);
            child2 = ChromosomeUtils.mutate(child2, mutationRate, tasks, random, accessibleIns);
        }
        son.add(child1);
        son.add(child2);
    }

    //两个区域之间进行一次reproduce
    public void outerReproduce(List<Chromosome> region1, List<Chromosome> region2, List<Chromosome> son) {
        if (region1.size() == 0 || region2.size() == 0) return;
        Chromosome chromosome1 = region1.get(random.nextInt(region1.size()));
        Chromosome chromosome2 = region2.get(random.nextInt(region2.size()));
        outerReproduce(chromosome1,chromosome2, son);
    }


    //获取fo
    public List<List<Integer>> getFeatureOrder(List<Chromosome> front) {
        List<List<Integer>> featureOrder = new ArrayList<>();
        for (Chromosome chromosome : front) {
            Map<Integer, List<Integer>> map = new HashMap<>();
            int[] order = chromosome.getTask();
            int[] ins = chromosome.getTask2ins();
            for (int i = 0; i < ins.length; ++i) {
                if (map.containsKey(ins[i])) {
                    map.get(ins[i]).add(i);
                } else {
                    map.put(ins[i], new ArrayList<>());
                    map.get(ins[i]).add(i);
                }
            }
            for (List<Integer> fo : map.values()) {
                if (fo.size() > 1 && fo.size() < order.length / 4) {
                    fo.sort((o1, o2) -> {
                        for (int num : order) {
                            if (num == o1) return -1;
                            if (num == o2) return 1;
                        }
                        return 0;
                    });
                    featureOrder.add(fo);
                }
            }
        }
        return featureOrder;
    }

    public List<Chromosome> getParetoFront(List<Chromosome> list) {
        for (Chromosome chromosome : list) {
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getPoor().clear();
            chromosome.getBetter().clear();
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

        List<Chromosome> front = new ArrayList<>();
        for (Chromosome chromosome : list) {
            if (chromosome.getBetterNum() == 0) front.add(chromosome);
        }
        for (Chromosome chromosome : list) {
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getPoor().clear();
            chromosome.getBetter().clear();
        }
        return front;
    }

    public void setBetterAndPoor(Chromosome better, Chromosome poor) {
        better.getPoor().add(poor);
        poor.getBetter().add(better);
        better.addPoor();
        poor.addBetter();
    }


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
            int size = IOUtils.readIntProperties("dnsgaii-random", "file.taskGraph.size");
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
}
