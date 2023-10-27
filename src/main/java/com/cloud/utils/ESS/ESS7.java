package com.cloud.utils.ESS;

import com.cloud.entity.CMSWCSolution;
import com.cloud.entity.CMSWCVM;
import com.cloud.utils.BaseEliteStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * It randomly selects one instance Ij , creates a new instance In with the same type of Ij , and migrates some
 * tasks from Ij to In .
 */
public class ESS7 extends BaseEliteStrategy {
    Random random;
    public ESS7() {
        super();
        random = new Random(0);
    }

    @Override
    public CMSWCSolution applyStrategy(CMSWCSolution solution) {
        try {
            CMSWCSolution ss = solution.clone();
            //随机选1个ins类型
            List<Integer> assignedType = new ArrayList<>(ss.getAssignedType());
            int randomType1 = assignedType.get(random.nextInt(assignedType.size()));
            //随机选1个type1的ins
            CMSWCVM vm1 = ss.getInsPool().get(randomType1).get(random.nextInt(ss.getInsPool().get(randomType1).size()));
            //如果vm1中只有1个task，跳过
            if (vm1.getTaskList().size()>=2){
                CMSWCVM newIns = new CMSWCVM(randomType1);
                ss.getInsPool().get(randomType1).add(newIns);

                //将vm1中一些task分配给newIns
                int n = vm1.getTaskList().size() / 2;
                for (int i = 0; i < n; i++) {
                    newIns.getTaskList().add(vm1.getTaskList().get(i));
                }
                for (int i = 0; i < n; i++) {
                    vm1.getTaskList().remove(0);
                }
            }
            ss.update();
            return ss;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
