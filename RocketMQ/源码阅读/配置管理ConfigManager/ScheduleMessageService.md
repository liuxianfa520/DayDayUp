# 简述

在RocketMQ中有延迟队列，延迟队列的调度，就是由`ScheduleMessageService`来负责的。

我们在RocketMQ中使用延迟队列的时候，这个`延迟多久`并不是随意设置的，RocketMQ设置了几个默认的延迟等级，只能从这几个延迟等级中选择：

```java
private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
```

`延迟消息服务ScheduleMessageService`中有个定时任务timer，对不同的`延迟等级`，都注册一个延迟task——`DeliverDelayedMessageTimerTask`。

另外，如果broker是slave，则slave会给master发送 `GET_ALL_DELAY_OFFSET` 请求，去获取master的延迟队列处理进度，然后持久化到slave机器的磁盘文件中。这个详见：[请求类型及处理/GET_ALL_DELAY_OFFSET.md](../网络组件Remoting/请求类型及处理/GET_ALL_DELAY_OFFSET.md)





# 类结构

```java
package org.apache.rocketmq.store.schedule;

public class ScheduleMessageService extends ConfigManager {
}
```



# 重要属性

在这个类中，有两个map，是比较重要的：

```java
/**
 * 偏移量表
 */
private final ConcurrentMap<Integer /* level */, Long/* offset */> offsetTable = new ConcurrentHashMap<>(32);

/**
 * 延迟等级表
 */
private final ConcurrentMap<Integer /* level */, Long/* delay timeMillis */> delayLevelTable = new ConcurrentHashMap<>(32);
```

