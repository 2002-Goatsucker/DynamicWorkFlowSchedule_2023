package com.cloud.utils.ESS;

import com.cloud.entity.CMSWCSolution;
import com.cloud.entity.CMSWCVM;
import com.cloud.entity.Task;
import com.cloud.utils.BaseEliteStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * It randomly selects a task ti and creates an new instance for ti .
 */
public class ESS2 extends BaseEliteStrategy {
    Random random;
    public ESS2() {
        super();
        random = new Random(0);
    }

    @Override
    public CMSWCSolution applyStrategy(CMSWCSolution solution) {
        try {
            CMSWCSolution ss = solution.clone();
            //随机获取一个已分配task的index
            int index = ss.getAssignedTask().get(random.nextInt(ss.getAssignedTask().size()));
            //随机一个新的ins类型
            int randomType = random.nextInt(8);
            //获得随机取得的这个task的ins type
            int insType = ss.getTasks()[index].getInsType();
            //修改这个task的ins type
            ss.getTasks()[index].setInsType(randomType);

            //把该task从原来ins的taskList中删除
            loop:for(CMSWCVM vm: ss.getInsPool().get(insType)){
                for(int t: vm.getTaskList()){
                    if (t == index){
                        vm.getTaskList().remove((Integer) t);
                        //如果移除后vm的taskList变为空，则移除该vm
                        if (vm.getTaskList().size() == 0) {
                            ss.getInsPool().get(insType).remove(vm);
                            if (ss.getInsPool().get(insType).size() == 0)
                                ss.getAssignedType().remove(vm.getType());
                        }
                        break loop;
                    }
                }
            }
            if (ss.getInsPool().get(randomType).size() == 0){
                ss.getInsPool().get(randomType).add(new CMSWCVM(randomType));
                ss.getAssignedType().add(randomType);
            }
            //把task放到随机选的ins中
            CMSWCVM vm1 = ss.getInsPool().get(randomType).get(random.nextInt(ss.getInsPool().get(randomType).size()));
            vm1.getTaskList().add(index);
            vm1.getTaskList().sort((a, b) -> Double.compare(ss.getTasks()[b].getCmswcRank(), ss.getTasks()[a].getCmswcRank()));
            ss.update();
            return ss;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }
}
