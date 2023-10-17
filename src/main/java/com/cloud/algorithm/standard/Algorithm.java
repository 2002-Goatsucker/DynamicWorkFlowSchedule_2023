package com.cloud.algorithm.standard;

import com.cloud.entity.Result;

public abstract class Algorithm {
    public String name;
    public Algorithm(String name){
        this.name = name;
    }
    public abstract Result execute();
}
