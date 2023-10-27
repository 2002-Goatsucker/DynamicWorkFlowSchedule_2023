package com.cloud.utils.ESS;

import com.cloud.entity.CMSWCSolution;
import com.cloud.entity.CMSWCVM;
import com.cloud.utils.BaseEliteStrategy;
import com.cloud.utils.CMSWCUtils;
import com.cloud.utils.ChromosomeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * It randomly selects two instances Ij and Ik , and combines tasks of these two instances to either Ij or Ik .
 */
public class ESS6 extends BaseEliteStrategy {
    Random random;
    public ESS6() {
        super();
        random = new Random(0);
    }

    @Override
    public CMSWCSolution applyStrategy(CMSWCSolution solution) {
        try {
            CMSWCSolution ss = solution.clone();
            //随机选2个ins类型
            List<Integer> assignedType = new ArrayList<>(ss.getAssignedType());
            int randomType1 = assignedType.get(random.nextInt(assignedType.size()));
            int randomType2 = assignedType.get(random.nextInt(assignedType.size()));
            if (randomType1 != randomType2){
                //分别随机选1个type1和type2的ins
                CMSWCVM vm1 = ss.getInsPool().get(randomType1).get(random.nextInt(ss.getInsPool().get(randomType1).size()));
                CMSWCVM vm2 = ss.getInsPool().get(randomType2).get(random.nextInt(ss.getInsPool().get(randomType2).size()));
                double Phi = random.nextDouble();
                if (Phi < 0.5){
                    //把vm2的task全给vm1
                    for (int i = 0; i < vm2.getTaskList().size(); i++) {
                        vm1.getTaskList().add(vm2.getTaskList().get(i));
                        ss.getTasks()[vm2.getTaskList().get(i)].setInsType(randomType1);
                    }
                    ss.getInsPool().get(randomType2).remove(vm2);
                    if (ss.getInsPool().get(randomType2).size() == 0){
                        ss.getAssignedType().remove(randomType2);
                    }
                    //重新按rankU排序
//                    if (!CMSWCUtils.checkTopology(CMSWCUtils.getTaskList(vm1.getTaskList(),ss.getTasks()))){
//                        vm1.setTaskList(CMSWCUtils.randomTopologicalSort(vm1.getTaskList(),ss.getTasks()));
//                        System.out.println(vm1.getTaskList());
//                    }
                    vm1.getTaskList().sort((a, b) -> Double.compare(ss.getTasks()[b].getCmswcRank(), ss.getTasks()[a].getCmswcRank()));

                } else {
                    //把vm1的task全给vm2
                    for (int i = 0; i < vm1.getTaskList().size(); i++) {
                        vm2.getTaskList().add(vm1.getTaskList().get(i));
                        ss.getTasks()[vm1.getTaskList().get(i)].setInsType(randomType2);
                    }
                    ss.getInsPool().get(randomType1).remove(vm1);
                    if (ss.getInsPool().get(randomType1).size() == 0){
                        ss.getAssignedType().remove(randomType1);
                    }
                    //重新按rankU排序
//                    if (!CMSWCUtils.checkTopology(CMSWCUtils.getTaskList(vm2.getTaskList(),ss.getTasks())))
//                        vm2.setTaskList(CMSWCUtils.randomTopologicalSort(vm2.getTaskList(),ss.getTasks()));
                    vm2.getTaskList().sort((a, b) -> Double.compare(ss.getTasks()[b].getCmswcRank(), ss.getTasks()[a].getCmswcRank()));
                }
            }
            ss.update();
            return ss;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
//[17, 41, 65]
//[79, 0, 7, 31, 17, 41, 65]
//0 7 31 55 79
//[70, 31, 0, 22, 55, 79]
