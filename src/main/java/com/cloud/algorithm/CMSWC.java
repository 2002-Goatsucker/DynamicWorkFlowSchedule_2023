package com.cloud.algorithm;

import com.cloud.algorithm.change.CMSWCInsAvailChange;
import com.cloud.algorithm.repair.CrashSimilarityRepair;
import com.cloud.algorithm.standard.Algorithm;
import com.cloud.entity.*;
import com.cloud.utils.ChromosomeUtils;
import com.cloud.utils.ESS.*;
import com.cloud.utils.EliteStrategy;
import com.cloud.utils.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.*;

import static com.cloud.entity.ReadOnlyData.types;

public class CMSWC extends Algorithm {
    public Task[] tasks;    //原始任务排序
    public TaskGraph graph;
    public List<List<Chromosome>> rank;
    public int bw;  //用于估算rankU
    public int K;
    public int insType;
    public double exploitRate;
    public List<EliteStrategy> strategies;
    public CMSWCInsAvailChange crash;
    public CrashSimilarityRepair repair;
    public List<Integer> accessibleIns = new LinkedList<>();
    public List<Integer> disabledIns = new LinkedList<>();
    public Random random;

    public CMSWC(String name) {
        super(name);
    }

    @Override
    public Result execute() {
        init();
        upwardRank(tasks);
        int[] order = getOrder(tasks);
        List<Chromosome> solutions = TaskMapping(order);

        Result result = new Result();
        List<double[]> front = new ArrayList<>();
        for (Chromosome solution : solutions) {
            front.add(new double[]{solution.getMakeSpan(), solution.getCost()});
        }
        result.map.put("front", front);
        System.out.println("------CMSWC FINISH------");
        return result;
    }

    public void init() {
        input();
        bw = IOUtils.readIntProperties("cmswc", "ins.bandwidth");
        K = IOUtils.readIntProperties("cmswc", "solution.number");
        insType = IOUtils.readIntProperties("cmswc", "ins.type");
        exploitRate = IOUtils.readDoubleProperties("cmswc", "exploitRate");
        crash = new CMSWCInsAvailChange();
        repair = new CrashSimilarityRepair();
        strategies = new ArrayList<>();
        strategies.add(new ESS1(random));
        strategies.add(new ESS2(random));
        strategies.add(new ESS3(random));
        strategies.add(new ESS4(random));
        strategies.add(new ESS5(random));
        strategies.add(new ESS6(random));
        strategies.add(new ESS7(random));

    }

