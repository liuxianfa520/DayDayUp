# RocketMQ中连接结构图

![image-20211115143017260](images/image-20211115143017260.png)

在RocketMQ中，producer和consumer都是作为客户端——client

通过上图，看到producer和consumer需要和NameServer和broker进行网络连接。这些网络连接都是通过netty来完成的。

在RocketMQ中，使用 `org.apache.rocketmq.client.impl.factory.MQClientInstance` 这个类封装了客户端。



# 类

```java
package org.apache.rocketmq.client.impl.factory;

/**
 * client表示:producer或consumer
 *
 * 在一个JVM中,所有消费者、生产者持有同一个MQClientInstance
 * MQClientInstance只会启动一次。
 */
public class MQClientInstance {
    
}
```





# 属性











