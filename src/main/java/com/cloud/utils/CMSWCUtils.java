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

    public static List<Integer> randomTopologicalSort(List<Integer> tasks, Task[] unsortedTasks) {
        //index, indegree
        Map<Integer, Integer> inDegree = new HashMap<>(); // 存储每个task的入度
        Set<Integer> set = new HashSet<>(tasks);
        // 计算每个task的入度
        for (int task : tasks) {
            inDegree.put(task, 0);
        }
        for (int task : tasks) {
            for (int predecessor : unsortedTasks[task].getPredecessor()) {
                if (set.contains(predecessor))
                    inDegree.put(task, inDegree.get(task) + 1);
            }
        }

        List<Integer> q = new ArrayList<>();
        for (int task : tasks) {
            if (inDegree.get(task) == 0) {
                q.add(task);
            }
        }

        Random rand = new Random(0);

        List<Integer> sortedList = new ArrayList<>();
        while (!q.isEmpty()) {
            // 随机选择一个入度为0的task
            int index = rand.nextInt(q.size());
            int task = q.get(index);
            sortedList.add(task);
            q.remove((Integer) task);

            for (int successor : unsortedTasks[task].getSuccessor()) {
                if (set.contains(successor)) {
                    inDegree.put(successor, inDegree.get(successor) - 1);
                    if (inDegree.get(successor) == 0) {
                        q.add(successor);
                    }
                }
            }
        }

        return sortedList;
    }

    public static Task[] getTaskList(List<Integer> vmTaskList, Task[] unsortedTasks){
        Task[] tasks = new Task[vmTaskList.size()];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = unsortedTasks[vmTaskList.get(i)];
        }
        return tasks;
    }
}
