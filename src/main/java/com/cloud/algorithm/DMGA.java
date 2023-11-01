package com.cloud.algorithm;

import com.cloud.algorithm.standard.Algorithm;
import com.cloud.entity.Chromosome;
import com.cloud.entity.Result;
import com.cloud.utils.IOUtils;

import java.util.List;

public class DMGA extends DNSGAII {

    public List<Chromosome> superior;
    public List<Chromosome> normal;
    public List<Chromosome> inferior;

    public DMGA(String name) {
        super(name);
    }
    @Override
    public Result execute() {
        return null;
    }
}
