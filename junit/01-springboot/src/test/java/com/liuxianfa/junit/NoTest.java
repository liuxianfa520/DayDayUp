import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author xianfaliu2@creditease.cn
 * @date 5/7 11:24
 */
public class NoTest {
    static String no = null;

    public static void main(String[] args) {
//        for (int i = 0; i < 16; i++) {
//            String s = HexUtil.toHex(i);
//            System.out.println(s);
//        }

        for (int i = 1; i <= 22005; i++) {
            String no = getNo();
            System.out.println(no);
        }
    }

    /**
     * 生成编号: 要求: 转成字符传之后,length必须是4;第1位是十六进制的.
     */
    private static String getNo() {
        if (StrUtil.isEmpty(no)) {
            no = "0001";
            return no;
        } else {
            try {
                int i = Integer.parseInt(no);
                ++i;
                if (i == 10000) {
                    // 第一位转成十六进制
                    no = HexUtil.toHex(10) + "000";
                    return no.toUpperCase();
                }
                no = String.format("%04d", i);
                return no;
            } catch (NumberFormatException e) {
                String prefix = no.substring(0, 1);
                String suffix = no.substring(1);
                int i = Integer.parseInt(suffix);
                ++i;
                if (i == 1000) {
                    prefix = HexUtil.toHex(HexUtil.hexToInt(prefix) + 1);
                    no = prefix + "000";
                } else {
                    no = prefix + String.format("%03d", i);
                }
                return no.toUpperCase();
            }
        }
    }
}
