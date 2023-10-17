package com.cloud.thread;

import com.cloud.algorithm.standard.Algorithm;
import com.cloud.entity.Result;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

//多线程处理办法
public class AlgorithmThreadPool{

    //name->result
    private static final Map<String, CompletableFuture<Result>> results = new HashMap<>();

    //使用的线程池
    private static final ExecutorService threadPool = new ThreadPoolExecutor(5, 15, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(30));


    //submit一个算法后，线程池会自动处理，将结果放到result中，该方法是阻塞式的
    public static void submit(Algorithm algorithm){
        //异步执行
        CompletableFuture<Result> completableFuture = CompletableFuture.supplyAsync(algorithm::execute, threadPool);
        if(results.containsKey(algorithm.name)){
            throw new RuntimeException("algorithm's name duplicated!");
        }
        results.put(algorithm.name, completableFuture);
    }

    public static Result getResult(String name){
        try {
            return results.get(name).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clear(){
        results.clear();
    }

    //协商式的，并非shutdown后立刻结束
    public static void shutdown(){
        threadPool.shutdown();
    }

}
