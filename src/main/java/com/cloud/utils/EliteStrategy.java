package com.cloud.utils;
import com.cloud.algorithm.CMSWC;
import com.cloud.entity.Chromosome;

public interface EliteStrategy {
    Chromosome applyStrategy(Chromosome solution, int i, CMSWC algorithm);
}
