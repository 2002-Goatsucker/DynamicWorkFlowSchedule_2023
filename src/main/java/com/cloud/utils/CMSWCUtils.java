package com.cloud.utils;
import com.cloud.entity.Task;

import java.util.*;

public class CMSWCUtils {
    /**
     * check topology
     * @param tasks
     * @return whether the tasks are in topology sort
     */
    public static boolean checkTopology(Task[] tasks) {
        Set<Integer> set = new HashSet<>();
        for (Task task : tasks) {
            set.add(task.getIndex());
            for (Integer suc : task.getSuccessor()) {
                if (set.contains(suc)){
                    return false;
                }
            }
        }
        return true;
    }
}
