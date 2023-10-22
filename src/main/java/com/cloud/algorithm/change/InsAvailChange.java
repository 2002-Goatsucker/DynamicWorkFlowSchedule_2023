package com.cloud.algorithm.change;

import com.cloud.algorithm.DNSGAII;
import com.cloud.algorithm.standard.Algorithm;
import com.cloud.algorithm.standard.Change;
import com.cloud.utils.IOUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class InsAvailChange implements Change {
    public static final HashSet<Integer> generations=new HashSet<>();
    private static final double severity;
    static {
        int[] generation = IOUtils.readIntArrayProperties("dnsgaii-random", "crash.generation",",");
        if(generation[0]!=-1) {
            for(int num:generation){
                generations.add(num);
            }
        }
        severity = IOUtils.readDoubleProperties("dnsgaii-random", "crash.severity");
    }

    @Override
    public void change(Algorithm algorithm) {
        DNSGAII dnsgaii = (DNSGAII) algorithm;
        List<Integer> accessibleIns = dnsgaii.accessibleIns;
        List<Integer> disabledIns = dnsgaii.disabledIns;
        Random random = dnsgaii.random;
        int num  = (int) (accessibleIns.size() *severity);
        for(int i=0;i<num;++i){
            int index = random.nextInt(accessibleIns.size());
            int ins = accessibleIns.get(index);
            accessibleIns.remove(index);
            disabledIns.add(ins);
        }
    }
}
