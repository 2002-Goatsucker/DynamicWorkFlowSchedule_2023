package com.cloud.test;

import com.cloud.algorithm.*;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Result;
import com.cloud.entity.Type;
import com.cloud.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HVTest {
    public static void main(String[] args) {
        Type[] types = new Type[8];
        types[0] = new Type(0, 1.7/8, 39321600, 0.06);
        types[1] = new Type(1, 3.75/8, 85196800, 0.12);
        types[2] = new Type(2, 3.75/8, 85196800, 0.113);
        types[3] = new Type(3, 7.5/8, 85196800, 0.24);
        types[4] = new Type(4, 7.5/8, 85196800, 0.225);
        types[5] = new Type(5, 15/8.0, 131072000, 0.48);
        types[6] = new Type(6, 15/8.0, 131072000, 0.45);
        types[7] = new Type(7, 30/8.0, 131072000, 0.9);
        ReadOnlyData.types = types;
        List<double[]> front1 = new ArrayList<>();
        List<double[]> front2 = new ArrayList<>();
        List<double[]> front3 = new ArrayList<>();
        List<double[]> front4 = new ArrayList<>();
        List<double[]> front5 = new ArrayList<>();
        List<double[]> front6 = new ArrayList<>();
        for(int i=0;i<1;++i){
            CMSWC cmswc = new CMSWC("cmswc" + i);
            initIns(cmswc.accessibleIns);
            cmswc.random = new Random(i);
            front6.addAll((List<double[]>) cmswc.execute().map.get("front"));

            DNSGAIIB dnsgaiib = new DNSGAIIB("dnsgaiib"+i);
            initIns(dnsgaiib.accessibleIns);
            dnsgaiib.random = new Random(i);
            List<List<double[]>> all = (List<List<double[]>>)dnsgaiib.execute().map.get("fronts");
            front1.addAll(all.get(all.size() - 1));

            DNSGAIIgIDG idg = new DNSGAIIgIDG("idg"+i, 0.5, 0.5);
            initIns(idg.accessibleIns);
            idg.random = new Random(i);
            all = (List<List<double[]>>)idg.execute().map.get("fronts");
            front2.addAll(all.get(all.size() - 1));

            FOGMP fogmp = new FOGMP("fogmp" + i, new Random(i));
            initIns(fogmp.accessibleIns);
            all = (List<List<double[]>>)fogmp.execute().map.get("fronts");
            front3.addAll(all.get(all.size() - 1));

            MBNSGAII mbnsgaii = new MBNSGAII("mb" + i);
            initIns(mbnsgaii.accessibleIns);
            mbnsgaii.random = new Random(i);
            all = (List<List<double[]>>)mbnsgaii.execute().map.get("fronts");
            front4.addAll(all.get(all.size() - 1));

            DMGA dmga = new DMGA("dmga" + i);
            initIns(dmga.accessibleIns);
            dmga.random = new Random(i);
            all = (List<List<double[]>>)dmga.execute().map.get("fronts");
            front5.addAll(all.get(all.size() - 1));

        }
        IOUtils.writeFrontToFile(quickNondominatedSort(front1).get(0),"src/main/resources/result/front_dnsgaiib.txt");
        IOUtils.writeFrontToFile(quickNondominatedSort(front2).get(0),"src/main/resources/result/front_idg.txt");
        IOUtils.writeFrontToFile(quickNondominatedSort(front3).get(0),"src/main/resources/result/front_cmswc.txt");
        IOUtils.writeFrontToFile(quickNondominatedSort(front4).get(0),"src/main/resources/result/front_mb.txt");
        IOUtils.writeFrontToFile(quickNondominatedSort(front5).get(0),"src/main/resources/result/front_dmga.txt");
        IOUtils.writeFrontToFile(quickNondominatedSort(front6).get(0),"src/main/resources/result/front_cmswc.txt");


    }
    public static void initIns(List<Integer> accessibleIns){
        if(ReadOnlyData.insToType.isEmpty()) {
            for (int i = 0; i < 8; ++i) {
                String conf = "ins.quantity.type" + i;
                int quantity = IOUtils.readIntProperties("dnsgaii-random", conf);
                for (int j = 0; j < quantity; ++j) {
                    ReadOnlyData.insToType.add(i);
                }
            }
        }
        for(int ins=0;ins<ReadOnlyData.insToType.size();++ins){
            accessibleIns.add(ins);
        }
        ReadOnlyData.insNum = accessibleIns.size();
    }

    public static List<List<double[]>> quickNondominatedSort(List<double[]> fronts) {
        List<List<double[]>> rank = new ArrayList<>();
        fronts.sort((a, b) -> Double.compare(a[0], b[0]));
        int i = 0;
        while(!fronts.isEmpty()){
            rank.add(new ArrayList<>());
            rank.get(i).add(fronts.get(0));
            double cost = fronts.get(0)[1];
            for (int j = 1; j < fronts.size(); j++) {
                if (fronts.get(j)[1] < cost){
                    rank.get(i).add(fronts.get(j));
                    cost = fronts.get(j)[1];
                }
            }
            for (double[] solution : rank.get(i)) {
                fronts.remove(solution);
            }
            i++;
        }
        return rank;
    }
}
