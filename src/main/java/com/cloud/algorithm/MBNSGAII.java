package com.cloud.algorithm;

import com.cloud.algorithm.change.InsAvailChange;
import com.cloud.algorithm.repair.CrashRandomRepair;
import com.cloud.entity.Chromosome;
import com.cloud.entity.Result;
import com.cloud.utils.ChromosomeUtils;

import java.util.*;

public class MBNSGAII extends DNSGAII{
    /**
     * Memory-based NSGAII
     * @param name
     */
    public List<Chromosome> memoryPool = new LinkedList<>();
    public int poolSize;
    public MBNSGAII(String name) {
        super(name);
    }

    @Override
    public Result execute() {
        this.poolSize = this.size;
        init();
        initPopulation();
        if(change==null) change = new InsAvailChange();
        if(repair==null) repair = new CrashRandomRepair();
        for(int i=0;i<generation;++i){
            if(change instanceof InsAvailChange){
                if(InsAvailChange.generations.contains(i)) {


                    int insertNum = 20;
                    for (int j = 0; j < insertNum; j++) {
                        if(this.memoryPool.size()>0) {
                            this.son.add(this.memoryPool.remove(0));
                        }
                    }
                    change.change(this);
                    repair.repair(this);
                }
            }
//            System.out.println("Generation" + i);
            iterate();
        }

        Result result = new Result();
        List<List<double[]>> fronts = new ArrayList<>();
        for(List<Chromosome> list:all){
            List<double[]> front = new ArrayList<>();
            for(Chromosome chromosome:list){
                front.add(new double[]{chromosome.getMakeSpan(),chromosome.getCost()});
            }
            fronts.add(front);
        }
        result.map.put("fronts", fronts);
        System.out.println("------MB FINISH------");
        return result;
    }

    @Override
    public void iterate() {
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

                this.memoryPool.add(rank.get(0).get(k).clone());
                if(this.memoryPool.size() > this.poolSize){
                    this.memoryPool.remove(0);
                }
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        all.add(list);
    }
}
