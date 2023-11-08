package com.cloud.test;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.CMSWCSolution;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Result;
import com.cloud.entity.Type;
import com.cloud.utils.PythonUtils;

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

        List<List<CMSWCSolution>> duplicatedSolution = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            ReadOnlyData.random = new Random(i);
            CMSWC cmswc = new CMSWC(i+"");
            Result result = cmswc.execute();
            duplicatedSolution.add((List<CMSWCSolution>)result.map.get("solutions"));
            try(BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/result/result_cmswc.txt")))
            {
                for(CMSWCSolution s: (List<CMSWCSolution>)result.map.get("solutions")){
                    out.write(s.getMakeSpan() + " " + s.getCost()+"\n");

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
