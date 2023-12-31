package com.cloud.test;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.Chromosome;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Result;
import com.cloud.entity.Type;
import com.cloud.utils.IOUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unchecked")
public class CMSWCTest {
    public static void main(String[] args) {
        //初始化Type
        Type[] types = new Type[8];
        types[0] = new Type(0, 1.7/8, 85196800, 0.06);
        types[1] = new Type(1, 3.75/8, 85196800, 0.12);
        types[2] = new Type(2, 3.75/8, 85196800, 0.113);
        types[3] = new Type(3, 7.5/8, 85196800, 0.24);
        types[4] = new Type(4, 7.5/8, 85196800, 0.225);
        types[5] = new Type(5, 15/8.0, 85196800, 0.48);
        types[6] = new Type(6, 15/8.0, 85196800, 0.45);
        types[7] = new Type(7, 30/8.0, 85196800, 0.9);
        ReadOnlyData.types = types;

        List<List<Chromosome>> duplicatedSolution = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            CMSWC cmswc = new CMSWC(i+"");
            initIns(cmswc.accessibleIns);
            cmswc.random = new Random(i);
            Result result = cmswc.execute();
            duplicatedSolution.add((List<Chromosome>)result.map.get("front"));
            try(BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/result/front_cmswc.txt")))
            {
                for(double[] s: (List<double[]>)result.map.get("front")){
                    out.write(s[0] + " " + s[1]+"\n");

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
}
