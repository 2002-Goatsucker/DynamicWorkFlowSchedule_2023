package com.cloud.test;

import com.cloud.utils.PythonUtils;

import java.util.List;
import java.util.Random;

public class PythonTest {
    public static void main(String[] args) {
        Random random1 = new Random(1);
        Random random2 = new Random(1);
        for(int i=0;i<10;++i) {
            System.out.println(random1.nextInt());
            System.out.println(random2.nextInt());
        }

    }
}