偏移量表 offsetTable 是需要持久化到磁盘的。并且slave还需要从master获取offsetTable，并持久化到slave的磁盘中。是比较重要的。[更多>](#offsetTable)

delayLevelTable 延迟等级表，key存的是延迟等级，value存储的是延迟的时间毫秒数，比如消息需要延迟1分钟，则存储的是 60*1000 。详见：[load()](#load())



# 重写的方法

在 [readme.md](readme.md) 中也说了，`ConfigManager` 是个抽象类，其中有4个抽象方法需要被子类实现：

## configFilePath()

```java
指定持久化到磁盘文件的绝对路径
public abstract String configFilePath();
```

![image-20211203185006634](images/image-20211203185006634.png)

格式：{rootDir}/config/delayOffset.json



## encode();

```java
把内存中的配置，转成字符串
public abstract String encode();
public abstract String encode(final boolean prettyFormat);
```

![image-20211203185310536](images/image-20211203185310536.png)

encode 方法，就是把 `offsetTable` 表转成json字符串。

然后在`ConfigManager`的持久化方法中，就会把这个字符串保存到文件中。



## decode();

```java
把磁盘文件读取出来的字符串，转成内存中的java对象。
public abstract void decode(final String jsonString);
```



![image-20211203185506975](images/image-20211203185506975.png)

把`字符串`反序列化成`offsetTable`。然后再putAll到内存中。

> 这个  `反序列化` ，其实就是把jsonString字符串，用 fastjson 转成javaBean:
>
> ![image-20211203185743757](images/image-20211203185743757.png)



> 画外音：
>
> 到这里已经知道了在持久化和从磁盘文件中读取配置时的流程。
>
> 接下来就看看这个类是如何来处理延迟队列的。



## load()

load() 方法不是抽象方法，但是 `ScheduleMessageService` 也重写了：

```java
@Override
public boolean load() {
    return super.load() && this.parseDelayLevel();
}
```

也就是，先调用父类中的加载逻辑，然后在 `ScheduleMessageService` 中又自定义了一些加载逻辑。

**boolean parseDelayLevel()** 方法，解析延迟等级，那么什么是`延迟等级`呢？

其实：我们在RocketMQ中使用延迟队列的时候，这个`延迟多久`并不是随意设置的，RocketMQ设置了几个默认的延迟等级，只能从这几个延迟等级中选择：

```java
private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
```

所以**boolean parseDelayLevel()** 方法：

![image-20211203191955267](images/image-20211203191955267.png)



# 定时任务Timer

```java
/**
 * 是否启动中
 *
 * 在关闭方法中 {@link #shutdown()} ,会使用cas把这个设置为false
 * 如果设置为了false,则定时任务 {@link #timer} 线程就会优雅停止.
 */
private final AtomicBoolean started = new AtomicBoolean(false);

/**
 * 定时任务
 */
private Timer timer;
```





**broker启动时：**

**一、启动时,从磁盘配置文件中加载**

**二、定时任务: 注册延迟定时任务**

**三、定时任务:持久化定时任务**

> 如果是broker是slave,在[启动]和[broker由master转成slave]时,还会发送 GET_ALL_DELAY_OFFSET 请求，从master拉取最新的配置，并持久化到slave的磁盘文件中。
>
> 详见：[请求类型及处理/GET_ALL_DELAY_OFFSET.md](../网络组件Remoting/请求类型及处理/GET_ALL_DELAY_OFFSET.md)

![image-20211203190223402](images/image-20211203190223402.png)

这个类，其实就是对任务的调度。具体延迟消息任务，是`DeliverDelayedMessageTimerTask`中。





# DeliverDelayedMessageTimerTask

![image-20211203200452638](images/image-20211203200452638.png)







# 延迟消息流程图

https://docs.qq.com/flowchart/DQW9PYmxhZlpVUFNL

抽象流程图：https://docs.qq.com/flowchart/DQUxIR0JURE9Idlpo







# offsetTable

上面说了，在 org.apache.rocketmq.store.schedule.ScheduleMessageService 中的重要属性：

```java
/**
 * 偏移量表
 */
private final ConcurrentMap<Integer /* level */, Long/* offset */> offsetTable = new ConcurrentHashMap<>(32);
```

## map的key

这个map的key表示延迟等级 delayLevel。delayLevel和queueId是有关系的：

- queueId = delayLevel - 1
- delayLevel = queueId + 1

```java
public static int delayLevel2QueueId(final int delayLevel) {
    return delayLevel - 1;
}
public static int queueId2DelayLevel(final int queueId) {
    return queueId + 1;
}
```

并且RocketMQ系统中存放延迟消息的队列：

```java
// org.apache.rocketmq.common.topic.TopicValidator#RMQ_SYS_SCHEDULE_TOPIC
public static final String RMQ_SYS_SCHEDULE_TOPIC = "SCHEDULE_TOPIC_XXXX";
```

这个队列的默认queue数量是：

```java
// org.apache.rocketmq.broker.topic.TopicConfigManager#SCHEDULE_TOPIC_QUEUE_NUM
private static final int SCHEDULE_TOPIC_QUEUE_NUM = 18;
```

broker初始化的时候，默认自动创建延迟topic：

```java
private final ConcurrentMap<String, TopicConfig> topicConfigTable = new ConcurrentHashMap<>(1024);

public TopicConfigManager(BrokerController brokerController) { // 省略其他默认自动创建的topic及代码逻辑。
    String topic = TopicValidator.RMQ_SYS_SCHEDULE_TOPIC; // SCHEDULE_TOPIC_XXXX
    TopicConfig topicConfig = new TopicConfig(topic);
    TopicValidator.addSystemTopic(topic);
    topicConfig.setReadQueueNums(SCHEDULE_TOPIC_QUEUE_NUM);
    topicConfig.setWriteQueueNums(SCHEDULE_TOPIC_QUEUE_NUM);
    this.topicConfigTable.put(topicConfig.getTopicName(), topicConfig);
}    
```

## map的value

这个map的value是 Long offset，那么这个offset到底有什么用呢？

**offset其实表示的是consumeQueue文件的逻辑偏移量！**再直白点说——就是consumeQueue文件内容的下标：

![image-20211206193725750](images/image-20211206193725750.png)

![image-20211206205501398](images/image-20211206205501398.png)

详见：[consumeQueue文件详解](https://gitee.com/anxiaole/rocketmq/blob/master/docs/cn/design.md#consumequeue)









# 使用场景

- 如果broker是master，才会启动 `延迟消息服务ScheduleMessageService`——使用其中的timer投递延迟消息。
  - slave只会定期从master拉取配置，然后持久化到slave的磁盘中。
  - 如果某天master宕机了，slave变成master，持久化到磁盘中的配置才能使用上。
  - DLedger详见：[broker高可用——DLedger机制](../Broker/ha/broker高可用——DLedger机制.md)
- 源码：

```java
public class DefaultMessageStore implements MessageStore {
    
    private final ScheduleMessageService scheduleMessageService;
    
    public DefaultMessageStore(MessageStoreConfig messageStoreConfig, 省略其他构造参数...) {
        this.scheduleMessageService = new ScheduleMessageService(this);
    }
    
    @Override
    public void start(){ // 启动 DefaultMessageStore 的其他逻辑省略。。。。
        
        // 如果没有启用DLedger模式:
        //  1、启动HA服务
        //  2、根据broker角色处理延迟消息服务：如果broker是master,则启动 ScheduleMessageService
        if (!messageStoreConfig.isEnableDLedgerCommitLog()) {
            // HA服务启动
            this.haService.start();
            
            this.handleScheduleMessageService(messageStoreConfig.getBrokerRole());
        }
    }
    
    @Override
    public void handleScheduleMessageService(BrokerRole brokerRole) {
        if (this.scheduleMessageService != null) {
            if (brokerRole == BrokerRole.SLAVE) {
                this.scheduleMessageService.shutdown();
            } else {
                // 只有master才会启动延迟消息服务.
                this.scheduleMessageService.start();
            }
        }
    }
}    
```

> 注释：
>
> 从上面源码上，你可能会觉得：*“当broker没有启动DLedger模式，并且broker是master，才会启动 ScheduleMessageService”*。
>
> 这种感觉是不对的！
>
> 如果启动了DLedger模式，broker的角色会自动变化：
>
> org.apache.rocketmq.broker.BrokerController#changeToMaster(int brokerId) 
>
> ![image-20211223235046144](images/image-20211223235046144.png)
>
> org.apache.rocketmq.broker.BrokerController#changeToSlave(int brokerId) 
>
> ![image-20211223235141276](images/image-20211223235141276.png)
>
> - 通过这也是能从侧面反映：DLedger模式只是进行commitLog文件的数据同步，并不会处理延迟队列消费的持久化数据。







