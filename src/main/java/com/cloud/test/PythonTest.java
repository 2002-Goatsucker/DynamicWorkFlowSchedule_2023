package com.cloud.test;

import com.cloud.utils.PythonUtils;

import java.util.List;

public class PythonTest {
    public static void main(String[] args) {
        List<String> result = PythonUtils.execute("test.py");
        System.out.println(result);
    }
}
