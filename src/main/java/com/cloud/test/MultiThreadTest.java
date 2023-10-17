package com.cloud.test;

import com.cloud.Application;
import com.cloud.algorithm.DNSGAII;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Type;
import com.cloud.thread.AlgorithmThreadPool;

public class MultiThreadTest {
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

        //创建算法步骤
        long start1 = System.currentTimeMillis();
        DNSGAII dnsgaii_1 = new DNSGAII("1");
        Application.initIns(dnsgaii_1.accessibleIns);
        dnsgaii_1.execute();
        long end1 = System.currentTimeMillis();
        System.out.println("single thread: "+ (double)(end1-start1)/1000);

        long start2 = System.currentTimeMillis();
        DNSGAII dnsgaii_2 = new DNSGAII("2");
        Application.initIns(dnsgaii_2.accessibleIns);
        DNSGAII dnsgaii_3 = new DNSGAII("3");
        Application.initIns(dnsgaii_3.accessibleIns);

        AlgorithmThreadPool.submit(dnsgaii_2);
        AlgorithmThreadPool.submit(dnsgaii_3);
        AlgorithmThreadPool.getResult("2");
        AlgorithmThreadPool.getResult("3");

        long end2 = System.currentTimeMillis();
        System.out.println("multi thread: "+ (double)(end2-start2)/1000);



        System.out.println();


        AlgorithmThreadPool.shutdown();
    }
}
