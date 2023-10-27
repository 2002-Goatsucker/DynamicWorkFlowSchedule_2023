package com.cloud.utils.ESS;

import com.cloud.entity.CMSWCSolution;
import com.cloud.entity.CMSWCVM;
import com.cloud.utils.BaseEliteStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * ESS4: It randomly selects two tasks on the same instance and exchanges their execution order.
 */
public class ESS4 extends BaseEliteStrategy {
    Random random;
    public ESS4() {
        super();
        random = new Random(0);
    }

    @Override
    public CMSWCSolution applyStrategy(CMSWCSolution solution) {
        try {
            CMSWCSolution ss = solution.clone();
            //随机选一个ins类型
            List<Integer> assignedType = new ArrayList<>(ss.getAssignedType());
            int randomType1 = assignedType.get(random.nextInt(assignedType.size()));
            //随机选一个该类型的ins
            CMSWCVM vm1 = ss.getInsPool().get(randomType1).get(random.nextInt(ss.getInsPool().get(randomType1).size()));
            if (vm1.getTaskList().size()>2){
                //随机选两个task
                Integer task1 = vm1.getTaskList().get(random.nextInt(vm1.getTaskList().size()));
                Integer task2 = vm1.getTaskList().get(random.nextInt(vm1.getTaskList().size()));

                //交换
                int indexOfTask1 = vm1.getTaskList().indexOf(task1);
                int indexOfTask2 = vm1.getTaskList().indexOf(task2);
                vm1.getTaskList().remove((Integer) task1);
                vm1.getTaskList().add(indexOfTask2,task1);
                vm1.getTaskList().remove((Integer)task2);
                vm1.getTaskList().add(indexOfTask1,task2);
                //重新按rankU排序
                vm1.getTaskList().sort((a, b) -> Double.compare(ss.getTasks()[b].getCmswcRank(), ss.getTasks()[a].getCmswcRank()));
            }
            ss.update();
            return ss;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }
}
