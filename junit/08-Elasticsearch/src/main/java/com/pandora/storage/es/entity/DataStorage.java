package com.pandora.storage.es.entity;

import com.alibaba.fastjson.JSON;
import com.pandora.storage.es.util.Constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.map.MapUtil;
import lombok.Data;

/**
 * @author LiuXianfa
 * @email xianfaliu@newbanker.cn
 * @date 2020/1/11 15:53
 */
@Data
public class DataStorage extends HashMap {

    private Long objectId;
    private String appId;
    private String className;
    private String serverData;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public DataStorage() {
    }

    public DataStorage(Map sourceMap) {
        putAll(sourceMap);
    }

    public DataStorage(String appId, String className, String content) {
        setAppId(appId);
        setClassName(className);
        setServerData(content);
    }

    public DataStorage(String appId, String className, Long objectId, String content) {
        setObjectId(objectId);
        setAppId(appId);
        setClassName(className);
        setServerData(content);
    }

    public String getAppId() {
        return MapUtil.getStr(this, "appId");
    }

    public DataStorage setAppId(String appId) {
        put("appId", appId);
        return this;
    }

    public String getObjectId() {
        return MapUtil.getStr(this, "objectId");
    }

    /**
     * <pre>
     * setObjectId参数类型为Long，在数据库和es中保存的也都是bigint/long类型，
     *      因为db需要根据此字段创建索引，数字类型的索引比字符串类型的索引查询效率高。
     * 但是getObjectId返回类型为String，是为了数据传输时，数据不会失真。
     * </pre>
     *
     * @param objectId
     */
    public DataStorage setObjectId(Long objectId) {
        put("objectId", objectId);
        return this;
    }

    public String getClassName() {
        return MapUtil.getStr(this, "className");
    }

    public DataStorage setClassName(String className) {
        put("className", className);
        return this;
    }

    /**
     * <pre>
     *
     * 【不可使用此方法！！！】
     *
     * 除了 mehe-data-storage 子服务外，其他服务都不可使用此方法，
     * 因为这个方法总返回null。哈哈哈。>_<
     *
     *
     * 如果需要获取 name 字段，直接使用：
     * {@code
     * String name = dataStorage.getString("name"); 即可
     * }
     * </pre>
     *
     * @return
     */
    @Deprecated
    public String getServerData() {
        return MapUtil.getStr(this, "serverData");
    }

    public DataStorage setServerData(String serverData) {
        put("serverData", serverData);
        return this;
    }

    public DataStorage setServerData(Map serverData) {
        put("serverData", JSON.toJSONString(serverData));
        return this;
    }

    /**
     * <pre>
     * 把
     * {
     *      "className":"user",
     *      "objectId":"xxxxxxxxxxxxxxxxxxxxxxxx",
     *      "serverData":"{\"name\":\"张三\",\"age\":18}"
     * }
     * 转成：
     * {
     *      "className":"user",
     *      "objectId":"xxxxxxxxxxxxxxxxxxxxxxxx",
     *      "serverData":"{\"name\":\"张三\",\"age\":18}",
     *      "name":"张三",
     *      "age":18
     * }
     * </pre>
     *
     * @return 返回原对象
     */
    public DataStorage transferServerData() {
        putAll(JSON.parseObject(getServerData()));
        return this;
    }

    /**
     * <pre>
     * 把
     * {
     *      "className":"user",
     *      "objectId":"xxxxxxxxxxxxxxxxxxxxxxxx",
     *      "serverData":"{\"name\":\"张三\",\"age\":18}"
     * }
     * 转成：
     * {
     *      "className":"user",
     *      "objectId":"xxxxxxxxxxxxxxxxxxxxxxxx",
     *      "name":"张三",
     *      "age":18
     * }
     * </pre>
     *
     * @return 返回原对象
     */
    public DataStorage transferAndRemoveServerData() {
        String serverData = getServerData();
        if (serverData == null) {
            return this;
        }
        putAll(JSON.parseObject(serverData));
        remove("serverData");
        return this;
    }

    public Date getCreateTime() {
        return MapUtil.getDate(this, "createTime");
    }

    public DataStorage setCreateTime(Date createTime) {
        put("createTime", createTime);
        return this;
    }

    public Date getUpdateTime() {
        return MapUtil.getDate(this, "updateTime");
    }

    public DataStorage setUpdateTime(Date updateTime) {
        put("updateTime", updateTime);
        return this;
    }

    public String getScrollId() {
        return MapUtil.getStr(this, "_scroll_id");
    }

    // ========================= 根据key值，获取指定类型的value =========================


    @Override
    public DataStorage put(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    public String getString(String key) {
        Object o = get(key);
        return o == null ? null : (String) o;
    }

    public List getList(String key) {
        Object o = get(key);
        return o == null ? null : (List) o;
    }

    public Integer getInteger(String key) {
        Object o = get(key);
        return o == null ? null : (Integer) o;
    }

    public boolean getBoolean(String key) {
        Object o = get(key);
        return o != null && Boolean.parseBoolean((String) o);
    }

    /**
     * 只保留指定key，其他的key remove掉。
     *
     * @param keys 需要保留的keys
     * @return 还返回this对象，并不是重新new一个新的对象
     */
    public DataStorage remainKeys(String... keys) {
        List<String> remainKeys = Arrays.asList(keys);
        for (Iterator<Entry<String, Object>> it = entrySet().iterator(); it.hasNext(); ) {
            Entry<String, Object> item = it.next();
            if (!remainKeys.contains(item.getKey())) {
                it.remove();
            }
        }
        return this;
    }

    /**
     * 只保留指定key，其他的key remove掉。
     *
     * @param keys 需要保留的keys
     * @return 返回一个新的对象，对象包含指定的keys
     */
    public DataStorage remainKeysReturnNewInstance(String... keys) {
        List<String> remainKeys = Arrays.asList(keys);
        DataStorage d = new DataStorage();
        for (String key : (Set<String>) keySet()) {
            if (remainKeys.contains(key)) {
                d.put(key, get(key));
            }
        }
        return d;
    }

    /**
     * <pre>
     * 移除掉所有的‘系统保留字段’
     *
     * 系统保留字段见：{@link Constants#Reserved_Column}
     * </pre>
     *
     * @return this
     */
    public DataStorage removeSystemReservedKeys() {
        for (Iterator<Entry<String, Object>> it = entrySet().iterator(); it.hasNext(); ) {
            Entry<String, Object> item = it.next();
            if (Constants.Reserved_Column.contains(item.getKey())) {
                it.remove();
            }
        }
        return this;
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }
}
