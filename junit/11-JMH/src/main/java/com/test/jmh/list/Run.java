package com.test.jmh.list;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import cn.hutool.core.io.FileUtil;

public class Run {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("com.test.jmh.list.*Test")
                .result(FileUtil.file(System.currentTimeMillis() + "_result.json").getAbsolutePath())
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();
    }

}
