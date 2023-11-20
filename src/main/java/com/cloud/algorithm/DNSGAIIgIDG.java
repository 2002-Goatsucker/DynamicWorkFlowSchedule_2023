package com.cloud.algorithm;

import com.cloud.entity.Chromosome;
import com.cloud.entity.Result;
import com.cloud.utils.ChromosomeUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DNSGAIIgIDG extends DNSGAII {
    /**
     * Description:
     *    This is the [generalized Immigrants-based Diversity Generator] version of NSGAII, which is an instance of [Diversity Maintenance] Strategy
     *    to solve DMOP(Dynamic Multi-objective Optimization Problems)
     *
     * @param name
     * @cite [C. R. B. Azevedo and A. F. R. Araújo, "Generalized immigration schemes for dynamic evolutionary multiobjective optimization,"
     *      2011 IEEE Congress of Evolutionary Computation (CEC), New Orleans, LA, USA, 2011, pp. 2033-2040, doi: 10.1109/CEC.2011.5949865.]
     */
    public double a; // Immigration Rate
    public int K; // Number of Immigrants
    public int R; // Number of Random Immigrants
    public double b; // Proportion of the uncorrelated immigrants
    public DNSGAIIgIDG(String name) {
        super(name);
    }
    public DNSGAIIgIDG(String name, double a, double b){
        super(name);
        this.a = a;
        this.b = b;
    }

    @Override
    public void init() {
        super.init();
        this.size = 40;
//        this.K = (int)(this.size * this.a);
        this.K = 10;
        this.R = (int)(this.K * this.b);
//        this.R = 5;
    }

    @Override
    public Result execute() {
        Result r = super.execute();
        System.out.println("------IDG FINISH------");
        return r;
    }

    @Override
    public void iterate() {
        // 产生子代
        doProduce();
        doSort();
        // 在进行产生子代和排序的操作过后，两代的所有解都存在rank链表里，
        /*
        The incorporation of IDG into NSGA2 is accomplished by
        replacing the K worst solutions with the generated immigrants
        after the application of the variation operators and
        before the survival selection step,
        so that the immigrants do not interfere in the offspring generation process,
        but compete with the newly generated solutions for survival.
         */
        doImmigrate();

        doEliminate();
        son.clear();

        // 复原操作
        for (Chromosome chromosome : fa) {
            ChromosomeUtils.refresh(chromosome, tasks);
            chromosome.setBetterNum(0);
            chromosome.setPoorNum(0);
            chromosome.getBetter().clear();
            chromosome.getPoor().clear();
        }

        // 将本代帕累托前沿上的解集加入‘all’
        List<Chromosome> list = new ArrayList<>();
        for(int k=0;k<rank.get(0).size();++k){
            try {
                list.add(rank.get(0).get(k).clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        all.add(list);
    }
    public void doImmigrate(){
        int C = this.K - this.R; // Number of Correlated Immigrants
        // Generation of correlated immigrants
        List<Chromosome> correlatedImmigrants = new LinkedList<>();

        l:for(List<Chromosome> r : this.rank){
            for(Chromosome c : r){
                correlatedImmigrants.add(c);
                if(correlatedImmigrants.size() == C){
                    break l;
                }
            }
        }
        List<Chromosome> mutatedImmigrants = new LinkedList<>();
        for(Chromosome c : correlatedImmigrants){
            Chromosome mutatedImmigrant = ChromosomeUtils.mutate(c, this.mutationRate, this.tasks, this.random, this.accessibleIns);
            ChromosomeUtils.refresh(mutatedImmigrant, this.tasks);
            mutatedImmigrants.add(mutatedImmigrant);
        }
        // Generation of uncorrelated immigrants
        List<Chromosome> uncorrelatedImmigrants = new LinkedList<>();
        for (int i = 0; i < this.R; i++) {
            uncorrelatedImmigrants.add(ChromosomeUtils.getInitialChromosome(this.graph, this.accessibleIns, this.random));
            ChromosomeUtils.refresh(uncorrelatedImmigrants.get(i), this.tasks);
        }
        List<Chromosome> immigrants = new LinkedList<>();
        immigrants.addAll(mutatedImmigrants);
        immigrants.addAll(uncorrelatedImmigrants);

        // Replace-the-worst one strategy
        son = new LinkedList<>();
        fa = new LinkedList<>();
        for(List<Chromosome> r : this.rank){
            for(Chromosome c : r){
                son.add(c);
            }
        }
        for (int i = 0; i < this.K; i++) {
            son.set(son.size()-K+i, immigrants.get(i));
        }

        doSort();

    }
}
