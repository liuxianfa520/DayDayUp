import com.alibaba.fastjson.JSON;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.pattern.CronPatternUtil;

public class CronTest {

    public static void main(String[] args) {
        extracted();
    }

    private static void extracted() {
        List<String> cronList = FileUtil.readLines("cron.txt", "utf-8")
                                        .stream()
                                        .filter(s -> !s.startsWith("#"))
                                        .map(s -> s.replaceAll("#.*", "").trim())
                                        .collect(Collectors.toList());

        System.out.println(JSON.toJSONString(cronList, true));

        HashSet<Date> hashSet = new HashSet();
        for (String cron : cronList) {
            hashSet.addAll(run(cron));
        }
        hashSet.stream()
               .sorted()
               .map(date -> new DateTime(date).toString(DatePattern.NORM_TIME_PATTERN))
               .forEach(System.out::println);
    }


    /**
     * 给定一些cron表达式,统计出任务都是在每天几点执行.
     *
     * @return
     */
    private static List<Date> run(String cron) {
        DateTime beginOfDay = DateUtil.beginOfDay(new Date());
        DateTime endOfDay = DateUtil.endOfDay(new Date());

        CronPattern pattern = new CronPattern(cron);
        List<Date> dates = CronPatternUtil.matchedDates(pattern, beginOfDay.getTime(), endOfDay.getTime(), 100, false);
        if (CollUtil.isNotEmpty(dates)) {
            return dates;
        }
        return Collections.emptyList();
    }

}
