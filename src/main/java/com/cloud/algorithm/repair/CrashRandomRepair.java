package com.cloud.algorithm.repair;

import com.cloud.algorithm.DNSGAII;
import com.cloud.algorithm.change.InsAvailChange;
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
        DNSGAII dnsgaii = (DNSGAII) algorithm;
        List<Chromosome> chromosomes = dnsgaii.fa;
        List<Integer> accessibleIns = dnsgaii.accessibleIns;
        List<Integer> disabledIns = dnsgaii.disabledIns;
        Random random = dnsgaii.random;
        Task[] tasks = dnsgaii.tasks;
        for (Chromosome chromosome : chromosomes) {
            for (int i = 0; i < chromosome.getTask2ins().length; ++i) {
                if (disabledIns.contains(chromosome.getTask2ins()[i])) {
                    chromosome.getTask2ins()[i] = accessibleIns.get(random.nextInt(accessibleIns.size()));
                }
            }
            ChromosomeUtils.refresh(chromosome, tasks);
        }
    }
}
