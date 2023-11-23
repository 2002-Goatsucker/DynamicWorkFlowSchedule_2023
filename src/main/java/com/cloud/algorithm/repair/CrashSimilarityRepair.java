package com.cloud.algorithm.repair;

import com.cloud.algorithm.CMSWC;
import com.cloud.algorithm.DNSGAII;
import com.cloud.algorithm.FOGMP;
import com.cloud.algorithm.standard.Algorithm;
import com.cloud.algorithm.standard.Repair;
import com.cloud.entity.Chromosome;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Task;
import com.cloud.entity.Type;
import com.cloud.utils.ChromosomeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrashSimilarityRepair implements Repair {

    @Override
    public void repair(Algorithm algorithm) {
        List<Chromosome> chromosomes = null;
        List<Integer> accessibleIns = null;
        List<Integer> disabledIns = null;
        Random random = null;
        Task[] tasks = null;
        if(algorithm instanceof DNSGAII dnsgaii) {
            chromosomes = dnsgaii.fa;
            accessibleIns = dnsgaii.accessibleIns;
            disabledIns = dnsgaii.disabledIns;
            random = dnsgaii.random;
            tasks = dnsgaii.tasks;
            for (Chromosome chromosome : chromosomes) {
                for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                    if (disabledIns.contains(chromosome.getTask2ins()[i])) {
                        chromosome.getTask2ins()[i] = getSimilarityIns(accessibleIns, chromosome.getTask2ins()[i], random);
                    }
                }
                ChromosomeUtils.refresh(chromosome, tasks);
            }
        }else if (algorithm instanceof FOGMP fogmp){
            accessibleIns = fogmp.accessibleIns;
            disabledIns = fogmp.disabledIns;
            random = fogmp.random;
            tasks = fogmp.tasks;
            for (Chromosome chromosome : fogmp.ca) {
                for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                    if (disabledIns.contains(chromosome.getTask2ins()[i])) {
                        chromosome.getTask2ins()[i] = getSimilarityIns(accessibleIns, chromosome.getTask2ins()[i], random);
                    }
                }
                ChromosomeUtils.refresh(chromosome, tasks);
            }
            for (int i=0; i<fogmp.da.size();++i) {
                Chromosome chromosome = fogmp.da.get(i);
                for (int j = 0; j < chromosome.getTask2ins().length; ++j) {
                    if (disabledIns.contains(chromosome.getTask2ins()[j])) {
                        fogmp.da.remove(i);
                        fogmp.da.add(i, ChromosomeUtils.getInitialChromosome(fogmp.graph,fogmp.accessibleIns,fogmp.random));
                    }
                }
                ChromosomeUtils.refresh(chromosome, tasks);
            }
        }
    }

    public int getSimilarityIns(List<Integer> accessibleIns, int ins, Random random){
        Type type = ReadOnlyData.types[ReadOnlyData.insToType.get(ins)];
        double min = Double.MAX_VALUE;
        List<Integer> repairIns = new ArrayList<>();
        for(int i:accessibleIns){
            Type t = ReadOnlyData.types[ReadOnlyData.insToType.get(i)];
            double dis = Math.abs((t.bw-type.bw)/(131072000 - 39321600)) + Math.abs((t.cu-type.cu)/(30/8.0-1.7/8)) + Math.abs((t.p-type.p)/(0.9-0.06));
            min = Math.min(min, dis);
        }
        for(int i: accessibleIns){
            Type t = ReadOnlyData.types[ReadOnlyData.insToType.get(i)];
            double dis = Math.abs((t.bw-type.bw)/(131072000 - 39321600)) + Math.abs((t.cu-type.cu)/(30/8.0-1.7/8)) + Math.abs((t.p-type.p)/(0.9-0.06));
            if(dis==min||Math.abs(dis-min)<0.000000001){
                repairIns.add(i);
            }
        }
        return repairIns.get(random.nextInt(repairIns.size()));
    }
}
