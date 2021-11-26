# 前言

NameServer最重要的作用之一就是：**维护broker服务地址及及时更新**

# Topic路由注册、剔除机制

> 下图来自：https://www.bilibili.com/video/BV1p44y1Y7AR

![image-20211125221127915](images/image-20211125221127915.png)

> 下图来自：https://www.bilibili.com/video/BV11Q4y1r7aW?p=9

![image-20211029102127556](images/image-20211029102127556.png)

我们根据以上流程来分步骤看一下对应的源码。

## 1、broker每30s向NameServer发送心跳包

请求类型：RequestCode#REGISTER_BROKER

```java
public static final int REGISTER_BROKER = 103;
```

**broker启动的时候，向所有NameServer注册broker：**

![BrokerOuterAPI#registerBrokerAll](images/image-20211117192602165.png)

**向指定的NameServer注册broker：**

![BrokerOuterAPI#registerBroker](images/image-20211117192653730.png)



## 2、NameServer收到broker的心跳包时会记录收到心跳包的时间

在NameServer的请求处理器中：

![image-20211126175325897](images/image-20211126175325897.png)



**this.registerBroker(ctx, request);**

![image-20211126175446544](images/image-20211126175446544.png)

> NameServer收到broker的注册请求之后，会直接调用下面方法：使用路由表管理器，注册broker。
>
> ```java
> this.namesrvController.getRouteInfoManager().registerBroker()
> ```
>
> this.namesrvController.getRouteInfoManager().registerBroker()   方法中：
>
> - 申请写锁
> - 把brokerName记录到 `clusterAddrTable`
> - 把brokerData保存到 `brokerAddrTable`
> - 记录broker存户信息到 `brokerLiveTable`，并更新 `lastUpdateTimestamp` （用于判断broker是否存活用）
> - 保存 `filterServerTable`
> - 如果brokerId不是0,表示slave节点——则给slave返回master的地址和ha服务地址.
> - 释放写锁
>
> 小结：其实注册broker的逻辑，就是记录broker的各种信息，并提供接口让别人查询，另外就是记录broker的最后活跃的时间，如果超过120s，就移除broker。这也是路由信息管理器的主要作用。
>
> 备注：以上这些Table表，详见：[RouteInfoManager](#RouteInfoManager)





## 3、NameServer每10s扫描broker注册表，移除超时的broker。超时时间为120s

![image-20211126183850825](images/image-20211126183850825.png)





![image-20211126183809135](images/image-20211126183809135.png)

上面，关闭channel，并从 `brokerLiveTable` 移除之后，

会调用 `public void onChannelDestroy(String remoteAddr, Channel channel)` 方法，

这个方法是比较长的，就是维护各个Table中的信息，该删掉的都删掉。*细节就不粘贴出来了，有兴趣自己去看看。*



## 4、broker持久化路由信息



## 5、根据topic查询路由信息













# NameServer如何管理路由表

## RouteInfoManager







# broker启动时注册







# 路由表的获取

producer和consumer都会连接NameServer，去获取路由表。





























