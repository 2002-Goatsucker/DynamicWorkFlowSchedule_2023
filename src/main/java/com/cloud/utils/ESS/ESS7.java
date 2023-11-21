package com.cloud.utils.ESS;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.Chromosome;
import com.cloud.utils.BaseEliteStrategy;

import java.util.Random;

/**
 * It randomly selects one instance Ij , creates a new instance In with the same type of Ij , and migrates some
 * tasks from Ij to In .
 */
public class ESS7 extends BaseEliteStrategy {
    Random random;
    public ESS7(Random random) {
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
            int newIns = chromosome1.getUnallocatedIns().get(0);
            for (int j = 0; j < chromosome1.getUnallocatedIns().size(); j++) {
                if (chromosome1.getUnallocatedIns().get(j) / 10 == type){
                    newIns = chromosome1.getUnallocatedIns().get(j);
                    break;
                }
            }
            double Phi;
            for (int j = 0; j < chromosome1.getTask2ins().length; j++) {
                if (chromosome1.getTask2ins()[j] == insIndex){
                    Phi = random.nextDouble();
                    if (Phi > 0.5) {
                        chromosome1.getTask2ins()[j] = newIns;
                    }
                }
            }
            chromosome1.getUnallocatedIns().remove((Integer) newIns);
            chromosome1.getExistIns().add(newIns);
            return chromosome1;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

}
