package com.cloud.algorithm.utils.dmga;

import java.util.ArrayList;
import java.util.List;

public class ParetoAlgorithm {
    public static ArrayList<Individual> get_pareto_result(List<Individual> list){
        for(int i=0;i<list.size();++i){
            Individual individual_1 = list.get(i);
            for(int j=i+1;j<list.size();++j){
                Individual individual_2 = list.get(j);
                if (individual_1.makespan>individual_2.makespan && individual_1.cost>=individual_2.cost || individual_1.makespan>=individual_2.makespan && individual_1.cost>individual_2.cost){
                    individual_1.better++;
                }
                if (individual_1.makespan<individual_2.makespan && individual_1.cost<=individual_2.cost || individual_1.makespan<=individual_2.makespan && individual_1.cost<individual_2.cost){
                    individual_2.better++;
                }
            }
        }
        ArrayList<Individual> ans = new ArrayList<>();
        for(Individual individual:list){
            if(individual.better==0){
                ans.add(individual);
            }else {
                individual.better=0;
            }
        }
        return ans;
    }
}
