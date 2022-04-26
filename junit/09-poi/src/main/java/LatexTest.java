import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.render.compute.SpELRenderDataCompute;

import org.ddr.poi.html.HtmlRenderPolicy;
import org.ddr.poi.latex.LaTeXRenderPolicy;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;

/**
 * @date 2022/4/26 13:50
 */
public class LatexTest {

    @SneakyThrows
    public static void main(String[] args) {

        ArrayList<String> lines = new ArrayList<>();
//        FileUtil.readLines(LatexTest.class.getResource("latex.txt"), "utf-8", lines);
        lines.add("ABC_{abc_{123}}");
        List<HashMap<Object, Object>> list = lines.stream()
                                                  .map(s -> String.format("$$ %s $$", s))
                                                  .map(latex -> {
                                                      HashMap<Object, Object> map = new HashMap<>();
                                                      map.put("row", latex);
                                                      map.put("abc_i", latex);
                                                      map.put("abc", latexTextRm(latex)); // fixme:
                                                      String htmlFromLatexMath = getHtmlFromLatexMath(latex);
                                                      System.out.println(htmlFromLatexMath);
                                                      map.put("abc_html", htmlFromLatexMath);
                                                      return map;
                                                  }).collect(Collectors.toList());
        HashMap<Object, Object> param = new HashMap<>();
        param.put("list", list);

        InputStream templateInputStream = LatexTest.class.getResourceAsStream("latex.docx");
        Configure config = Configure.builder()
                                    // 启用spring el
                                    .useSpringEL()
                                    // 设置spring el的数据计算器.并且设置非严格模式.isStrict=false
                                    .setRenderDataComputeFactory(model -> new SpELRenderDataCompute(model, false, Collections.emptyMap()))
                                    .bind("list", new LoopRowTableRenderPolicy())
                                    .bind("abc_i", new LaTeXRenderPolicy())
                                    .bind("abc", new LaTeXRenderPolicy())
                                    .bind("abc_html", new HtmlRenderPolicy())
                                    .build();

        XWPFTemplate template = XWPFTemplate.compile(templateInputStream, config).render(param);
        // 导出的文件保存到临时文件夹中.
        String tmpFileName = "D://" + System.currentTimeMillis() + ".docx";
        template.writeAndClose(new FileOutputStream(tmpFileName));
        System.out.println("生成的文件在:" + tmpFileName);
    }

    /**
     * 把公式转成正体格式
     *
     * @param latex latex公式
     */
    public static String latexTextRm(String latex) {
        if (StrUtil.isEmpty(latex)) {
            return "";
        }
        latex = latex.replaceAll("[$]", "").trim();
        latex = ReUtil.replaceAll(latex, "(.*?)_\\{(.*?)\\}", "$1\\textrm_{$2}");
        latex = ReUtil.replaceAll(latex, "(.*?)\\^\\{(.*?)\\}", "$1\\textrm^{$2}");

        // 部分公式,没有{}大括号      比如:   CO_2
        latex = ReUtil.replaceAll(latex, "(.*?)_(.)", "\\textrm{$1}_\\textrm{$2}");
        latex = ReUtil.replaceAll(latex, "(.*?)\\^(.)", "\\textrm{$1}^\\textrm{$2}");

        if (!latex.endsWith("}")) {
            latex = ReUtil.replaceAll(latex, "(.*)\\}(.*)", "$1\\textrm{$2}");
        }
        return String.format("$$ %s $$", latex);
    }

    /**
     * latex 公式转html代码
     *
     * @param latex 输入:   "A_{f}"
     * @return 输出:  "A<sub>f</sub>"
     */
    public static String getHtmlFromLatexMath(String latex) {
        if (StrUtil.isEmpty(latex)) {
            return "";
        }
        // 把公式中的 $ 和 \  两个符号去掉.
        latex = latex.replaceAll("[$\\\\]", "").trim();
        while (latex.contains("_") || latex.contains("^")) {
            latex = ReUtil.replaceAll(latex, "(.*?)_\\{(.*?)\\}", "$1<sub>$2</sub>");
            latex = ReUtil.replaceAll(latex, "(.*?)\\^\\{(.*?)\\}", "$1<sup>$2</sup>");

            // 部分公式,没有{}大括号      比如:   CO_2
            latex = ReUtil.replaceAll(latex, "(.*?)_(.)", "$1<sub>$2</sub>");
            latex = ReUtil.replaceAll(latex, "(.*?)\\^(.)", "$1<sup>$2</sup>");
        }
        // ABC_{abc_{123}}  ->    ABC<sub>abc<sub>{</sub>123</sub>}
        latex = ReUtil.replaceAll(latex, "(.*?)<sub>(.*?)<sub>\\{</sub>(.*?)</sub>\\}", "$1<sub>$2<sub>$3</sub></sub>");
        return latex;
    }
}
