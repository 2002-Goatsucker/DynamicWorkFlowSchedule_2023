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

    public static void clearFile(String path){
        File file = new File(path);
        if(!file.exists()) return;
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearFile(File file){
        if(!file.exists()) return;
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static double readDoubleProperties(String file, String key){
        ResourceBundle bundle = ResourceBundle.getBundle(file);
        return Double.parseDouble(bundle.getString(key));
    }

    public static int[] readIntArrayProperties(String file, String key, String separator){
        ResourceBundle bundle = ResourceBundle.getBundle(file);
        String[] strs = bundle.getString(key).split(separator);
        int[] array = new int[strs.length];
        for(int i=0;i<array.length;++i){
            array[i] = Integer.parseInt(strs[i]);
        }
        return array;
    }

    public static void writeRankToFile(List<List<Chromosome>> rank, String path){
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


    public static void writeFrontToFile(List<double[]> front, String path){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(path)))
        {

            for(double[] s :front){
                out.write(s[0] + " " + s[1]+"\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeHVToFile(List<Double> hv, String path){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(path)))
        {
            for(Double num:hv){
                out.write(num+"\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
