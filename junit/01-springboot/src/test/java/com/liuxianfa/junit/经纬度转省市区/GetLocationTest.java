package com.liuxianfa.junit.经纬度转省市区;

import com.alibaba.fastjson.JSONPath;

import cn.hutool.http.HttpUtil;

/**
 * @date 6/26 20:24
 */
public class GetLocationTest {


    public static void main(String[] args) {
//        122.04688699999997,41.129513
        String lng = "122.04688699999997";
        String lat = "41.129513";
        String sheng = getSheng(lat, lng);
        System.out.println(sheng);
    }

    private static String baiduKey = "PZ9rZ3DCSKzrnrO4eBM84d2ExQHKaT4Y";
    private static String url = "http://api.map.baidu.com/geocoder/v2/?location=%s,%s&output=json&ak=" + baiduKey + "&pois=0";

    private static String getSheng(String lng, String lat) {
        String s = HttpUtil.get(String.format(url, lng, lat));
        System.out.println(s);
        Object eval = JSONPath.eval(s, "$.result.addressComponent.province");
        if (eval != null) {
            return eval.toString().replace("省", "");
        }
        return "";
    }
}