package com.liuxianfa.junit.springboot.logger.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import static java.util.stream.Collectors.toList;

/**
 * 查询、修改logger日志级别
 */
@RestController
public class LoggingLevelModifyController {
    private static final Logger logger = LoggerFactory.getLogger(LoggingLevelModifyController.class);

    /**
     * 修改日志等级
     * <p>
     * curl -X GET --location "http://localhost:8080/loggingLevelModify?name=com.liuxianfa.junit.springboot&level=debug"
     *
     * @param name  日志名称,比如:root 或者包名 com.baidu.service  或者全类名
     * @param level 等级(off/trace/debug/info/warn/error/fatal
     */
    @RequestMapping("loggingLevelModify")
    public String loggingLevelModify(String name, String level) {
        logger.warn(">>>正在修改日志等级:name={},level={}", name, level);

        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            if ("all".equalsIgnoreCase(name)) {
                loggerContext.getLoggerList().forEach(log -> log.setLevel(Level.valueOf(level)));
            } else {
                loggerContext.getLogger(name).setLevel(Level.valueOf(level));
            }

        } catch (Exception e) {
            logger.error("动态修改日志级别出错", e);
            throw new RuntimeException("动态修改日志级别出错:" + e.getMessage());
        }
        return "修改成功!";
    }

    /**
     * 通过名称查询日志等级
     * <p>
     * curl -X GET --location "http://localhost:8080/loggerLevelGet?name=com.liuxianfa.junit.springboot"
     *
     * @param name 日志名称,比如:root 或者包名 com.baidu.service  或者全类名
     */
    @RequestMapping("loggerLevelGet")
    public String loggerLevelGet(String name) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Optional<ch.qos.logback.classic.Logger> any = loggerContext.getLoggerList().stream().filter(log -> log.getName().equalsIgnoreCase(name)).findAny();
        boolean present = any.isPresent();
        if (!present) {
            throw new RuntimeException(String.format("不存在名为[%s]的Logger!", name));
        }

        Level level = any.get().getLevel();
        if (level == null) {
            return String.format("Logger[%s](ROOT),日志等级为:[%s]", name, loggerContext.getLogger("ROOT").getLevel());
        }
        return String.format("Logger[%s],日志等级为:[%s]", name, level);
    }

    /**
     * 测试输出日志等级(需要从日志文件或控制台中查看)
     *
     * curl -X GET --location "http://localhost:8080/loggerTest"
     */
    @RequestMapping("loggerTest")
    public String loggerTest() {
        logger.error("我是error");
        logger.warn("我是warn");
        logger.info("我是info");
        logger.debug("我是debug");

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger.debug(">>>所有日志等级:{}", loggerContext.getLoggerList().stream()
                                                  .filter(log -> log.getLevel() != null)
                                                  .map(log -> log.getLevel().levelStr + ":" + log.getName())
                                                  .collect(toList()));
        return "接口执行成功,打印日志需要去日志文件查看.";
    }
}
