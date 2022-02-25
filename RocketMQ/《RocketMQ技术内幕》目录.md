> RocketMQ技术内幕
>
> RocketMQ架构设计与实现原理（第2版）
>
> 豆瓣读书  https://read.douban.com/ebook/330451274/





# 作品简介

这是一本指导读者如何在实践中让RocketMQ实现低延迟、高并发、高可用、高可靠的著作。

作者是RocketMQ官方认定的“优秀布道师”和技术专家，持续在RocketMQ领域深耕。本书从源码的角度分析了RocketMQ的技术架构和实现原理，第1版获得了良好的口碑，是RocketMQ领域的标志性作品，第2版做了较大幅度的更新。

Apache RocketMQ创始人/Linux OpenMessaging创始人兼主席/Alibaba Messaging开放技术负责人冯嘉高度评价并作序推荐。

全书一共11章，逻辑上可分为3个部分：第1部分（第1章）简单介绍了RocketMQ的设计理念与目标，以及阅读RocketMQ源码的方法与技巧；第二部分（第2～9章）从源码角度对RocketMQ的技术架构以及消息发送、消息存储、消息消费、消息过滤、顺序消息、主从同步、事务消息等主要功能模块的实现原理进行了深入分析。第三部分（第10～11章）首先从实战的角度讲了RocketMQ监控的原理、实现和应用，然后通过各种类型的大量示例展示了RocketMQ的使用技巧。

丁威，中间件技术专家，资深RocketMQ技术专家，曾获RocketMQ官方颁发的“优秀布道师”称号。现担任中通快递技术平台部资深架构师，主要负责全链路压测、消息中间件、数据同步等产品的研发与落地，拥有千亿级消息集群的运维经验，不仅实践经验丰富，而且对RocketMQ的源码有深入、系统的研究。热衷于中间件领域的技术分享，荣获“CSDN 2020博客之星”等荣誉称号，“中间件兴趣圈”公众号维护者。

张登，专家级架构师，资深RocketMQ技术专家，在分布式系统架构领域有丰富的实战经验，擅长高并发系统的架构设计与调优，主导过多家快递公司EA规划。

曾就职于拼多多、德邦等公司，现任圆通科技公司技术平台部架构负责人，负责开发框架的搭建、中间件及混合云相关技术的平台化建设。主导设计过消息分发、API网关、全链路监控、分布式文件存储等多个涉及百亿级规模的基础服务平台。“IT巅峰技术”公众号维护者，RocketMQ上海社区联合创始人。

周继锋，资深RocketMQ技术专家，知名开源分布式数据库中间件Mycat负责人。拥有10余年大型项目架构设计及实战经验，曾主导过大量分布式、微服务、大数据相关的项目。在高并发、高可用、高可扩展性、高可维护性等领域有丰富经验，对Hadoop、Spark的源码进行过深度分析并具有丰富的实战经验。曾在医学、互联网、SaaS行业担任资深架构师、技术总监等职务。现任炼数成金高级讲师，广州鼎牛网络、金石数字创始人。







# 作品目录

## [内容简介](https://read.douban.com/reader/ebook/330451274/toc/1)

## [作者简介](https://read.douban.com/reader/ebook/330451274/toc/2)

## [推荐语](https://read.douban.com/reader/ebook/330451274/toc/3)

## [序](https://read.douban.com/reader/ebook/330451274/toc/4)

## [前言](https://read.douban.com/reader/ebook/330451274/toc/5)



## [第1章阅读源码前的准备](https://read.douban.com/reader/ebook/330451274/toc/6)

##### [1.1　获取和调试RocketMQ的源码](https://read.douban.com/reader/ebook/330451274/toc/7)

##### [1.2　RocketMQ源码的目录结构](https://read.douban.com/reader/ebook/330451274/toc/8)

##### [1.3　RocketMQ的设计理念和设计目标](https://read.douban.com/reader/ebook/330451274/toc/9)

##### [1.4　本章小结](https://read.douban.com/reader/ebook/330451274/toc/10)



## [第2章RocketMQ路由中心NameServer](https://read.douban.com/reader/ebook/330451274/toc/11)

##### [2.1　NameServer架构设计](https://read.douban.com/reader/ebook/330451274/toc/12)

