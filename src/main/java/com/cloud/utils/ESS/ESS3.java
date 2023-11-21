package com.cloud.utils.ESS;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.Chromosome;
import com.cloud.utils.BaseEliteStrategy;

import java.util.Random;

/**
 * It randomly selects two tasks on different instances and exchanges their positions
 */
public class ESS3 extends BaseEliteStrategy {
    Random random;
    public ESS3(Random random) {
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
            int task1 = random.nextInt(i);
            int task2 = random.nextInt(i);
            int temp = chromosome1.getTask2ins()[task2];
            chromosome1.getTask2ins()[task2] = chromosome1.getTask2ins()[task1];
            chromosome1.getTask2ins()[task1] = temp;
            return chromosome1;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

}