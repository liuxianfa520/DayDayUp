package com.liuxianfa.junit.springboot;

import java.io.File;
import java.nio.charset.StandardCharsets;

import cn.hutool.core.io.FileUtil;

/**
 * 读取指定目录下所有文本文件,合并成一个文件
 */
public class ReadTextToOneFile {
    private static String srcDir = "D:\\WorkSpace\\YiXin\\fund_institution\\src\\main\\java\\com\\yirendai\\fund";

    private static String targetText = "d://a.txt";

    public static void main(String[] args) {
        deleteOnRun(targetText);

        readFileToTargetText(new File(srcDir), new File(targetText));
        System.out.println("处理完毕");
    }

    public static void readFileToTargetText(File file, File target) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                readFileToTargetText(listFile, target);
            }
        } else {
            if (fileFilter(file)) {
                System.out.println("保存文件:" + file.getAbsolutePath());
                FileUtil.appendLines(FileUtil.readLines(file, StandardCharsets.UTF_8), target, StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * 文件过滤
     *
     * @param file 文件(不是文件夹)
     * @return true:追加.false:不追加
     */
    private static boolean fileFilter(File file) {
        String path = file.getAbsolutePath();
        return path.contains("service") && path.contains("impl");
    }

    private static void deleteOnRun(String targetText) {
        File target = new File(targetText);
        if (target.exists()) {
            target.delete();
        }
    }
}
