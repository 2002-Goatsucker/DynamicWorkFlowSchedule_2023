package com.cloud.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class PythonUtils {
    public static List<String> execute(String file, String... args){
        Process proc;
        String exe = "python";
        StringBuilder arg = new StringBuilder();
        for(String s:args){
            arg.append(s).append(" ");
        }
        try {
            String cmd = exe + " " + "src\\main\\java\\com\\cloud\\python\\" + file + " " + arg;
            proc = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            List<String> result = new LinkedList<>();
            while ((line = reader.readLine())!=null){
                result.add(line);
            }
            reader.close();
            proc.waitFor();
            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
