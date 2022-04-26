import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.render.compute.SpELRenderDataCompute;

import org.ddr.poi.html.HtmlRenderPolicy;
import org.ddr.poi.latex.LaTeXRenderPolicy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;

/**
 * @date 2022/4/19 10:15
 */
public class WordTest {

    public static void main(String[] args) throws Exception {
        base();
    }


    private static void base() throws IOException {
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("comName", "百度");
        param.put("address", "北京-软件园");
        param.put("mobile", "010-651xxx151");
        param.put("date", new DateTime());
        param.put("logo", "https://www.baidu.com/img/flexible/logo/pc/result.png");

        Map<Object, Object> biz = MapUtil.builder().put("name", "软件开发").put("no", "software development").put("desc", "各种软件都能开发,加钱就能解决").put("remark", "-").build();
        param.put("biz", Arrays.asList(biz, biz));

        Map<Object, Object> prd1 = MapUtil.builder().put("name", "二氧化碳").put("fh_latex_default", "$$ {CO}_{2} $$").put("fuhao_latex", "$$ \\textrm{CO}_\\textrm{2} $$").put("fuhao", "CO<sub>2</sub>").put("danwei", "吨").put("value", "20").build();
        Map<Object, Object> prd2 = MapUtil.builder().put("name", "四氧化三铁").put("fh_latex_default", "$$ Fe_3O_4 $$").put("fuhao_latex", "$$ \\textrm{Fe}_\\textrm{3}\\textrm{O}_\\textrm{4} $$").put("fuhao", "Fe<sub>3</sub>O<sub>4</sub>").put("danwei", "吨").put("value", "15.23").build();
        param.put("prd", Arrays.asList(prd1, prd2));


        InputStream templateInputStream = WordTest.class.getResourceAsStream("template.docx");
        Configure config = Configure.builder()
                                    // 启用spring el
                                    .useSpringEL()
                                    // 设置spring el的数据计算器.并且设置非严格模式.isStrict=false
                                    .setRenderDataComputeFactory(model -> new SpELRenderDataCompute(model, false, Collections.emptyMap()))
                                    .bind("fuhao", new HtmlRenderPolicy())
                                    // latex 使用 \textrm 取消斜体
                                    .bind("fuhao_latex", new LaTeXRenderPolicy())
                                    // latex公式默认是斜体
                                    .bind("fh_latex_default", new LaTeXRenderPolicy())
                                    .bind("biz", new LoopRowTableRenderPolicy())
                                    .bind("prd", new LoopRowTableRenderPolicy())
                                    .build();

        XWPFTemplate template = XWPFTemplate.compile(templateInputStream, config).render(param);
        // 导出的文件保存到临时文件夹中.
        String tmpFileName = "D://" + System.currentTimeMillis() + ".docx";
        template.writeAndClose(new FileOutputStream(tmpFileName));
        System.out.println("生成的文件在:" + tmpFileName);
    }
}
