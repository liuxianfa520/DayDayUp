package com.liuxianfa.junit.poi;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.map.MapUtil;

/**
 * @date 6/19 23:19
 */
public class PoiTest {

    public static void main(String[] args) throws IOException {
        String templateFileName = "维修记录表.xls";
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Object> list = new ArrayList<>();
        map.put("myList", list);
        for (int i = 1; i <= 50; i++) {
            list.add(MapUtil.builder()
                            .put("no", "" + i)
                            .put("plan", "计划" + i)
                            .put("buwei", "部位" + i)
                            .put("dianjihao", "dianjihao" + i)
                            .put("dianjihao", "dianjihao" + i)
                            .put("pjmc", "pjmc" + i)
                            .put("xh", "xh" + i)
                            .put("zjsl", "zjsl" + i)
                            .put("ghsl", "ghsl" + i)
                            .put("wxsl", "wxsl" + i)
                            .put("plan", "plan" + i)
                            .put("buwei", "buwei" + i)
                            .put("jidianhao", "jidianhao" + i)
                            .put("pjmc", "pjmc" + i)
                            .put("xh", "xh" + i)
                            .put("zjsl", "zjsl" + i)
                            .put("ghsl", "ghsl" + i)
                            .put("wxsl", "wxsl" + i)
                            .put("ccql", "ccql" + i)
                            .put("dw", "dw" + i)
                            .put("bz", "bz" + i)
                            .put("sfyyh", "sfyyh" + i)
                            .put("sfhj", "sfhj" + i)
                            .put("fzr", "fzr" + i)
                            .put("wxjl", "wxjl" + i)
                            .put("wxzl", "wxzl" + i)
                            .put("shr", "shr" + i)
                            .put("shyj", "shyj" + i)
                            .build()
            );
        }

        TemplateExportParams params = new TemplateExportParams(templateFileName, true);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        FileOutputStream fos = new FileOutputStream("d://aaa.xls");
        workbook.write(fos);
        fos.close();
    }

}
