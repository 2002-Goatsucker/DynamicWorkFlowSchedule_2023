package com.cloud;

import com.cloud.algorithm.DNSGAII;
import com.cloud.entity.Chromosome;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Type;
import com.cloud.thread.AlgorithmThreadPool;
import com.cloud.utils.ChromosomeUtils;
import com.cloud.utils.IOUtils;
import com.cloud.utils.PythonUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unchecked")
public class Application {
    public static void main(String[] args) {

        //初始化Type
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

        List<DNSGAII> dnsgaiis = new LinkedList<>();
        //创建算法步骤
        for(int i=0;i<30;++i){
            DNSGAII dnsgaii = new DNSGAII(i+"");
            dnsgaii.random = new Random(i);
            initIns(dnsgaii.accessibleIns);
            dnsgaiis.add(dnsgaii);
        }

        //提交算法
        for(DNSGAII dnsgaii:dnsgaiis){
            AlgorithmThreadPool.submit(dnsgaii);
        }



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


    public static void initIns(List<Integer> accessibleIns){
        for(int i=0;i<8;++i){
            String conf = "ins.quantity.type"+i;
            int quantity = IOUtils.readIntProperties("dnsgaii-random",conf);
            for(int j=0;j<quantity;++j){
                ReadOnlyData.insToType.add(i);
            }
        }
        for(int ins=0;ins<ReadOnlyData.insToType.size();++ins){
            accessibleIns.add(ins);
        }
        ReadOnlyData.insNum = accessibleIns.size();
    }
}
