package com.cloud.algorithm.utils.dmga;

import java.util.ArrayList;

public class CrowdingDistanceAlgorithm {
    public ArrayList<Individual> individual_select_by_crowding_distance(ArrayList<Individual> list, int need_num) {
        ArrayList<Individual> ans = new ArrayList<>();
        list.sort((o1, o2) -> {
            if (o1.makespan - o2.makespan > 0.000000001) return 1;
            else if (o1.makespan - o2.makespan < -0.000000001) return -1;
            return 0;
        });
        list.get(0).crowding = Double.MAX_VALUE;
        list.get(list.size() - 1).crowding = Double.MAX_VALUE;
        for (int i = 1; i < list.size() - 1; ++i) {
            list.get(i).crowding = Math.abs(list.get(i + 1).makespan - list.get(i - 1).makespan) * Math.abs(list.get(i + 1).cost - list.get(i - 1).cost);
        }
        list.sort((o1, o2) -> {
            double num = o1.crowding - o2.crowding;
            if (num > 0) return -1;
            if (num < 0) return 1;
            return 0;
        });

        int num=0;
        for (Individual chromosome : list) {
            ans.add(chromosome);
            num++;
            if (num==need_num) break;
        }
        return ans;
    }
}
