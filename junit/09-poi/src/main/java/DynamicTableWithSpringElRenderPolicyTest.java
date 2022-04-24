import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.policy.RenderPolicy;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.deepoove.poi.util.TableTools;

import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;

/**
 * @date 2022/4/24 13:55
 */
public class DynamicTableWithSpringElRenderPolicyTest {
    public static void main(String[] args) {
        useSpringEl();
    }

    static class DynamicTableWithSpringElRenderPolicy implements RenderPolicy {
        @Override
        public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
            RunTemplate runTemplate = (RunTemplate) eleTemplate;
            XWPFRun run = runTemplate.getRun();
            run.setText("", 0);
            try {
                if (!TableTools.isInsideTable(run)) {
                    throw new IllegalStateException("The template tag " + runTemplate.getSource() + " must be inside a table");
                }
                LoopRowTableRenderPolicy loopRowTableRenderPolicy = new LoopRowTableRenderPolicy();
                loopRowTableRenderPolicy.render(eleTemplate, data, template);
            } catch (Exception e) {
                throw new RenderException("Dynamic render table error:" + e.getMessage(), e);
            }
        }
    }

    public static ArrayList<Map<Object, Object>> userList = new ArrayList() {{
        for (int i = 1; i <= 20; i++) {
            add(MapUtil.builder().put("userName", "张" + i).put("id", i).build());
        }
    }};

    public static List<Map<Object, Object>> getUserListByIds(String ids) {
        // userList 的数据,一般是从数据库中,查询出来.
        // 使用 SpringUtil.getBean(A.class)  可以获取对应的service
        List<String> idList = StrUtil.split(ids, ",");
        return userList.stream().filter(o -> idList.contains(String.valueOf(MapUtil.getInt(o, "id")))).collect(Collectors.toList());
    }

    @SneakyThrows
    private static void useSpringEl() {
        HashMap<String, Method> spELFunction = new HashMap<>();
        spELFunction.put("getUserListByIds", DynamicTableWithSpringElRenderPolicyTest.class.getMethod("getUserListByIds", String.class));

        Configure config = Configure.builder()
                                    .addPlugin('%', new DynamicTableWithSpringElRenderPolicy())
                                    .useSpringEL(spELFunction)
                                    .build();

        InputStream resourceAsStream = DynamicTableWithSpringElRenderPolicyTest.class.getResourceAsStream("/DynamicTableWithSpringElRenderPolicy.docx");
        XWPFTemplate template = XWPFTemplate.compile(resourceAsStream, config).render(new HashMap<>());
        String name = "d://" + System.currentTimeMillis() + ".docx";
        template.writeAndClose(new FileOutputStream(name));
        System.out.println("生成的docx保存到了:" + name);
    }
}