package com.cloud.algorithm;

import com.cloud.algorithm.standard.Algorithm;
import com.cloud.entity.*;
import com.cloud.utils.CMSWCUtils;
import com.cloud.utils.ESS.*;
import com.cloud.utils.EliteStrategy;
import com.cloud.utils.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CMSWC extends Algorithm {
    public Task[] tasks;    //维护排好序的tasks
    public TaskGraph graph;
    public int bw;
    public int K;
    public int insType;
    public double exploitRate;
    public List<EliteStrategy> strategies;
    public CMSWC(String name) {
        super(name);
    }

    @Override
    public Result execute() {
        init();
        upwardRank(tasks);
        Task [] unsortedTasks = new Task[tasks.length]; //unsortedTasks保留原task的index
        for (int i = 0; i < tasks.length; i++) {
            unsortedTasks[i] = tasks[i].clone();
        }
        Arrays.sort(tasks, (o1, o2) -> {
            if (o1.getCmswcRank() - o2.getCmswcRank()>0)
                return -1;
            else return 1;
        });
        List<CMSWCSolution> solutions = null;
        if (CMSWCUtils.checkTopology(tasks)){
            solutions = TaskMapping(tasks,unsortedTasks);
            try(BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/result/result_cmswc.txt")))
            {
                for(CMSWCSolution s: solutions){
                    out.write(s.getMakeSpan() + " " + s.getCost()+"\n");

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Result result = new Result();
        result.map.put("CMSWC", solutions);
        return result;
    }



    public void init() {
        bw = IOUtils.readIntProperties("cmswc", "ins.bandwidth");
        K = IOUtils.readIntProperties("cmswc", "solution.number");
        insType = IOUtils.readIntProperties("cmswc", "ins.type");
        exploitRate = IOUtils.readDoubleProperties("cmswc", "exploitRate");
        strategies = new ArrayList<>();
        strategies.add(new ESS1());
        strategies.add(new ESS2());
        strategies.add(new ESS3());
        strategies.add(new ESS4());
        strategies.add(new ESS5());
        strategies.add(new ESS6());
        strategies.add(new ESS7());
        input();
    }

    public void input(){
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


    public double calculateRankU(Task t){
        double avgEtime = getAvgEtime(t);
        if (t.getSuccessor().isEmpty()){
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

    private double getAvgEtime(Task t) {
        double totalEtime = 0;
        for (int i = 0; i < ReadOnlyData.types.length; i++) {
            totalEtime += t.getReferTime() / ReadOnlyData.types[i].cu;
        }
        return totalEtime / 8;
    }

    public void upwardRank(Task[] tasks){
        for (Task t : tasks) {
            t.setCmswcRank(calculateRankU(t));
        }
    }

    private List<CMSWCSolution> TaskMapping(Task[] tasks,Task[] unsortedTasks) {
        List<CMSWCSolution> S = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            S.add(new CMSWCSolution(unsortedTasks));                 //𝑆𝑒 ← (∅, ∅, 0, 0)
        }
        try {
            for (int i = 0; i < tasks.length; i++) {  // each task
                if (i % 100 == 0)
                    System.out.println("分配第"+i+"个任务...");
                ArrayList<CMSWCSolution> intermediateSolution = new ArrayList<>();  //S'← ∅
                for (CMSWCSolution s: S) {   //each solution
                    for (int j = 0; j < s.getInsPool().size(); j++) {
                        for (int k = 0; k < s.getInsPool().get(j).size(); k++) {
                            CMSWCSolution interSol = s.clone();
                            interSol.getAssignedTask().add(tasks[i].getIndex());
                            CMSWCVM vm = interSol.getInsPool().get(j).get(k);   //第j种类型的第k个实例
                            int indexOfTask = tasks[i].getIndex();
                            vm.getTaskList().add(indexOfTask);
                            interSol.getTasks()[indexOfTask].setInsType(vm.getType());
                            interSol.update();
                            intermediateSolution.add(interSol);
                        }
                    }

                    for (int j = 0; j < insType; j++) {
                        CMSWCSolution interSol = s.clone();
                        interSol.getAssignedTask().add(tasks[i].getIndex());
                        CMSWCVM newIns = new CMSWCVM(j);
                        int indexOfTask = tasks[i].getIndex();
                        newIns.getTaskList().add(indexOfTask);
                        interSol.getTasks()[indexOfTask].setInsType(j);
                        interSol.getInsPool().get(j).add(newIns);
                        interSol.getAssignedType().add(j);

                        interSol.update();
                        intermediateSolution.add(interSol);
                    }
                }
                List<List<CMSWCSolution>> rank = quickNondominatedSort(intermediateSolution); //选择前K个Pareto最优解
                S.clear();
                int front = 0;
                while (S.size() < K && front < rank.size()){
                    if (S.size() + rank.get(front).size() > K){
                        List<CMSWCSolution> rankk = SDEDensitySelection(rank.get(front),K- S.size());
                        S.addAll(rankk);
                        break;
                    }
                    S.addAll(rank.get(front));
                    front++;
                }
                List<CMSWCSolution> S_ = EliteStudyStrategy(S);
                S_.addAll(S);
                if (i == tasks.length - 1){
                    return quickNondominatedSort(S_).get(0);
                } else {
                    S = SDEDensitySelection(S_,K);
                }
            }
            return S;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<List<CMSWCSolution>> quickNondominatedSort(List<CMSWCSolution> intermediateSolution) {
        List<List<CMSWCSolution>> rank = new ArrayList<>();
        intermediateSolution.sort((a, b) -> Double.compare(a.getMakeSpan(), b.getMakeSpan()));
        int i = 0;
        while(!intermediateSolution.isEmpty()){
            rank.add(new ArrayList<>());
            rank.get(i).add(intermediateSolution.get(0));
            double cost = intermediateSolution.get(0).getCost();
            for (int j = 1; j < intermediateSolution.size(); j++) {
                if (intermediateSolution.get(j).getCost() < cost){
                    rank.get(i).add(intermediateSolution.get(j));
                    cost = intermediateSolution.get(j).getCost();
                }
            }
            for (CMSWCSolution solution : rank.get(i)) {
                intermediateSolution.remove(solution);
            }
            i++;
        }
        return rank;
    }

    private List<CMSWCSolution> SDEDensitySelection(List<CMSWCSolution> S, int n) {
        int Phi = S.size();
        List<CMSWCSolution> S_ = new ArrayList<>();
        for (CMSWCSolution solution : S) {
            solution.setCrowdingDist(0);
        }
        S.sort((a, b) -> Double.compare(a.getMakeSpan(), b.getMakeSpan()));
        S.get(0).setCrowdingDist(Double.MAX_VALUE);
        S.get(Phi - 1).setCrowdingDist(Double.MAX_VALUE);
        for (int i = 1; i < Phi - 1; i++) {
            double S_l = S.get(i-1).SDE(S.get(i).getMakeSpan(),"makespan");
            double S_r = S.get(i+1).SDE(S.get(i).getMakeSpan(),"makespan");
            double delta = S_r - S_l;
            S.get(i).setCrowdingDist(S.get(i).getCrowdingDist() + delta / (S.get(Phi-1).getMakeSpan() - S.get(0).getMakeSpan()));
        }
        S.sort((a, b) -> Double.compare(a.getCost(), b.getCost()));
        S.get(0).setCrowdingDist(Double.MAX_VALUE);
        S.get(Phi - 1).setCrowdingDist(Double.MAX_VALUE);
        for (int i = 1; i < Phi - 1; i++) {
            double S_l = S.get(i-1).SDE(S.get(i).getCost(),"cost");
            double S_r = S.get(i+1).SDE(S.get(i).getCost(),"cost");
            double delta = S_r - S_l;
            S.get(i).setCrowdingDist(S.get(i).getCrowdingDist() + delta / (S.get(Phi-1).getCost() - S.get(0).getCost()));
        }
        S.sort((a, b) -> Double.compare(b.getCrowdingDist(), a.getCrowdingDist()));
        for (int i = 0; i < n; i++) {
            S_.add(S.get(i));
        }
        return S_;
    }

    private List<CMSWCSolution> EliteStudyStrategy(List<CMSWCSolution> S) {
        EliteStrategy strategy;
        Random random = new Random(0);
        List<CMSWCSolution> S_ = new ArrayList<>();
        for (CMSWCSolution solution : S) {
            double t = random.nextDouble();
            if (t < exploitRate) { //choose from {ESS1,2,3,4}
                strategy = strategies.get(random.nextInt(4));
            } else {            //choose from {ESS5,6,7}
                strategy = strategies.get(4 + random.nextInt(3));
            }
            S_.add(strategy.applyStrategy(solution));
        }

        return S_;
    }
}

