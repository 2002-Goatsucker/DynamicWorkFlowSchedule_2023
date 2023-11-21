package com.cloud.utils.ESS;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.Chromosome;
import com.cloud.utils.BaseEliteStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * It randomly selects an assigned task t_i and finds an existed instance for t_i.
 */
public class ESS1 extends BaseEliteStrategy {
    Random random;
    public ESS1(Random random) {
        super();
        this.random = random;
    }

    @Override
    public Chromosome applyStrategy(Chromosome chromosome,int i, CMSWC algorithm) {
        try {
            if (i == 0){
                return chromosome.clone();
            }
            Chromosome chromosome1 = chromosome.clone();
            int task = random.nextInt(i);
            List<Integer> existIns = new ArrayList<>(chromosome1.getExistIns());
            chromosome1.getTask2ins()[task] = existIns.get(random.nextInt(existIns.size()));
            return chromosome1;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

}
