package com.cloud.utils.ESS;

import com.cloud.entity.CMSWCSolution;
import com.cloud.entity.CMSWCVM;
import com.cloud.utils.BaseEliteStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * It randomly selects an instance Ij and finds a new type for Ij .
 */
public class ESS5 extends BaseEliteStrategy {
    Random random;
    public ESS5() {
        super();
        random = new Random(0);
    }

    @Override
    public CMSWCSolution applyStrategy(CMSWCSolution solution) {
        try {
            CMSWCSolution ss = solution.clone();
//            //随机选2个ins类型
            List<Integer> assignedType = new ArrayList<>(ss.getAssignedType());
            int randomType1 = assignedType.get(random.nextInt(assignedType.size()));
            int randomType2 = assignedType.get(random.nextInt(assignedType.size()));
            if (randomType1 != randomType2){
                //随机选一个type1的ins
                CMSWCVM vm1 = ss.getInsPool().get(randomType1).get(random.nextInt(ss.getInsPool().get(randomType1).size()));
                //将vm1改成type2
                vm1.setType(randomType2);
                //将vm1中的task的type也改成type2
                for (int integer : vm1.getTaskList()) {
                    ss.getTasks()[integer].setInsType(randomType2);
                }
                //把vm1转移到insPool中的type2
                ss.getInsPool().get(randomType1).remove(vm1);
                ss.getInsPool().get(randomType2).add(vm1);
                //如果type1类型为0，assignedType移除type1
                if (ss.getInsPool().get(randomType1).size() == 0){
                    ss.getAssignedType().remove(randomType1);
                }
            }
            ss.update();
            return ss;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
