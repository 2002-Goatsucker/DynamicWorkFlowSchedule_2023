package com.cloud;

import com.cloud.algorithm.*;
import com.cloud.algorithm.standard.Algorithm;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Type;
import com.cloud.thread.AlgorithmThreadPool;
import com.cloud.utils.ChromosomeUtils;
import com.cloud.utils.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
public class Application {
    public static void main(String[] args) {

        //初始化Type
        Type[] types = new Type[8];
        types[0] = new Type(0, 1.7 / 8, 39321600, 0.06);
        types[1] = new Type(1, 3.75 / 8, 85196800, 0.12);
        types[2] = new Type(2, 3.75 / 8, 85196800, 0.113);
        types[3] = new Type(3, 7.5 / 8, 85196800, 0.24);
        types[4] = new Type(4, 7.5 / 8, 85196800, 0.225);
        types[5] = new Type(5, 15 / 8.0, 131072000, 0.48);
        types[6] = new Type(6, 15 / 8.0, 131072000, 0.45);
        types[7] = new Type(7, 30 / 8.0, 131072000, 0.9);
        ReadOnlyData.types = types;

        //TODO: 如果想自定义路径，提供一个空文件夹即可
        String path1 = "src/main/resources/result/dnsgaiib.txt";
        String path2 = "src/main/resources/result/mb.txt";
        String path3 = "src/main/resources/result/idg.txt";
        String path4 = "src/main/resources/result/dmga.txt";
        String path5 = "src/main/resources/result/fogmp.txt";
        String path6 = "src/main/resources/result/cmswc.txt";
        File file1 = new File(path1);
        File file2 = new File(path2);
        File file3 = new File(path3);
        File file4 = new File(path4);
        File file5 = new File(path5);
        File file6 = new File(path6);
        IOUtils.clearFile(file1);
        IOUtils.clearFile(file2);
        IOUtils.clearFile(file3);
        IOUtils.clearFile(file4);
        IOUtils.clearFile(file5);
        IOUtils.clearFile(file6);

        try {
            if(!file1.exists()) file1.createNewFile();
            if(!file2.exists()) file2.createNewFile();
            if(!file3.exists()) file3.createNewFile();
            if(!file4.exists()) file4.createNewFile();
            if(!file5.exists()) file5.createNewFile();
            if(!file6.exists()) file6.createNewFile();

            BufferedWriter dnsgaiib_w = new BufferedWriter(new FileWriter(file1,true));
            BufferedWriter mb_w = new BufferedWriter(new FileWriter(file2,true));
            BufferedWriter idg_W = new BufferedWriter(new FileWriter(file3,true));
            BufferedWriter dmga_w = new BufferedWriter(new FileWriter(file4,true));
            BufferedWriter fogmp_w = new BufferedWriter(new FileWriter(file5,true));
            BufferedWriter cmswc_w = new BufferedWriter(new FileWriter(file6,true));
            //创建算法步骤
//        DNSGAII dnsgaii = new DNSGAII("0");
//        dnsgaii.random = new Random(1);
//        initIns(dnsgaii.accessibleIns);
            List<Algorithm> list = new ArrayList<>();
            for (int i = 0; i < 20; ++i) {

                DNSGAIIB dnsgaiib = new DNSGAIIB("dnsgaiib" + i);
                initIns(dnsgaiib.accessibleIns);
                dnsgaiib.random = new Random(i);

                DNSGAIIgIDG idg = new DNSGAIIgIDG("idg" + i, 0.5, 0.5);
                initIns(idg.accessibleIns);
                idg.random = new Random(i);

                FOGMP fogmp = new FOGMP("fogmp" + i, new Random(i));
                initIns(fogmp.accessibleIns);

                MBNSGAII mbnsgaii = new MBNSGAII("mb" + i);
                initIns(mbnsgaii.accessibleIns);
                mbnsgaii.random = new Random(i);

                DMGA dmga = new DMGA("dmga" + i);
                initIns(dmga.accessibleIns);
                dmga.random = new Random(i);

                CMSWC cmswc = new CMSWC("cmswc" + i);
                initIns(cmswc.accessibleIns);
                cmswc.random = new Random(i);

                AlgorithmThreadPool.submit(dnsgaiib);
                AlgorithmThreadPool.submit(fogmp);
                AlgorithmThreadPool.submit(idg);
                AlgorithmThreadPool.submit(mbnsgaii);
                AlgorithmThreadPool.submit(dmga);
                AlgorithmThreadPool.submit(cmswc);

                List<double[]> front1 = (List<double[]>) AlgorithmThreadPool.getResult("fogmp" + i).map.get("front");
                List<double[]> front2 = (List<double[]>) AlgorithmThreadPool.getResult("dnsgaiib" + i).map.get("front");
                List<double[]> front3 = (List<double[]>) AlgorithmThreadPool.getResult("idg" + i).map.get("front");
                List<double[]> front4 = (List<double[]>) AlgorithmThreadPool.getResult("mb" + i).map.get("front");
                List<double[]> front5 = (List<double[]>) AlgorithmThreadPool.getResult("dmga" + i).map.get("front");
                List<double[]> front6 = (List<double[]>) AlgorithmThreadPool.getResult("cmswc" + i).map.get("front");

                double maxMakeSpan = 0;
                double maxCost = 0;
                double minMakeSpan = Double.MAX_VALUE;
                double minCost = Double.MAX_VALUE;

                for (double[] c : front1) {
                    maxMakeSpan = Math.max(maxMakeSpan, c[0]);
                    minMakeSpan = Math.min(minMakeSpan, c[0]);
                    maxCost = Math.max(maxCost, c[1]);
                    minCost = Math.min(minCost, c[1]);
                }
                for (double[] c : front2) {
                    maxMakeSpan = Math.max(maxMakeSpan, c[0]);
                    minMakeSpan = Math.min(minMakeSpan, c[0]);
                    maxCost = Math.max(maxCost, c[1]);
                    minCost = Math.min(minCost, c[1]);
                }
                for (double[] c : front3) {
                    maxMakeSpan = Math.max(maxMakeSpan, c[0]);
                    minMakeSpan = Math.min(minMakeSpan, c[0]);
                    maxCost = Math.max(maxCost, c[1]);
                    minCost = Math.min(minCost, c[1]);
                }
                for (double[] c : front4) {
                    maxMakeSpan = Math.max(maxMakeSpan, c[0]);
                    minMakeSpan = Math.min(minMakeSpan, c[0]);
                    maxCost = Math.max(maxCost, c[1]);
                    minCost = Math.min(minCost, c[1]);
                }
                for (double[] c : front5) {
                    maxMakeSpan = Math.max(maxMakeSpan, c[0]);
                    minMakeSpan = Math.min(minMakeSpan, c[0]);
                    maxCost = Math.max(maxCost, c[1]);
                    minCost = Math.min(minCost, c[1]);
                }
                for (double[] c : front6) {
                    maxMakeSpan = Math.max(maxMakeSpan, c[0]);
                    minMakeSpan = Math.min(minMakeSpan, c[0]);
                    maxCost = Math.max(maxCost, c[1]);
                    minCost = Math.min(minCost, c[1]);
                }

                double hv1 = ChromosomeUtils.getHV(front1, maxMakeSpan, minMakeSpan, maxCost, minCost);
                double hv2 = ChromosomeUtils.getHV(front2, maxMakeSpan, minMakeSpan, maxCost, minCost);
                double hv3 = ChromosomeUtils.getHV(front3, maxMakeSpan, minMakeSpan, maxCost, minCost);
                double hv4 = ChromosomeUtils.getHV(front4, maxMakeSpan, minMakeSpan, maxCost, minCost);
                double hv5 = ChromosomeUtils.getHV(front5, maxMakeSpan, minMakeSpan, maxCost, minCost);
                double hv6 = ChromosomeUtils.getHV(front6, maxMakeSpan, minMakeSpan, maxCost, minCost);
                fogmp_w.write(hv1+"\n");
                dnsgaiib_w.write(hv2+"\n");
                idg_W.write(hv3+"\n");
                mb_w.write(hv4+"\n");
                dmga_w.write(hv5+"\n");
                cmswc_w.write(hv6+"\n");
            }
            fogmp_w.close();
            dnsgaiib_w.close();
            idg_W.close();
            mb_w.close();
            dmga_w.close();
            cmswc_w.close();
        }catch (IOException e){
            e.printStackTrace();
        }

//        for (int i = 0; i < 20; ++i) {
////            List<double[]> fronts6 = (List<double[]>) AlgorithmThreadPool.getResult("cmswc"+i).map.get("front");
//
////            IOUtils.writeFrontToFile(fronts1.get(fronts1.size() - 1),"src/main/resources/result/front_fogmp.txt");
////            IOUtils.writeFrontToFile(fronts2.get(fronts2.size() - 1),"src/main/resources/result/front_dnsgaiib.txt");
////            IOUtils.writeFrontToFile(fronts3.get(fronts3.size() - 1),"src/main/resources/result/front_idg.txt");
////            IOUtils.writeFrontToFile(fronts4.get(fronts4.size() - 1),"src/main/resources/result/front_mb.txt");
////            IOUtils.writeFrontToFile(fronts5.get(fronts5.size() - 1),"src/main/resources/result/front_dmga.txt");
////            IOUtils.writeFrontToFile(fronts6,"src/main/resources/result/front_cmswc.txt");
//
//
//            List<Double> hv1 = ChromosomeUtils.getHV(fronts1, maxMakeSpan, minMakeSpan, maxCost, minCost);
//            List<Double> hv2 = ChromosomeUtils.getHV(fronts2, maxMakeSpan, minMakeSpan, maxCost, minCost);
//            List<Double> hv3 = ChromosomeUtils.getHV(fronts3, maxMakeSpan, minMakeSpan, maxCost, minCost);
//            List<Double> hv4 = ChromosomeUtils.getHV(fronts4, maxMakeSpan, minMakeSpan, maxCost, minCost);
//            List<Double> hv5 = ChromosomeUtils.getHV(fronts5, maxMakeSpan, minMakeSpan, maxCost, minCost);
//            List<Double> hv6 = ChromosomeUtils.getHV(fronts6, maxMakeSpan, minMakeSpan, maxCost, minCost);
//
//            for (int j = 0; j < hv1.size(); ++j) {
//                if (i == 0) {
//                    fogmp.add(hv1.get(j));
//                    dnsgaiib.add(hv2.get(j));
//                    idg.add(hv3.get(j));
//                    mb.add(hv4.get(j));
//                    dmga.add(hv5.get(j));
//                } else {
//                    fogmp.set(j, fogmp.get(j) + hv1.get(j));
//                    dnsgaiib.set(j, dnsgaiib.get(j) + hv2.get(j));
//                    idg.set(j, idg.get(j) + hv3.get(j));
//                    mb.set(j, mb.get(j) + hv4.get(j));
//                    dmga.set(j, dmga.get(j) + hv5.get(j));
//                }
//            }
//            if (i == 0) {
//                cmswc.add(hv6.get(0));
//            } else {
//                cmswc.set(0, cmswc.get(0) + hv6.get(0));
//            }
//        }
//        for (int i = 0; i < fogmp.size(); ++i) {
//            fogmp.set(i, fogmp.get(i) / 20);
//            dnsgaiib.set(i, dnsgaiib.get(i) / 20);
//            idg.set(i, idg.get(i) / 20);
//            mb.set(i, mb.get(i) / 20);
//            dmga.set(i, dmga.get(i) / 20);
//        }
//        cmswc.set(0, cmswc.get(0) / 20);
//
//        IOUtils.writeHVToFile(fogmp, "src/main/resources/result/hv_fogmp.txt");
//        IOUtils.writeHVToFile(dnsgaiib, "src/main/resources/result/hv_nsgaiib.txt");
//        IOUtils.writeHVToFile(idg, "src/main/resources/result/hv_idg.txt");
//        IOUtils.writeHVToFile(mb, "src/main/resources/result/hv_mb.txt");
//        IOUtils.writeHVToFile(dmga, "src/main/resources/result/hv_dmga.txt");
//        IOUtils.writeHVToFile(cmswc, "src/main/resources/result/hv_cmswc.txt");
//        List<Double> res = new ArrayList<>();
//        res.add(dnsgaiib.get(dnsgaiib.size() - 1));
//        res.add(idg.get(idg.size() - 1));
//        res.add(mb.get(mb.size() - 1));
//        res.add(dmga.get(dmga.size() - 1));
//        res.add(cmswc.get(cmswc.size() - 1));
//        res.add(fogmp.get(fogmp.size() - 1));
//        IOUtils.writeHVToFile(res, "src/main/resources/result/hv_conclude.txt");


//        IOUtils.writeFrontToFile(front1,"C:\\Users\\徐璟源\\Desktop\\git repository\\DynamicScheduling_2023\\src\\main\\java\\com\\cloud\\python\\front1.txt");
//        IOUtils.writeFrontToFile(front2,"C:\\Users\\徐璟源\\Desktop\\git repository\\DynamicScheduling_2023\\src\\main\\java\\com\\cloud\\python\\front2.txt");


//        List<Double> common = new ArrayList<>();
//        List<Double> common_mutate = new ArrayList<>();
//
//        for(int i=0;i<5;++i) {
//            List<Double> all1 = (List<Double>) AlgorithmThreadPool.getResult("fogmp"+i).map.get("hv");
//            List<Double> all2 = (List<Double>) AlgorithmThreadPool.getResult("dnsgaiib"+i).map.get("hv");
//            if(common.isEmpty()) common.addAll(all1);
//            else {
//                for(int j=0;j<common.size();++j){
//                    common.set(j,common.get(j)+all1.get(j));
//                }
//            }
//            if(common_mutate.isEmpty()) common_mutate.addAll(all2);
//            else {
//                for(int j=0;j<common_mutate.size();++j){
//                    common_mutate.set(j,common_mutate.get(j)+all2.get(j));
//                }
//            }
//        }
//
//        common.replaceAll(aDouble -> aDouble / 5);
//        common_mutate.replaceAll(aDouble -> aDouble / 5);
//
//
//        IOUtils.writeHVToFile(common,"src/main/resources/result/result_fogmp.txt");
//        IOUtils.writeHVToFile(common_mutate,"src/main/resources/result/result_nsgaiib.txt");

//        List<Double> mean_hv = new ArrayList<>();
//        for(int i=0;i<30;++i) {
//            //获取结果
//            List<List<Chromosome>> all = (List<List<Chromosome>>) AlgorithmThreadPool.getResult(i+"").map.get("HV");
//            List<Double> hv = ChromosomeUtils.getHV(all);
//            if(mean_hv.isEmpty()){
//                for(Double num:hv){
//                    mean_hv.add(num);
//                }
//            }else {
//                for(int j=0;j<hv.size();++j){
//                    mean_hv.set(j, mean_hv.get(j) + hv.get(j));
//                }
//            }
//        }
//
//        String[] info = new String[mean_hv.size()];
//        StringBuilder cmd = new StringBuilder("DrawHV.py");
//        for(int i=0;i<mean_hv.size();++i){
//            info[i] = "" + mean_hv.get(i)/30;
//            cmd.append(" ").append(mean_hv.get(i) / 30);
//        }
//        System.out.println(cmd);
//
//        PythonUtils.execute("DrawHV.py", info);

//        IOUtils.writeHVToFile(mean_hv,"src\\main\\resources\\results\\hv.txt");
        AlgorithmThreadPool.shutdown();
    }


    public static void initIns(List<Integer> accessibleIns) {
        if (ReadOnlyData.insToType.isEmpty()) {
            for (int i = 0; i < 8; ++i) {
                String conf = "ins.quantity.type" + i;
                int quantity = IOUtils.readIntProperties("dnsgaii-random", conf);
                for (int j = 0; j < quantity; ++j) {
                    ReadOnlyData.insToType.add(i);
                }
            }
        }
        for (int ins = 0; ins < ReadOnlyData.insToType.size(); ++ins) {
            accessibleIns.add(ins);
        }
        ReadOnlyData.insNum = accessibleIns.size();
    }
}
