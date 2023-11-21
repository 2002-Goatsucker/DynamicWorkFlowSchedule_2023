package com.cloud.utils.ESS;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.Chromosome;
import com.cloud.utils.BaseEliteStrategy;

import java.util.Random;

/**
 * It randomly selects an instance Ij and finds a new type for Ij .
 */
public class ESS5 extends BaseEliteStrategy {
    Random random;
    public ESS5(Random random) {
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
            int insIndex = chromosome1.getExistIns().get(random.nextInt(chromosome1.getExistIns().size()));
            int type = insIndex / 10;
            int anotherType = random.nextInt(8);
            while(anotherType == type) anotherType = random.nextInt(8);
            int newIns = chromosome1.getUnallocatedIns().get(0);
            for (int j = 0; j < chromosome1.getUnallocatedIns().size(); j++) {
                if (chromosome1.getUnallocatedIns().get(j) / 10 == anotherType){
                    newIns = chromosome1.getUnallocatedIns().get(j);
                    break;
                }
            }
            for (int j = 0; j < chromosome1.getTask2ins().length; j++) {
                if (chromosome1.getTask2ins()[j] == insIndex){
                    chromosome1.getTask2ins()[j] = newIns;
                }
            }
            chromosome1.getExistIns().add(newIns);
            chromosome1.getExistIns().remove((Integer) insIndex);
            chromosome1.getUnallocatedIns().add(insIndex);
            chromosome1.getUnallocatedIns().remove((Integer) newIns);

            return chromosome1;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }
}
