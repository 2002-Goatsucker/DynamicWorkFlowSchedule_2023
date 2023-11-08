package com.cloud.algorithm.change;

import com.cloud.algorithm.CMSWC;
import com.cloud.algorithm.standard.Algorithm;
import com.cloud.algorithm.standard.Change;
import com.cloud.utils.IOUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CMSWCInsAvailChange implements Change {
    public static final HashSet<Integer> crashTask=new HashSet<>();
    public Random random = new Random(0);
    private static final double severity;
    static {
        int[] task = IOUtils.readIntArrayProperties("cmswc", "crash.task",",");
        if(task[0]!=-1) {
            for(int num:task){
                crashTask.add(num);
            }
        }
        severity = IOUtils.readDoubleProperties("cmswc", "crash.severity");
    }
    @Override
    public void change(Algorithm algorithm) {
        CMSWC cmswc = (CMSWC) algorithm;
        List<Integer> accessibleIns = cmswc.accessibleIns;
        Set<Integer> disabledIns = cmswc.disabledIns;
        int num  = (int) (accessibleIns.size() *severity);
        for(int i=0;i<num;++i){
            int index = random.nextInt(accessibleIns.size());
            int ins = accessibleIns.get(index);
            accessibleIns.remove(index);
            disabledIns.add(ins);
            cmswc.insQuantity[ins/10] -= 1;
        }
    }
}
