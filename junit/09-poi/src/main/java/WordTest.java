import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;

import org.ddr.poi.html.HtmlRenderPolicy;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;

/**
 * @date 2022/4/19 10:15
 */
public class WordTest {

    public static void main(String[] args) throws Exception {
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("comName", "百度");
        param.put("address", "北京-软件园");
        param.put("mobile", "010-651xxx151");
        param.put("date", new DateTime());
        param.put("logo", "https://www.baidu.com/img/flexible/logo/pc/result.png");

        Map<Object, Object> biz = MapUtil.builder().put("name", "软件开发").put("no", "software development").put("desc", "各种软件都能开发,加钱就能解决").put("remark", "-").build();
        param.put("biz", Arrays.asList(biz, biz));

        Map<Object, Object> prd1 = MapUtil.builder().put("name", "二氧化碳").put("fuhao", "CO<sub>2</sub>").put("danwei", "吨").put("value", "20").build();
        Map<Object, Object> prd2 = MapUtil.builder().put("name", "四氧化三铁").put("fuhao", "Fe<sub>3</sub>O<sub>4</sub>").put("danwei", "吨").put("value", "15.23").build();
        param.put("prd", Arrays.asList(prd1, prd2));


        InputStream templateInputStream = WordTest.class.getResourceAsStream("template.docx");
        Configure config = Configure.builder()
                                    .useSpringEL()
                                    .bind("fuhao", new HtmlRenderPolicy())
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
