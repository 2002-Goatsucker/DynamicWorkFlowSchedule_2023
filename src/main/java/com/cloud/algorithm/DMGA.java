package com.cloud.algorithm;

import com.cloud.entity.*;
import com.cloud.utils.CMSWCUtils;
import com.cloud.utils.ChromosomeUtils;
import com.cloud.utils.IOUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.cloud.utils.ChromosomeUtils.crossoverIns;

public class DMGA extends DNSGAII {
    private int popSize;
    private double mutationRate;
    private List<Chromosome> superior;
    private List<Chromosome> ordinary;
    private List<Chromosome> inferior;

    private List<List<Integer>> bestLCS;
    private List<List<Integer>> inferiorLCS;

    public DMGA(String name) {
        super(name);
    }

    @Override
    public Result execute() {
        super.execute();
        Result result = new Result();
        List<double[]> front = new ArrayList<>();
        for(Chromosome chromosome: all.get(all.size()-1)) {
            front.add(new double[]{chromosome.getMakeSpan(), chromosome.getCost()});
        }
        result.map.put("front", front);
        System.out.println("-------DMGA Finish-------");

        return result;
    }

    @Override
    public void iterate() {
        popDiv();
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
        for (int k = 0; k < rank.get(0).size(); ++k) {
            try {
                list.add(rank.get(0).get(k).clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        all.add(list);
//        writeFile(all.get(all.size() - 1));
    }

    @Override
    public void init() {
        input();
        generation = IOUtils.readIntProperties("dmga", "iteration.max");
        size = IOUtils.readIntProperties("dmga", "population.size");
        mutationRate = IOUtils.readDoubleProperties("dmga", "population.mutation");
        superior = new ArrayList<>();
        ordinary = new ArrayList<>();
        inferior = new ArrayList<>();
        bestLCS = new ArrayList<>();
        inferiorLCS = new ArrayList<>();
    }

    public void popDiv() {
        superior.clear();
        inferior.clear();
        ordinary.clear();
        bestLCS.clear();
        inferiorLCS.clear();
        doSort();
        doEliminate();
        for (int i = 0; i < fa.size(); i++) {
            if (i < size / 3) {
                superior.add(fa.get(i));
            } else if (i > 2 * size / 3) {
                inferior.add(fa.get(i));
            } else {
                ordinary.add(fa.get(i));
            }
        }

        int k = size / 10;   //popSize = 100时,k=10
        List<Integer> bestInd = new ArrayList<>();
        for (int j = 0; j < k; j++) {
            bestInd.add(j);
        }
        //根据Algorithm2，将前k个优秀个体随机分成5组,每组k/5个
        List<List<Chromosome>> group = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            group.add(new ArrayList<>());
            for (int l = 0; l < k / 5; l++) {
                int index = bestInd.get(random.nextInt(bestInd.size()));
                group.get(j).add(superior.get(index));
                bestInd.remove((Integer) index);
            }
            //获取bestLCS
            for (int l = 0; l < group.get(j).size() - 1; l++) {
                for (int m = l + 1; m < group.get(j).size(); m++) {
                    List<Integer> result = getLCS(group.get(j).get(l).getTask(), group.get(j).get(m).getTask());
                    if (result.size() != 0)
                        bestLCS.add(result);
                }
            }
        }

        List<Integer> inferiorInd = new ArrayList<>();
        for (int j = inferior.size() - 1; j >= inferior.size() - k; j--) {
            inferiorInd.add(j);
        }

        group = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            group.add(new ArrayList<>());
            for (int l = 0; l < k / 5; l++) {
                int index = inferiorInd.get(random.nextInt(inferiorInd.size()));
                group.get(j).add(inferior.get(index));
                inferiorInd.remove((Integer) index);
            }
            //获取inferiorLCS
            for (int l = 0; l < group.get(j).size() - 1; l++) {
                for (int m = l + 1; m < group.get(j).size(); m++) {
                    List<Integer> result = getLCS(group.get(j).get(l).getTask(), group.get(j).get(m).getTask());
                    if (result.size() != 0)
                        inferiorLCS.add(result);
                }
            }
        }

    }

    //DMGA源论文代码中的获取LCS的方法,A,B序列为两个个体的任务调度序列
    public List<Integer> getLCS(int[] a, int[] b) {
        int m = a.length;
        int n = b.length;
        int[][] c = new int[m + 1][n + 1];

        //初始化二维数组
        for (int i = 0; i < m + 1; i++) {
            c[i][0] = 0;
        }
        for (int i = 0; i < n + 1; i++) {
            c[0][i] = 0;
        }

        //实现公式逻辑
        int[][] path = new int[m + 1][n + 1];//记录通过哪个子问题解决的，也就是递推的路径
        for (int i = 1; i < m + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if (a[i - 1] == b[j - 1]) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                    path[i][j] = 1;
                } else if (c[i - 1][j] >= c[i][j - 1]) {
                    c[i][j] = c[i - 1][j];
                    path[i][j] = 2;
                } else {
                    c[i][j] = c[i][j - 1];
                    path[i][j] = 3;
                }
            }
        }
        int i = m - 1;
        int j = n - 1;
        ArrayList<Integer> same = new ArrayList<Integer>();
        int k = 0;
        while (i > 0 && j > 0) {
            if (path[i][j] == 1) {
                same.add(a[i - 1]);
                k++;
                i -= 1;
                j -= 1;
            } else if (path[i][j] == 2) {
                i -= 1;
            } else {
                j -= 1;
            }
        }
        Collections.reverse(same);
        return same;
    }

    @Override
    public void doProduce() {
        crossoverByLCS(superior);
        crossoverByLCS(inferior);
        ordinaryCrossover();
    }

    private void mutateByLCS(Chromosome chromosome) {

    }

    private void crossoverByLCS(List<Chromosome> LCS) {
        try {
            for (int i = 0; i < LCS.size() - 1; i++) {
                Chromosome parent1 = LCS.get(i).clone();
                Chromosome parent2 = LCS.get(i + 1).clone();
                List<Chromosome> children;
                if (LCS == superior)
                    children = crossover(parent1, parent2, "superior");
                else children = crossover(parent1, parent2, "inferior");
                Chromosome child1 = children.get(0);
                Chromosome child2 = children.get(1);
                son.add(child1);
                son.add(child2);
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 原论文中的交叉互换算子是错误的，考虑到以下依赖关系D={(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(4,7)}
     *
     * @param parent1
     * @param parent2
     * @param LCS
     * @return
     */
    private List<Chromosome> crossover(Chromosome parent1, Chromosome parent2, String LCS) {
        Chromosome chromosome1 = null;
        Chromosome chromosome2 = null;
        try {
            chromosome1 = (Chromosome) parent1.clone();
            chromosome2 = (Chromosome) parent2.clone();
            switch (LCS) {
                case "superior": {
                    //随机选一个特征序列
                    List<Integer> feature = bestLCS.get(random.nextInt(bestLCS.size()));
                    TaskGraph taskGraph1 = graph.clone();
                    TaskGraph taskGraph2 = graph.clone();
                    List<Integer> f = new ArrayList<>();
                    List<Integer> nonFeature1 = new ArrayList<>();
                    List<Integer> nonFeature2 = new ArrayList<>();
                    int j = 0;
                    for (int i = 0; i < tasks.length; i++) {
                        if (j < feature.size() && chromosome2.getTask()[i] == feature.get(j)) {
                            f.add(feature.get(j));
                            j++;
                            continue;
                        }
                        nonFeature1.add(chromosome2.getTask()[i]);
                    }
                    for (int i = 0; i < f.size() - 1; i++) {
                        taskGraph1.addEdge(f.get(i),f.get(i+1));
                    }
                    for (int i = 0; i < nonFeature1.size() - 1; i++) {
                        taskGraph1.addEdge(nonFeature1.get(i),nonFeature1.get(i+1));
                    }
                    int [] order1 = taskGraph1.TopologicalSorting(random);

                    f.clear();
                    j = 0;
                    for (int i = 0; i < tasks.length; i++) {
                        if (j < feature.size() && chromosome1.getTask()[i] == feature.get(j)) {
                            f.add(feature.get(j));
                            j++;
                            continue;
                        }
                        nonFeature2.add(chromosome1.getTask()[i]);
                    }
                    for (int i = 0; i < f.size() - 1; i++) {
                        taskGraph2.addEdge(f.get(i),f.get(i+1));
                    }
                    for (int i = 0; i < nonFeature2.size() - 1; i++) {
                        taskGraph2.addEdge(nonFeature2.get(i),nonFeature2.get(i+1));
                    }
                    int [] order2 = taskGraph2.TopologicalSorting(random);
                    chromosome1.setTask(order1);
                    chromosome2.setTask(order2);
                    chromosome1 = ChromosomeUtils.mutate(chromosome1, mutationRate, tasks, random, accessibleIns);
                    chromosome2 = ChromosomeUtils.mutate(chromosome2, mutationRate, tasks, random, accessibleIns);
                    break;
                }
                case "inferior": {
                    //随机选一个特征序列
                    List<Integer> feature = inferiorLCS.get(random.nextInt(inferiorLCS.size()));
                    //找到两条链中特征位置
                    TaskGraph taskGraph1 = graph.clone();
                    TaskGraph taskGraph2 = graph.clone();
                    List<Integer> nonFeature1 = new ArrayList<>();
                    List<Integer> nonFeature2 = new ArrayList<>();
                    int j = 0;
                    for (int i = 0; i < tasks.length; i++) {
                        if (j < feature.size() && chromosome1.getTask()[i] == feature.get(j)) {
                            j++;
                            continue;
                        }
                        nonFeature1.add(chromosome1.getTask()[i]);
                    }
                    for (int i = 0; i < nonFeature1.size() - 1; i++) {
                        taskGraph1.addEdge(nonFeature1.get(i), nonFeature1.get(i+1));
                    }

                    int [] order1 = taskGraph1.TopologicalSorting(random);

                    j = 0;
                    for (int i = 0; i < tasks.length; i++) {
                        if (j < feature.size() && chromosome2.getTask()[i] == feature.get(j)) {
                            j++;
                            continue;
                        }
                        nonFeature2.add(chromosome2.getTask()[i]);
                    }
                    for (int i = 0; i < nonFeature2.size() - 1; i++) {
                        taskGraph2.addEdge(nonFeature2.get(i), nonFeature2.get(i+1));
                    }
                    int [] order2 = taskGraph1.TopologicalSorting(random);
                    chromosome1.setTask(order1);
                    chromosome2.setTask(order2);
                    chromosome1 = ChromosomeUtils.mutate(chromosome1, mutationRate, tasks, random,accessibleIns);
                    chromosome2 = ChromosomeUtils.mutate(chromosome2, mutationRate, tasks, random,accessibleIns);
                    break;
                }
            }
            crossoverIns(chromosome1, chromosome2, random);

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        ChromosomeUtils.refresh(chromosome1, tasks);
        ChromosomeUtils.refresh(chromosome2, tasks);
        List<Chromosome> list = new ArrayList<>();
        list.add(chromosome1);
        list.add(chromosome2);
        return list;
    }



    public void ordinaryCrossover() {
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
                    child1 = ChromosomeUtils.mutate(child1, mutationRate, tasks, random,accessibleIns);
                    child2 = ChromosomeUtils.mutate(child2, mutationRate, tasks, random,accessibleIns);
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

    public void writeFile(List<Chromosome> pof) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/result/result_dmga.txt"))) {
            for (Chromosome chromosome : pof) {
                out.write(chromosome.getMakeSpan() + " " + chromosome.getCost() + "\n");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeEdge(TaskGraph graph){
        try (BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/result/edges.txt"))) {
            for (int[] edge: graph.edges) {
                out.write(edge[0] + " " + edge[1] + "\n");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean checkTopology(List<Integer> feature) {
        Set<Integer> set = new HashSet<>();
        for (int t: feature) {
            set.add(tasks[t].getIndex());
            for (Integer suc : tasks[t].getSuccessor()) {
                if (set.contains(suc)){
                    return false;
                }
            }
        }
        return true;
    }
}
