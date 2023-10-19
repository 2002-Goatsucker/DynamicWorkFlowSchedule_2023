package com.cloud.utils;

import com.cloud.entity.Chromosome;

import java.io.*;
import java.util.List;
import java.util.ResourceBundle;

public class IOUtils {

    public static int readIntProperties(String file, String key){
        ResourceBundle bundle = ResourceBundle.getBundle(file);
        return Integer.parseInt(bundle.getString(key));
    }

    public static double readDoubleProperties(String file, String key){
        ResourceBundle bundle = ResourceBundle.getBundle(file);
        return Double.parseDouble(bundle.getString(key));
    }

    public static void writeToFile(List<List<Chromosome>> rank, String path){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(path)))
        {
            for(List<Chromosome> list:rank){
                for(Chromosome chromosome:list){
                    out.write(chromosome.getMakeSpan() + " " + chromosome.getCost()+"\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
