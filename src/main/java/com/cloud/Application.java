package com.cloud;

import com.cloud.algorithm.DNSGAII;
import com.cloud.entity.Chromosome;
import com.cloud.entity.ReadOnlyData;
import com.cloud.entity.Type;
import com.cloud.thread.AlgorithmThreadPool;
import com.cloud.utils.IOUtils;
import java.util.List;

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

        //创建算法步骤
        DNSGAII dnsgaii = new DNSGAII("1");
        initIns(dnsgaii.accessibleIns);


        //提交算法
        AlgorithmThreadPool.submit(dnsgaii);



        //获取结果
        List<List<Chromosome>> rank = (List<List<Chromosome>>) AlgorithmThreadPool.getResult("1").map.get("rank");
        IOUtils.writeToFile(rank,"D:\\result.txt");
        System.out.println();
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
