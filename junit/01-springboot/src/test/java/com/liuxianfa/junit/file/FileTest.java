package com.liuxianfa.junit.file;

import java.nio.charset.Charset;

import cn.hutool.core.io.FileUtil;

/**
 * @author xianfaliu2@creditease.cn
 * @date 2022/4/1 16:56
 */
public class FileTest {

    public static void main(String[] args) {
        FileUtil.writeString("这是内容", "d://tmp.txt", Charset.defaultCharset());
    }
}
