# 前言

NameServer最重要的作用之一就是：**维护broker服务地址及及时更新**

# Topic路由注册、剔除机制

![image-20211029102127556](images/image-20211029102127556.png)

> 上图来自：https://www.bilibili.com/video/BV11Q4y1r7aW?p=9

我们根据以上流程来分步骤看一下对应的源码。

## 1、broker每30s向NameServer发送心跳包

请求类型：RequestCode#REGISTER_BROKER

```java
public static final int REGISTER_BROKER = 103;
```

**向所有NameServer注册broker：**

![BrokerOuterAPI#registerBrokerAll](images/image-20211117192602165.png)

**向指定的NameServer注册broker：**

![BrokerOuterAPI#registerBroker](images/image-20211117192653730.png)



## 2、NameServer收到broker的心跳包时会记录收到心跳包的时间



## 3、NameServer每10s扫描broker注册表，移除超时的broker。超时时间为120s



## 4、broker持久化路由信息



## 5、根据topic查询路由信息













# NameServer如何管理路由表

## RouteInfoManager







# broker启动时注册







# 路由表的获取

producer和consumer都会连接NameServer，去获取路由表。





























