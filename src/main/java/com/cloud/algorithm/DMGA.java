package com.cloud.algorithm;

import com.cloud.entity.*;

import java.util.*;

public class DMGA extends DNSGAII {

    public List<Chromosome> population = new ArrayList<>();
    public List<Chromosome> superior = new ArrayList<>();
    public List<Chromosome> normal = new ArrayList<>();
    public List<Chromosome> inferior = new ArrayList<>();
    public List<List<Integer>> goodLCS = new ArrayList<>();
    public List<List<Integer>> badLCS = new ArrayList<>();

    Random random = new Random();
    public DMGA(String name) {
        super(name);
    }
    @Override
    public Result execute() {
        return null;
    }

    /**
     * @Description: 初始化种群，
     */
    public void initPopulation(){
        for(int i=0;i<size;++i){
            Chromosome chromosome = new Chromosome();
            //根据DMGA，task基因应该为随机拓扑序列
            chromosome.setTask(graph.TopologicalSorting(random));
            //大于平均大小的任务，要使用最好的机器执行
            double meanReferTime = 0;
            for(Task task:tasks){
                meanReferTime+=task.getReferTime();
            }
            meanReferTime /= tasks.length;


            int[] ins = new int[chromosome.getTask().length];
            int best = 0;
            for(int x=0;x<ReadOnlyData.insToType.size();++x){
                Type t = ReadOnlyData.types[ReadOnlyData.insToType.get(x)];
                if(Math.abs(t.cu - 30/8.0) < 0.00001){
                    best = x;
                    break;
                }
            }
            for(int j=0;j<ins.length;++j){
                if(tasks[chromosome.getTask()[j]].getReferTime()>meanReferTime){
                    ins[j] = best;
                }else {
                    ins[j] = accessibleIns.get(random.nextInt(accessibleIns.size()));
                }
            }
            chromosome.setTask2ins(ins);
            population.add(chromosome);
        }
    }

    //DMGA源论文代码中的获取LCS的方法
    public static List<Integer> getLCS(int[] a,int[]b) {
        int m=a.length;
        int n=b.length;
        int[][] c = new int[m+1][n+1];

        //初始化二维数组
        for (int i = 0; i < m+1; i++) {
            c[i][0] = 0;
        }
        for (int i = 0; i < n+1; i++) {
            c[0][i] = 0;
        }

        //实现公式逻辑
        int[][] path = new int[m+1][n+1];//记录通过哪个子问题解决的，也就是递推的路径
        for (int i = 1; i < m+1; i++) {
            for (int j = 1; j < n+1; j++) {
                if(a[i-1] == b[j-1]){
                    c[i][j] = c[i-1][j-1] + 1;
                    path[i][j]=1;
                }else if(c[i-1][j] >= c[i][j-1]){
                    c[i][j] = c[i-1][j];
                    path[i][j] = 2;
                }else{
                    c[i][j] = c[i][j-1];
                    path[i][j] = 3;
                }
            }
        }
        int i=m-1;
        int j=n-1;
        ArrayList<Integer> same=new ArrayList<Integer>();
        int k=0;
        while (i>0 && j>0) {
            if (path[i][j]==1) {
                same.add(a[i-1]);
                k++;
                i-=1;
                j-=1;
            }
            else if (path[i][j]==2) {
                i-=1;
            }
            else {
                j-=1;
            }
        }
        Collections.reverse(same);
        return same;

    }

    public void dividePopulation(){
        List<Chromosome> list = new LinkedList<>(population);

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
        for (List<Chromosome> l : rank) {
            l.sort((o1, o2) -> {
                if (o1.getMakeSpan() - o2.getMakeSpan() > 0.000000001) return 1;
                else if (o1.getMakeSpan() - o2.getMakeSpan() < -0.000000001) return -1;
                return 0;
            });
            l.get(0).setCrowding(Double.MAX_VALUE);
            l.get(l.size() - 1).setCrowding(Double.MAX_VALUE);
            for (int i = 1; i < l.size() - 1; ++i) {
                l.get(i).setCrowding(Math.abs(l.get(i + 1).getMakeSpan() - l.get(i - 1).getMakeSpan()) * Math.abs(l.get(i + 1).getCost() - l.get(i - 1).getCost()));
            }
            l.sort((o1, o2) -> {
                double num = o1.getCrowding() - o2.getCrowding();
                if (num > 0) return -1;
                if (num < 0) return 1;
                return 0;
            });
        }
        int num=0;
        for(List<Chromosome> cl:rank){
            for(Chromosome chromosome:cl){
                if(num<size/3){
                    superior.add(chromosome);
                }else if(num<size*2/3){
                    normal.add(chromosome);
                }else {
                    inferior.add(chromosome);
                }
            }
        }
    }


}
