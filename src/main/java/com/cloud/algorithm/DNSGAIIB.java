package com.cloud.algorithm;

import com.cloud.algorithm.change.InsAvailChange;
import com.cloud.algorithm.repair.CrashRandomRepair;
import com.cloud.algorithm.repair.CrashSimilarityRepair;
import com.cloud.entity.Chromosome;
import com.cloud.entity.Result;
import com.cloud.utils.ChromosomeUtils;

import java.util.ArrayList;
import java.util.List;

public class DNSGAIIB extends DNSGAII{
    public DNSGAIIB(String name) {
        super(name);
    }

    @Override
    public Result execute() {
        init();
        if(change==null) change = new InsAvailChange();
        if(repair==null) repair = new CrashRandomRepair();
        initPopulation();
        for(int i=0;i<generation;++i){
            if(change instanceof InsAvailChange){
                if(InsAvailChange.generations.contains(i)) {
                    change.change(this);
                    repair.repair(this);
                    for(Chromosome chromosome: fa){
                        ChromosomeUtils.mutate(chromosome,1.0,tasks,random,accessibleIns);
                    }
                }
            }
            iterate();
        }

//        List<Double> hv = ChromosomeUtils.getHV(all);
        Result result = new Result();
        List<double[]> front = new ArrayList<>();
        for(Chromosome chromosome: all.get(all.size()-1)) {
            front.add(new double[]{chromosome.getMakeSpan(), chromosome.getCost()});
        }
        result.map.put("front", front);
        System.out.println("-------dnsgaiiB Finish-------");
        return result;
    }
}
