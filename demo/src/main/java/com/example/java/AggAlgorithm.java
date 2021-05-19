package com.example.java;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @Author xuyj
 * @Description 自定义一个Collector，内部逻辑在抽象方法实现
 **/
public abstract class AggAlgorithm<T, R> implements Collector<List<T>, List<R>, List<R>> {
    /**
     * 创建一个接收结果的可变容器
     */
    @Override
    public Supplier<List<R>> supplier() {
        return ArrayList::new;
    }

    /**
     * 累加器方法，将流中的元素放入可变容器中
     */
    @Override
    public BiConsumer<List<R>, List<T>> accumulator() {
        return (this::aggAlgorithmCal);
    }

    /**
     * 组合结果，当流被拆分成多个部分时，需要将多个结果合并。
     */
    @Override
    public BinaryOperator<List<R>> combiner() {
        return (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        };
    }

    /**
     * 结果转换
     *
     * @return
     */
    @Override
    public Function<List<R>, List<R>> finisher() {
        return (lists -> lists);
    }

    /**
     * 返回一个描述收集器特征的不可变集合
     * CONCURRENT：支持多线程并发操作
     * UNORDERED：不保证集合中元素的顺序
     * IDENTITY_FINISH：表明`完成方法finisher`是一个恒等式，可以被忽略
     */
    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT));
    }

    /**
     * 子类实现分组算法
     *
     * @param result
     * @param source
     */
    public abstract void aggAlgorithmCal(List<R> result, List<T> source);

}
