作者：一个测试人员的日常
链接：https://www.jianshu.com/p/46998fd40484
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。





# 一、**背景**

前段时间公司有个项目要测试RocketMQ队列的消费处理能力，本人遇到了一些坑，最终都得到了解决，于是便有了这篇文章（最终开发的jmeter插件见文章末尾）。

# 二、**遇到的问题**

\1. 数据编码

\2. 多线程并发

\3. 队列的堆积

# 三、**问题分析和解决**

**问题一**：**MQ报文乱码****。**

**现象：**查看采集平台MQ中的被消费的日志，开发人员发现有部分报文乱码。

**原因：**开发人员通过查看后台日志，发现日志中某字段值为乱码，经排查，此字段值通常由汉字组成。

**解决方案：**在脚本发送请求时增加编码字段，并将message设为utf-8编码，如图：



![img](https:////upload-images.jianshu.io/upload_images/26223917-9debbe6991e10bba.png?imageMogr2/auto-orient/strip|imageView2/2/w/780/format/webp)

经修改，开发反馈请求返回预期报文了：



![img](https:////upload-images.jianshu.io/upload_images/26223917-23f10d4639132e46.png?imageMogr2/auto-orient/strip|imageView2/2/w/770/format/webp)

**问题二**：**多线程并发，连接MQ服务器后，启动生产者失败****。**

**现象：**按TPS梯度稳定发压场景，500TPS-2500TPS，需要多个线程并发以支持设定的TPS梯度，异常日志提示“The producer service state not OK, START_FAILED” 和“The producer service state not OK,CREATE_JUST”两种异常。

**原因：**经与开发沟通并自查脚本，每一个线程的生产者组名和实例名不可重复，于是修改脚本，对生产者组名和实例名进行参数分块；

![img](https:////upload-images.jianshu.io/upload_images/26223917-54e6f0cf7541e931.png?imageMogr2/auto-orient/strip|imageView2/2/w/917/format/webp)

重新发起多并发请求，仍然有上述异常请求出现，于是查看java sample中的具体请求代码，经过本地多次调试，最终定位是成员变量中producer和producerName设置为静态变量导致；



![img](https:////upload-images.jianshu.io/upload_images/26223917-e251a4a373c26a16.png?imageMogr2/auto-orient/strip|imageView2/2/w/752/format/webp)

**解决方案：**将去掉producer和producerName中static关键字，重新打包放到..\lib\ext下，本地多线程并发测试通过，如图：

![img](https:////upload-images.jianshu.io/upload_images/26223917-2f7cee52dce55ed1.png?imageMogr2/auto-orient/strip|imageView2/2/w/653/format/webp)



![img](https:////upload-images.jianshu.io/upload_images/26223917-90d1d16f89931438.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

**问题三**：**如何通过查看队列堆积情况，确定消费者的处理能力****。**

**方案：**按TPS梯度稳定发压场景，开始执行场景后，实时监控队列的堆积情况，当监控到队列中有明显的消息积压，并呈现有加大的趋势时，则判定在特定场景下此时达到消费者的最大处理能力。

**分析：**我们在此次压测中，通过按照TPS稳定发压，队列监控人员实时监测：在1400TPS之后出现10条以上的消息堆积（此前一直保持在10条以内），并且堆积逐渐加大，我们认为此时达到消费者最大的处理能力；经过压测后组内复盘，我们认为之前的策略存在一定的问题，因为在请求结束后，积压的消息可能在几秒内就消费完了，按照之前的策略评估会导致消费者能力偏悲观。

**最终方案：**根据分析，组内认为3秒内处理完是用户基本可接受的，因此消费者的最大处理能力应该是当队列中堆积消息达到当前TPS的3倍时较为合理。

# 四、**特别说明**

问题二中，我们实际对脚本做了两次调整，两次均为必要调整；另外生产者组名和实例名进行参数分块，最少保证对这两个入参中一个分块即可。

本文最终开发的jar包（jmeter插件）可参考以下：https://download.csdn.net/download/weixin_40126600/85011909?spm=1001.2014.3001.5501

请把下载的jar包放到jmeter的lib下的ext下，然后重启jmeter，在java sample中使用。

> 注： [RocketMQTest-1.0.jar](RocketMQTest-1.0.jar)   为下载下来的jmeter插件。





作者：一个测试人员的日常
链接：https://www.jianshu.com/p/46998fd40484
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。















