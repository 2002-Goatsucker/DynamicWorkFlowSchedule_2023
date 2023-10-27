package com.cloud.utils.ESS;

import com.cloud.entity.CMSWCSolution;
import com.cloud.entity.CMSWCVM;
import com.cloud.utils.BaseEliteStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * It randomly selects two tasks on different instances and exchanges their positions
 */
public class ESS3 extends BaseEliteStrategy {
    Random random;
    public ESS3() {
        super();
        random = new Random(0);
    }

    @Override
    public CMSWCSolution applyStrategy(CMSWCSolution solution) {
        try {
            CMSWCSolution ss = solution.clone();
            //随机选两个task
//            int task1 = ss.getAssignedTask().get(random.nextInt(ss.getAssignedTask().size()));
//            int task2 = ss.getAssignedTask().get(random.nextInt(ss.getAssignedTask().size()));
//
//            //获得2个task的ins
//            CMSWCVM vm1 = null;
//            CMSWCVM vm2 = null;
//            for(CMSWCVM vm: ss.getInsPool().get(ss.getTasks()[task1].getInsType())){
//                for(int t: vm.getTaskList()){
//                    if (t == task1){
//                        vm1 = vm;
//                        break;
//                    }
//                }
//            }
//            for(CMSWCVM vm: ss.getInsPool().get(ss.getTasks()[task2].getInsType())){
//                for(int t: vm.getTaskList()){
//                    if (t == task2){
//                        vm2 = vm;
//                        break;
//                    }
//                }
//            }
//
//            //交换
//            vm1.getTaskList().remove((Integer) task1);
//            vm1.getTaskList().add(task2);
//            vm2.getTaskList().remove((Integer)task2);
//            vm2.getTaskList().add(task1);
//            ss.getTasks()[task1].setInsType(vm2.getType());
//            ss.getTasks()[task2].setInsType(vm1.getType());
//            vm1.getTaskList().sort((a, b) -> Double.compare(ss.getTasks()[b].getCmswcRank(), ss.getTasks()[a].getCmswcRank()));
//            vm2.getTaskList().sort((a, b) -> Double.compare(ss.getTasks()[b].getCmswcRank(), ss.getTasks()[a].getCmswcRank()));
//            ss.update();
            return ss;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }


    }
}