##### [2.2　NameServer启动流程](https://read.douban.com/reader/ebook/330451274/toc/13)

##### [2.3　NameServer路由注册、故障剔除](https://read.douban.com/reader/ebook/330451274/toc/14)

##### [2.4　本章小结](https://read.douban.com/reader/ebook/330451274/toc/15)



## [第3章RocketMQ消息发送](https://read.douban.com/reader/ebook/330451274/toc/16)

##### [3.1　漫谈RocketMQ消息发送](https://read.douban.com/reader/ebook/330451274/toc/17)

##### [3.2　认识RocketMQ消息](https://read.douban.com/reader/ebook/330451274/toc/18)

##### [3.3　生产者启动流程](https://read.douban.com/reader/ebook/330451274/toc/19)

##### [3.4　消息发送基本流程](https://read.douban.com/reader/ebook/330451274/toc/20)

##### [3.5　批量消息发送](https://read.douban.com/reader/ebook/330451274/toc/21)

##### [3.6　本章小结](https://read.douban.com/reader/ebook/330451274/toc/22)



## [第4章RocketMQ消息存储](https://read.douban.com/reader/ebook/330451274/toc/23)

##### [4.1　存储概要设计](https://read.douban.com/reader/ebook/330451274/toc/24)

##### [4.2　初识消息存储](https://read.douban.com/reader/ebook/330451274/toc/25)

##### [4.3　消息发送存储流程](https://read.douban.com/reader/ebook/330451274/toc/26)

##### [4.4　存储文件组织与内存映射](https://read.douban.com/reader/ebook/330451274/toc/27)

##### [4.5　RocketMQ存储文件](https://read.douban.com/reader/ebook/330451274/toc/28)

##### [4.6　实时更新ConsumeQueue与Index文件](https://read.douban.com/reader/ebook/330451274/toc/29)

##### [4.7　ConsumeQueue与Index文件恢复](https://read.douban.com/reader/ebook/330451274/toc/30)

##### [4.8　文件刷盘机制](https://read.douban.com/reader/ebook/330451274/toc/31)

##### [4.9　过期文件删除机制](https://read.douban.com/reader/ebook/330451274/toc/32)

##### [4.10　同步双写](https://read.douban.com/reader/ebook/330451274/toc/33)

##### [4.11　本章小结](https://read.douban.com/reader/ebook/330451274/toc/34)



## [第5章RocketMQ消息消费](https://read.douban.com/reader/ebook/330451274/toc/35)

##### [5.1　RocketMQ消息消费概述](https://read.douban.com/reader/ebook/330451274/toc/36)

##### [5.2　消息消费者初探](https://read.douban.com/reader/ebook/330451274/toc/37)

##### [5.3　消费者启动流程](https://read.douban.com/reader/ebook/330451274/toc/38)

##### [5.4　消息拉取](https://read.douban.com/reader/ebook/330451274/toc/39)

##### [5.5　消息队列负载与重新分布机制](https://read.douban.com/reader/ebook/330451274/toc/40)

##### [5.6　消息消费过程](https://read.douban.com/reader/ebook/330451274/toc/41)

##### [5.7　定时消息机制](https://read.douban.com/reader/ebook/330451274/toc/42)

##### [5.8　消息过滤机制](https://read.douban.com/reader/ebook/330451274/toc/43)

##### [5.9　顺序消息](https://read.douban.com/reader/ebook/330451274/toc/44)

##### [5.10　本章小结](https://read.douban.com/reader/ebook/330451274/toc/45)



## [第6章RocketMQ的ACL](https://read.douban.com/reader/ebook/330451274/toc/46)

##### [6.1　什么是ACL](https://read.douban.com/reader/ebook/330451274/toc/47)

##### [6.2　如何使用ACL](https://read.douban.com/reader/ebook/330451274/toc/48)

##### [6.3　ACL实现原理](https://read.douban.com/reader/ebook/330451274/toc/49)

##### [6.4　本章小结](https://read.douban.com/reader/ebook/330451274/toc/50)



## [第7章RocketMQ主从同步机制](https://read.douban.com/reader/ebook/330451274/toc/51)

