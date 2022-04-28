package com.test.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

//作者：武培轩
//        链接：https://www.zhihu.com/question/276455629/answer/1259967560
//        来源：知乎
//        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

// 测试模式.
//1. Throughput：整体吞吐量，每秒执行了多少次调用，单位为 `ops/time`
//2. AverageTime：用的平均时间，每次操作的平均时间，单位为 `time/op`
//3. SampleTime：随机取样，最后输出取样结果的分布
//4. SingleShotTime：只运行一次，往往同时把 Warmup 次数设为 0，用于测试冷启动时的性能
//5. All：上面的所有模式都执行一次
@BenchmarkMode(Mode.AverageTime)
// 预热 预热3次,每次预热1秒
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
//测量5次，每次测量的持续时间为20秒
@Measurement(iterations = 5, time = 20, timeUnit = TimeUnit.SECONDS)
// 每个进程中的测试线程，可用于类或者方法上。
@Threads(4)
// 进行 fork 的次数，可用于类或者方法上。如果 fork 数是 2 的话，则 JMH 会 fork 出两个进程来进行测试。
@Fork(1)
// 通过 State 可以指定一个对象的作用范围，JMH 根据 scope 来进行实例化和共享操作。
//1. Scope.Benchmark：所有测试线程共享一个实例，测试有状态实例在多线程共享下的性能
//2. Scope.Group：同一个线程在同一个 group 里共享实例
//3. Scope.Thread：默认的 State，每个测试线程分配一个实例
@State(value = Scope.Benchmark)
// 统计结果的时间单位。可用于类或者方法注解
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class StringConnectTest {

    @Param(value = {"100", "500", "1000"})
    private int length;

    @Benchmark
    public void testStringAdd(Blackhole blackhole) {
        String a = "";
        for (int i = 0; i < length; i++) {
            a += i;
        }
        blackhole.consume(a);
    }

    @Benchmark
    public void testStringBuilderAdd(Blackhole blackhole) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(i);
        }
        blackhole.consume(sb.toString());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringConnectTest.class.getSimpleName())
                .result("StringConnectTest_result.json")
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();
    }
}