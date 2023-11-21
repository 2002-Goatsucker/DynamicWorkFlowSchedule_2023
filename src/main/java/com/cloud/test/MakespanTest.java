package com.cloud.test;

import com.cloud.algorithm.DNSGAII;
import com.cloud.entity.*;
import com.cloud.utils.ChromosomeUtils;
import com.cloud.utils.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class MakespanTest {
    public static void main(String[] args) {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        Set<Integer> set2 = new HashSet<>(set);
        set2.remove((Integer) 2);
        System.out.println(set);
        System.out.println(set2);
    }
}
//makespan:31.14308
//cost:21.35999
//task2ins: [70, 76, 75, 75, 78, 78, 60, 72, 73, 77, 76, 82, 74, 50, 79, 79, 64, 77, 58, 53, 58, 74, 61, 70, 54, 62, 78, 77, 65, 72, 82, 69, 66, 55, 81, 72, 79, 82, 53, 63, 54, 73, 67, 50, 76, 67, 60, 78, 56, 76, 80, 79, 50, 70, 68, 52, 40, 40, 56, 74, 68, 75, 30, 32, 73, 81, 75, 66, 80, 50, 74, 31, 72, 52, 40, 80, 41, 50, 70, 70, 73, 70, 40, 79, 54, 52, 72, 76, 81, 74, 77, 80, 78, 82, 50, 75, 70, 70, 70, 72]