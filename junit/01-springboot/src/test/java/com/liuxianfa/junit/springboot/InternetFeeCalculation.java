package com.liuxianfa.junit.springboot;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author LiuXianfa
 * 
 * @date 11/20 15:52
 */
public class InternetFeeCalculation {

    private static String beginTime = "2021-11-20T15:00:00";

    private static String endTime = "2021-11-21T16:03:00";

    private static String baoYeBegin = "23:00";
    private static String baoYeEnd = "06:00";

    public static void main(String[] args) throws Exception {
        ShangJiTime shangJiTime = new ShangJiTime(LocalDateTime.parse(beginTime), LocalDateTime.parse(endTime),
                                                  LocalTime.parse(baoYeBegin), LocalTime.parse(baoYeEnd),
                                                  1000, 2500, null);
        LocalTime 上机时分秒 = shangJiTime.getBegin().toLocalTime();
        LocalTime 下机时分秒 = shangJiTime.getEnd().toLocalTime();
        LocalDate 上机日期 = shangJiTime.getBegin().toLocalDate();
        LocalDate 下机日期 = shangJiTime.getEnd().toLocalDate();
        long b和e相差天数 = DateUtil.betweenDay(new DateTime(shangJiTime.getBegin()), new DateTime(shangJiTime.getEnd()), false);


        ArrayList<ShangJiTime> list = new ArrayList<>();
        for (long i = 0; i <= b和e相差天数; i++) {
            LocalDateTime 当天上机时间 = 上机日期.plusDays(i).atTime(上机时分秒);
            LocalDateTime 当天下机时间 = 下机日期.plusDays(i).atTime(下机时分秒);
            LocalDateTime 当天包夜开始时间 = 当天上机时间.toLocalDate().atTime(shangJiTime.getBaoYeBegin());
            LocalDateTime 当天包夜结束时间 = 当天上机时间.toLocalDate().atTime(shangJiTime.getBaoYeEnd());
            LocalDateTime 前一天包夜开始时间 = 当天上机时间.plusDays(-1).toLocalDate().atTime(shangJiTime.getBaoYeBegin());
            LocalDateTime 当天最后时间 = DateUtil.endOfDay(new DateTime(当天上机时间)).toTimestamp().toLocalDateTime();


            if (当天上机时间.isBefore(当天下机时间)) {
                if (isIn(当天上机时间, 前一天包夜开始时间, 当天包夜结束时间)) {
                    if (isIn(当天下机时间, 前一天包夜开始时间, 当天包夜结束时间)) {
                        list.add(new ShangJiTime(前一天包夜开始时间, 当天下机时间, ShangJiType.baoYe));
                    } else if (isIn(当天下机时间, 当天包夜结束时间, 当天包夜开始时间)) {
                        list.add(new ShangJiTime(当天上机时间, 当天包夜结束时间, ShangJiType.baoYe));
                        list.add(new ShangJiTime(当天包夜结束时间, 当天下机时间, ShangJiType.normal));
                    }
                } else if (isIn(当天上机时间, 当天包夜结束时间, 当天包夜开始时间)) {
                    if (当天下机时间.isBefore(当天包夜开始时间)) {
                        list.add(new ShangJiTime(当天上机时间, 当天下机时间, ShangJiType.normal));
                    } else if (isIn(当天下机时间, 当天包夜开始时间, 当天最后时间)) {
                        list.add(new ShangJiTime(当天上机时间, 当天包夜开始时间, ShangJiType.normal));
                        list.add(new ShangJiTime(当天包夜开始时间, 当天下机时间, ShangJiType.baoYe));
                    }
                }
            } else {

            }

        }
        System.out.println(JSON.toJSONString(list, true));
    }

    private static boolean isIn(LocalDateTime time, LocalDateTime begin, LocalDateTime end) {
        return new DateTime(time).isIn(new DateTime(begin), new DateTime(end));
    }

    private static void test() throws SQLException {
        Entity feeConfig = getAreaConfig(1, 2);
    }


    enum ShangJiType {
        normal,
        baoYe
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    static
    class ShangJiTime {
        LocalDateTime begin;

        LocalDateTime end;

        LocalTime baoYeBegin;

        LocalTime baoYeEnd;

        /**
         * 价格  (单位:分/小时)
         */
        int price;

        /**
         * 包夜价格 (单位:分/小时)
         */
        int baoYePrice;

        ShangJiType shangJiType;

        public ShangJiTime(LocalDateTime begin, LocalDateTime end, ShangJiType shangJiType) {
            this.begin = begin;
            this.end = end;
            this.shangJiType = shangJiType;
        }

        public List<ShangJiTime> calcTimeType() {

            return Collections.emptyList();
        }
    }


    public static boolean isSameDay(DateTime t1, DateTime t2) {
        return DateUtil.isSameDay(t1, t2);
    }

    public static DateTime getTodayBaoyeBeginTime(DateTime today, String baoyeBegin) {
        String[] split = baoyeBegin.split(":");
        today.setHours(Integer.parseInt(split[0]));
        today.setMinutes(Integer.parseInt(split[1]));
        today.setSeconds(0);
        return today;
    }

    public static DateTime getTomorrowBaoyeEndTime(DateTime today, String baoyeEnd) {
        String[] split = baoyeEnd.split(":");
        today.setDate(today.getDay() + 1);
        today.setHours(Integer.parseInt(split[0]));
        today.setMinutes(Integer.parseInt(split[1]));
        today.setSeconds(0);
        return today;
    }

    private static boolean containsBaoYeTime(DateTime begin, DateTime end, Entity feeConfig) {
        DateTime baoyeStart = DateUtil.parse(feeConfig.getStr("start_time"));


        DateTime baoyeEnd = DateUtil.parse(feeConfig.getStr("end_time"));


        return false;
    }

    /**
     * 中间相隔多少小时,不满1小时按1小时算
     *
     * @param begin
     * @param end
     */
    private static int betweenHour(DateTime begin, DateTime end) {
        return new BigDecimal(end.between(begin, DateUnit.MS)).divide(new BigDecimal(DateUnit.HOUR.getMillis()), 0, RoundingMode.UP).intValue();
    }


    private static BigDecimal toMinute(Integer price) {
        return new BigDecimal(price).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
    }

    private static Entity getAreaConfig(Integer tenantId, Integer areaId) throws SQLException {
        List<Entity> entities = Db.use().find(new Entity().setTableName("boss_member_fee_set").set("area_id", areaId).set("tenant_id", tenantId));
        if (CollUtil.isEmpty(entities)) {
            throw new RuntimeException("不存在");
        }
        Entity entity = entities.get(0);
        if (entity.getInt("price") < 100) {
            entity.set("price", 100);
        }
        Integer id = entity.getInt("id");
        Integer price = entity.getInt("price");// 分/小时
        Integer price_fen = entity.getInt("price_fen");
        Integer unit_price = entity.getInt("unit_price");
        Integer night_money = entity.getInt("night_money");
        Double time_price = entity.getDouble("time_price");
        Integer time_night = entity.getInt("time_night");
        Integer low_money = entity.getInt("low_money");
        Integer is_low_money = entity.getInt("is_low_money");
        Integer area_id = entity.getInt("area_id");
        Integer store_id = entity.getInt("store_id");
        Integer status = entity.getInt("status");
        Integer creater = entity.getInt("creater");
        Integer tenant_id = entity.getInt("tenant_id");
        String begin_time = entity.getStr("begin_time");
        String end_time = entity.getStr("end_time");
        return entity;
    }
}
