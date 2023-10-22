package com.cloud.algorithm.standard;

import com.cloud.algorithm.change.InsAvailChange;
import com.cloud.entity.Chromosome;
import com.cloud.entity.Task;

import java.util.List;
import java.util.Random;

/**
 * @Description:
 * 1. 这是一个Repair接口，他的含义是动态环境发生后，对其进行修复或者调整，所有的修复方案将被建模成一个类，且必须实现该接口。
 * 2. 这个接口的实例需要被维护在Algorithm的实现类中，除非Algorithm是一个静态算法。
 * 3. 并不是所有的动态发生后都需要进行修复，如果不需要修复，请实现一个空实现的方法
 */
public interface Repair {

    void repair(Algorithm algorithm);
}
