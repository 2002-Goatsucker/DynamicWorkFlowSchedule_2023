package com.cloud.algorithm.standard;

import com.cloud.utils.IOUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @Description:
 * 1. 这是一个Change接口，它代表着动态环境中的动态因素，所有动态因素都会被建模一个单独的类，这些类被规定必须实现该接口。
 * 2. 这个接口的实例需要被维护在Algorithm的实现类中，除非Algorithm并不需要动态属性
 * 3. 类只负责对动态环境进行改变，不包括对动态环境变化后产生的影响进行修复，修复部分需要继承Repair接口实现，当然，如果你真的期望在这里进行修复，也可以实现。
 *
 */
public interface Change {
    /**
     * @param algorithm: 将需要被变化的算法放入其中
     */
    void change(Algorithm algorithm);
}
