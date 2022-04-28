package com.test.jmh.list;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.io.FileUtil;

@BenchmarkMode(Mode.AverageTime)
// 预热 预热3次,每次预热10秒
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
//测量5次，每次测量的持续时间为20秒
@Measurement(iterations = 1, time = 20, timeUnit = TimeUnit.SECONDS)
// 每个进程中的测试线程，可用于类或者方法上。
//@Threads(5)
// 进行 fork 的次数，可用于类或者方法上。如果 fork 数是 2 的话，则 JMH 会 fork 出两个进程来进行测试。
@Fork(1)
// 通过 State 可以指定一个对象的作用范围，JMH 根据 scope 来进行实例化和共享操作。
//1. Scope.Benchmark：所有测试线程共享一个实例，测试有状态实例在多线程共享下的性能
//2. Scope.Group：同一个线程在同一个 group 里共享实例
//3. Scope.Thread：默认的 State，每个测试线程分配一个实例
@State(value = Scope.Benchmark)
// 统计结果的时间单位。可用于类或者方法注解
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class RemoveHeadTest {

    @Param(value = {"500", "1000"})
    private int length;

    ArrayList<Object> arrayList = new ArrayList<Object>();
    LinkedList<Object> linkedList = new LinkedList<Object>();

    @Setup
    public void setup() {
        System.out.println("setup. length:" + length);
        arrayList.clear();
        linkedList.clear();
        for (int i = 0; i < length; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }
    }


    @Benchmark
    public Object 从头删除ArrayList() {
        int size = arrayList.size();
        if (size != 0) {
            System.out.println("从头删除ArrayList. size:" + size);
        }
        for (int i = 0; i < size; i++) {
            arrayList.remove(0);
        }
        return arrayList;
    }

    @Benchmark
    public Object 从头删除LinkedList() {
        int size = linkedList.size();
        if (size != 0) {
            System.out.println("从头删除LinkedList. size:" + size);
        }

        for (int i = 0; i < size; i++) {
            linkedList.remove(0);
        }
        return linkedList;
    }


    public static void main(String[] args) throws RunnerException {
//        RemoveHeadTest a = new RemoveHeadTest();
//        a.setup();
//        a.从头删除ArrayList();
//        a.从头删除LinkedList();


        Options opt = new OptionsBuilder()
                .include(RemoveHeadTest.class.getSimpleName())
                .result(FileUtil.file(RemoveHeadTest.class.getSimpleName() + System.currentTimeMillis() + "_result.json").getAbsolutePath())
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();
    }
}
