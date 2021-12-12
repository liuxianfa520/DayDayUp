```java
package org.apache.rocketmq.remoting.netty;

public class NettyServerConfig implements Cloneable {
    /**
     * server监听端口号
     *
     * 通过set方法,可以知道:
     * broker作为server端,默认通道监听端口号是:10911
     * broker作为server端,vip通道监听端口号是:10909
     * NameServer监听端口号是:9876
     *
     * 而且这个端口号,是不能通过配置项进行修改的.
     *
     *
     * 其他的端口号: {@link org.apache.rocketmq.store.config.MessageStoreConfig#haListenPort}
     */
    private int listenPort = 8888;
}    
```









# 高可用HA监听端口号

org.apache.rocketmq.store.config.MessageStoreConfig#haListenPort

```java
package org.apache.rocketmq.store.config;

public class MessageStoreConfig {
    private int haListenPort = 10912;
}
```







































