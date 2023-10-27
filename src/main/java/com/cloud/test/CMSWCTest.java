package com.cloud.test;

import com.cloud.algorithm.CMSWC;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Type;
import com.cloud.utils.PythonUtils;

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

        //创建算法步骤
        CMSWC cmswc = new CMSWC("2");
        cmswc.execute();


    }
}