##### [7.1　RocketMQ主从同步原理](https://read.douban.com/reader/ebook/330451274/toc/52)

##### [7.2　RocketMQ读写分离机制](https://read.douban.com/reader/ebook/330451274/toc/53)

##### [7.3　RocketMQ元数据同步](https://read.douban.com/reader/ebook/330451274/toc/54)

##### [7.4　本章小结](https://read.douban.com/reader/ebook/330451274/toc/55)



## [第8章RocketMQ消息轨迹](https://read.douban.com/reader/ebook/330451274/toc/56)

##### [8.1　消息轨迹的引入目的和使用方法](https://read.douban.com/reader/ebook/330451274/toc/57)

##### [8.2　消息轨迹设计原理](https://read.douban.com/reader/ebook/330451274/toc/58)

##### [8.3　消息轨迹实现原理](https://read.douban.com/reader/ebook/330451274/toc/59)

##### [8.4　本章小结](https://read.douban.com/reader/ebook/330451274/toc/60)



## [第9章RocketMQ主从切换](https://read.douban.com/reader/ebook/330451274/toc/61)

##### [9.1　主从切换引入目的](https://read.douban.com/reader/ebook/330451274/toc/62)

##### [9.2　Raft协议简介](https://read.douban.com/reader/ebook/330451274/toc/63)

##### [9.3　RocketMQ DLedger主从切换之Leader选主](https://read.douban.com/reader/ebook/330451274/toc/64)

##### [9.4　RocketMQ DLedger主从切换之存储实现](https://read.douban.com/reader/ebook/330451274/toc/65)

##### [9.5　RocketMQ DLedger主从切换之日志追加](https://read.douban.com/reader/ebook/330451274/toc/66)

##### [9.6　RocketMQ DLedger主从切换之日志复制](https://read.douban.com/reader/ebook/330451274/toc/67)

##### [9.7　RocketMQ整合DLedger设计技巧与实现原理](https://read.douban.com/reader/ebook/330451274/toc/68)

##### [9.8　RocketMQ主从切换实战](https://read.douban.com/reader/ebook/330451274/toc/69)

##### [9.9　本章小结](https://read.douban.com/reader/ebook/330451274/toc/70)



## [第10章RocketMQ监控](https://read.douban.com/reader/ebook/330451274/toc/71)

##### [10.1　设计理念](https://read.douban.com/reader/ebook/330451274/toc/72)

##### [10.2　实现原理](https://read.douban.com/reader/ebook/330451274/toc/73)

##### [10.3　监控数据采样机制](https://read.douban.com/reader/ebook/330451274/toc/74)

##### [10.4　如何采集监控指标](https://read.douban.com/reader/ebook/330451274/toc/75)

##### [10.5　监控实战应用](https://read.douban.com/reader/ebook/330451274/toc/76)

##### [10.6　本章小结](https://read.douban.com/reader/ebook/330451274/toc/77)



## [第11章RocketMQ实战](https://read.douban.com/reader/ebook/330451274/toc/78)

##### [11.1　消息批量发送](https://read.douban.com/reader/ebook/330451274/toc/79)

##### [11.2　消息发送队列自选择](https://read.douban.com/reader/ebook/330451274/toc/80)

##### [11.3　消息过滤](https://read.douban.com/reader/ebook/330451274/toc/81)

##### [11.4　事务消息](https://read.douban.com/reader/ebook/330451274/toc/82)

##### [11.5　Spring整合RocketMQ](https://read.douban.com/reader/ebook/330451274/toc/83)

##### [11.6　Spring Cloud整合RocketMQ](https://read.douban.com/reader/ebook/330451274/toc/84)

##### [11.7　RocketMQ监控与运维命令](https://read.douban.com/reader/ebook/330451274/toc/85)

##### [11.8　应用场景分析](https://read.douban.com/reader/ebook/330451274/toc/86)

##### [11.9　实战案例](https://read.douban.com/reader/ebook/330451274/toc/87)

##### [11.10　本章小结](https://read.douban.com/reader/ebook/330451274/toc/88)

##### [附录A参数说明](https://read.douban.com/reader/ebook/330451274/toc/89)

##### [附录BRocketMQ各版本概述与升级建议](https://read.douban.com/reader/ebook/330451274/toc/90)