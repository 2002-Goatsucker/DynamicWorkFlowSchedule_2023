package com.cloud.algorithm.repair;

import com.cloud.algorithm.CMSWC;
import com.cloud.algorithm.DNSGAII;
import com.cloud.algorithm.FOGMP;
import com.cloud.algorithm.standard.Algorithm;
import com.cloud.algorithm.standard.Repair;
import com.cloud.entity.Chromosome;
import com.cloud.entity.Task;
import com.cloud.utils.ChromosomeUtils;

import java.util.List;
import java.util.Random;

public class CrashRandomRepair implements Repair {

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
        }else if (algorithm instanceof FOGMP fogmp){
            chromosomes = fogmp.ca;
            accessibleIns = fogmp.accessibleIns;
            disabledIns = fogmp.disabledIns;
            random = fogmp.random;
            tasks = fogmp.tasks;
        }
        for (Chromosome chromosome : chromosomes) {
            for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                if (disabledIns.contains(chromosome.getTask2ins()[i])) {
                    chromosome.getTask2ins()[i] = accessibleIns.get(random.nextInt(accessibleIns.size()));
                }
            }
            ChromosomeUtils.refresh(chromosome, tasks);
        }
    }

    public void repair(CMSWC cmswc, List<Chromosome> fa){
        List<Integer> accessibleIns = cmswc.accessibleIns;
        List<Integer> disabledIns = cmswc.disabledIns;
        Random random = cmswc.random;
        Task[] tasks = cmswc.tasks;
        for (Chromosome chromosome : fa) {
            for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                if (disabledIns.contains(chromosome.getTask2ins()[i])) {
                    chromosome.getTask2ins()[i] = accessibleIns.get(random.nextInt(accessibleIns.size()));
                }
            }
            ChromosomeUtils.refresh(chromosome, tasks);
        }
    }
}
