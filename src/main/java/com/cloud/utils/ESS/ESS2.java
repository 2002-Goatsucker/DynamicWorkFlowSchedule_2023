package com.cloud.utils.ESS;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.Chromosome;
import com.cloud.utils.BaseEliteStrategy;

import java.util.Random;


/**
 * It randomly selects a task ti and creates an new instance for ti .
 */
public class ESS2 extends BaseEliteStrategy {
    Random random;
    public ESS2(Random random) {
        super();
        this.random = random;
    }

    @Override
    public Chromosome applyStrategy(Chromosome chromosome, int i, CMSWC algorithm) {

        try {
            if (i == 0){
                return chromosome.clone();
            }
            Chromosome chromosome1 = chromosome.clone();
            int task = random.nextInt(i);
            int ins = algorithm.accessibleIns.get(random.nextInt(algorithm.accessibleIns.size()));
            while (chromosome1.getExistIns().contains(ins)){
                ins = algorithm.accessibleIns.get(random.nextInt(algorithm.accessibleIns.size()));
            }

            chromosome1.getTask2ins()[task] = ins;
            chromosome1.getExistIns().add(ins);
            chromosome1.getUnallocatedIns().remove((Integer) ins);
            return chromosome1;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

}