    public void input() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("cmswc");
            int taskSize = IOUtils.readIntProperties("cmswc", "file.taskGraph.size");
            graph = new TaskGraph(taskSize);
            tasks = new Task[taskSize];
            for (int i = 0; i < taskSize; i++) {
                tasks[i] = new Task(i);
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(DNSGAII.class.getClassLoader().getResource(bundle.getString("file.taskGraph.path")));
            Element root = document.getRootElement();
            for (Element child : root.elements()) {
                // 处理依赖关系
                if (child.getName().equals("child")) {
                    int ver2 = Integer.parseInt(child.attributeValue("ref").substring(2));
                    for (Element parent : child.elements()) {
                        int ver1 = Integer.parseInt(parent.attributeValue("ref").substring(2));
                        graph.addEdge(ver1, ver2);
                        tasks[ver1].getSuccessor().add(ver2);
                        tasks[ver2].getPredecessor().add(ver1);
                    }
                }
                // 处理任务属性
                else if (child.getName().equals("job")) {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void upwardRank(Task[] tasks) {
        for (Task t : tasks) {
            t.setRank(calculateRankU(t));
        }
    }

    public double calculateRankU(Task t) {
        double avgEtime = getAvgEtime(t);
        if (t.getSuccessor().isEmpty()) {
            return avgEtime;
        }

        double maxSucRank = 0;
        for (int suc : t.getSuccessor()) {
            double sucRank = calculateRankU(tasks[suc]);
            double load = t.getOutputSize() / bw;

            if (sucRank + load > maxSucRank) {
                maxSucRank = sucRank;
            }
        }

        return avgEtime + maxSucRank;
    }

    public double getAvgEtime(Task t) {
        double totalEtime = 0;
        for (int i = 0; i < ReadOnlyData.types.length; i++) {
            totalEtime += t.getReferTime() / ReadOnlyData.types[i].cu;
        }
        return totalEtime / ReadOnlyData.types.length;
    }

    public int[] getOrder(Task[] tasks) {
        Task[] originalTasks = new Task[tasks.length]; //unsortedTasks保留原task的index
        int[] order = new int[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
            originalTasks[i] = tasks[i].clone();
        }
        Arrays.sort(originalTasks, (o1, o2) -> {
            if (o1.getRank() - o2.getRank() > 0)
                return -1;
            else return 1;
        });
        for (int i = 0; i < tasks.length; i++) {
            order[i] = originalTasks[i].getIndex();
        }
        return order;
    }

    public List<Chromosome> TaskMapping(int[] order) {
        try {
            List<Chromosome> fa = new ArrayList<>();
            for (int i = 0; i < K; i++) {
                fa.add(new Chromosome(order, new int[order.length], accessibleIns));
            }
            for (int i = 0; i < tasks.length; i++) {
//                System.out.println(i);
                if (CMSWCInsAvailChange.crashTask.contains(i)){
                    crash.change(this);
                    repair.repair(this,fa);
                }
                List<Chromosome> son = new ArrayList<>();
                for (Chromosome parent : fa) {
                    for (int ins : parent.getExistIns()) {
                        Chromosome child = parent.clone();
                        child.getTask2ins()[i] = ins;
                        ChromosomeUtils.refresh(child, tasks);
                        son.add(child);
                    }

                    for (int j = 0; j < types.length; j++) {//从unallocatedIns中获取新机器，并将其从中移除
                        int ins = parent.getUnallocatedIns().get(0);
                        for (int insIndex : parent.getUnallocatedIns()) {
                            if (insIndex / 10 == j && !parent.getExistIns().contains(insIndex)) {
                                ins = insIndex;
                                break;
                            }
                        }
                        Chromosome child = parent.clone();
                        child.getTask2ins()[i] = ins;
                        child.getExistIns().add(ins);
                        child.getUnallocatedIns().remove((Integer) ins);
                        ChromosomeUtils.refresh(child, tasks);
                        son.add(child);
                    }
                }
                quickNondominatedSort(son);
                fa.clear();
                getNextGeneration(fa);
                List<Chromosome> eliteSon = EliteStrategy(fa, i);
                for (Chromosome chromosome : eliteSon) {
                    ChromosomeUtils.refresh(chromosome,tasks);
                }
                eliteSon.addAll(fa);
                if (i == tasks.length - 1){
                    quickNondominatedSort(eliteSon);
                    return rank.get(0);
                } else {
                    fa = SDEDensitySelection(eliteSon,K);
                }
            }
            return null;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void quickNondominatedSort(List<Chromosome> list) {
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
    public void getNextGeneration(List<Chromosome> fa) {
        int front = 0;
        while (fa.size() < K && front < rank.size()){
            if (fa.size() + rank.get(front).size() > K){
                List<Chromosome> rankk = SDEDensitySelection(rank.get(front),K- fa.size());
                fa.addAll(rankk);
                break;
            }
            fa.addAll(rank.get(front));
            front++;
        }
    }
    public List<Chromosome> SDEDensitySelection(List<Chromosome> fa, int n) {
        int Phi = fa.size();
        List<Chromosome> S_ = new ArrayList<>();
        for (Chromosome solution : fa) {
            solution.setCrowding(0);
        }
        fa.sort((a, b) -> Double.compare(a.getMakeSpan(), b.getMakeSpan()));
        fa.get(0).setCrowding(Double.MAX_VALUE);
        fa.get(Phi - 1).setCrowding(Double.MAX_VALUE);
        for (int i = 1; i < Phi - 1; i++) {
            double S_l = fa.get(i-1).SDE(fa.get(i).getMakeSpan(),"makespan");
            double S_r = fa.get(i+1).SDE(fa.get(i).getMakeSpan(),"makespan");
            double delta = S_r - S_l;
            fa.get(i).setCrowding(fa.get(i).getCrowding() + delta / (fa.get(Phi-1).getMakeSpan() - fa.get(0).getMakeSpan()));
        }
        fa.sort((a, b) -> Double.compare(a.getCost(), b.getCost()));
        fa.get(0).setCrowding(Double.MAX_VALUE);
        fa.get(Phi - 1).setCrowding(Double.MAX_VALUE);
        for (int i = 1; i < Phi - 1; i++) {
            double S_l = fa.get(i-1).SDE(fa.get(i).getCost(),"cost");
            double S_r = fa.get(i+1).SDE(fa.get(i).getCost(),"cost");
            double delta = S_r - S_l;
            fa.get(i).setCrowding(fa.get(i).getCrowding() + delta / (fa.get(Phi-1).getCost() - fa.get(0).getCost()));
        }
        fa.sort((a, b) -> Double.compare(b.getCrowding(), a.getCrowding()));
        for (int i = 0; i < n; i++) {
            S_.add(fa.get(i));
        }
        return S_;
    }
    public List<Chromosome> EliteStrategy(List<Chromosome> list, int i){
        EliteStrategy strategy;
        List<Chromosome> eliteSon = new ArrayList<>();
        for (Chromosome chromosome : list) {
            double t = random.nextDouble();
            if (t < exploitRate){
                strategy = strategies.get(random.nextInt(4));
            } else {
                strategy = strategies.get(4 + random.nextInt(3));
            }
            eliteSon.add(strategy.applyStrategy(chromosome,i,this));
        }
        return eliteSon;
    }
}
