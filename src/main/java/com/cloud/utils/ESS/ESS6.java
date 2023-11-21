package com.cloud.utils.ESS;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.Chromosome;
import com.cloud.utils.BaseEliteStrategy;

import java.util.Random;

/**
 * It randomly selects two instances Ij and Ik , and combines tasks of these two instances to either Ij or Ik .
 */
public class ESS6 extends BaseEliteStrategy {
    Random random;
    public ESS6(Random random) {
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
            int ins1 = chromosome1.getExistIns().get(random.nextInt(chromosome1.getExistIns().size()));
            int ins2 = chromosome1.getExistIns().get(random.nextInt(chromosome1.getExistIns().size()));
            double Phi = random.nextDouble();
            if (Phi <= 0.5){    //ins1 -> ins2
                for (int j = 0; j < chromosome1.getTask2ins().length; j++) {
                    if (chromosome1.getTask2ins()[j] == ins1){
                        chromosome1.getTask2ins()[j] = ins2;
                    }
                }
                chromosome1.getExistIns().remove((Integer) ins1);
                chromosome1.getUnallocatedIns().add(ins1);
            } else {    //ins2 -> ins1
                for (int j = 0; j < chromosome1.getTask2ins().length; j++) {
                    if (chromosome1.getTask2ins()[j] == ins2){
                        chromosome1.getTask2ins()[j] = ins1;
                    }
                }
                chromosome1.getExistIns().remove((Integer) ins2);
                chromosome1.getUnallocatedIns().add(ins2);
            }
            return chromosome1;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

}
//[17, 41, 65]
//[79, 0, 7, 31, 17, 41, 65]
//0 7 31 55 79
//[70, 31, 0, 22, 55, 79]
