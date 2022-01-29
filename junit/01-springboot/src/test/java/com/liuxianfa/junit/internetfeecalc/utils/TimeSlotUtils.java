package com.liuxianfa.junit.internetfeecalc.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author LiuXianfa
 * @email LiuXianfa
 * @date 2021/11/24 9:20
 */
public class TimeSlotUtils {

    public static void main(String[] args) {
        TimeSlot a = TimeSlotUtils.buildSlot("2021-01-01 10:00:00", "2021-01-01 12:00:00");
        TimeSlot b = TimeSlotUtils.buildSlot("2021-01-01 08:00:00", "2021-01-01 13:00:00");

        boolean overlapped = TimeSlotUtils.overlapped(a, b);
        System.out.println(overlapped);


        long begin = Math.max(a.getStartTime().toEpochSecond(ZoneOffset.of("+8")), b.getStartTime().toEpochSecond(ZoneOffset.of("+8")));
        long end = Math.min(a.getEndTime().toEpochSecond(ZoneOffset.of("+8")), b.getEndTime().toEpochSecond(ZoneOffset.of("+8")));
        boolean b1 = end - begin > 0;
        System.out.println(b1);
    }


    /**
     * 判断两个时间段是否重叠
     *
     * @param slot1
     * @param slot2
     * @return
     */
    public static boolean overlapped(TimeSlot slot1, TimeSlot slot2) {
        TimeSlot previous, next;
        previous = slot1.startTime.isBefore(slot2.startTime) ? slot1 : slot2;
        next = slot2.startTime.isAfter(slot1.startTime) ? slot2 : slot1;
        // 这里业务需要，允许时间点的重叠
        // 例如某个时间段的起始时间：2020-06-29 00:00:00
        // 和另一个时间段的终止时间：2020-06-29 00:00:00
        // 它们俩可以有交点。如果不需要这种逻辑只把le改成lt
        // ，ge改成gt就可
        return !(lt(previous, next) || gt(previous, next));
    }

    /**
     * 构造一个时间段
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static TimeSlot buildSlot(LocalDateTime startTime, LocalDateTime endTime) {
        return new TimeSlot(startTime, endTime);
    }

    public static TimeSlot buildSlot(Date startTime, Date endTime) {
        return new TimeSlot(DateUtil.toLocalDateTime(startTime), DateUtil.toLocalDateTime(endTime));
    }

    public static TimeSlot buildSlot(String startTime, String endTime) {
        return buildSlot(startTime, endTime, null);
    }

    public static TimeSlot buildSlot(String startTime, String endTime, String format) {
        if (StrUtil.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new TimeSlot(LocalDateTimeUtil.parse(startTime, format), LocalDateTimeUtil.parse(endTime, format));
    }


    /**
     * less equal 小于等于
     *
     * @param prev
     * @param next
     * @return
     */
    private static boolean le(TimeSlot prev, TimeSlot next) {
        return lt(prev, next) || next.endTime.isEqual(prev.startTime);
    }

    /**
     * greater equal 大于等于
     *
     * @param prev
     * @param next
     * @return
     */
    private static boolean ge(TimeSlot prev, TimeSlot next) {
        return gt(prev, next) || prev.endTime.isEqual(next.startTime);
    }

    /**
     * greater than 大于
     *
     * @param prev
     * @param next
     * @return
     */
    private static boolean gt(TimeSlot prev, TimeSlot next) {
        return prev.endTime.isBefore(next.startTime);
    }

    /**
     * less than 小于
     *
     * @param prev
     * @param next
     * @return
     */
    private static boolean lt(TimeSlot prev, TimeSlot next) {
        return next.endTime.isBefore(prev.startTime);
    }


    enum Type {
        Y,
        N
    }

    @Data
    static class TimeSlot {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Type type;

        public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
            if (startTime.isAfter(endTime)) {
                this.startTime = endTime;
                this.endTime = startTime;
            } else {
                this.startTime = startTime;
                this.endTime = endTime;
            }
        }
    }

    private TimeSlotUtils() {
    }
}






