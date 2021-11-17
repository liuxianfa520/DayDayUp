# 官方文档

[设计-通信机制](https://gitee.com/anxiaole/rocketmq/blob/master/docs/cn/design.md#2-%E9%80%9A%E4%BF%A1%E6%9C%BA%E5%88%B6)



# 简述

RocketMQ有很多的角色：

- NameServer
- Broker
- Producer
- Consumer

这些角色之间会有很多的网络请求，底层是使用的netty作为网络框架传输的数据。

![image-20211029101355878](images/image-20211029101355878.png)

![image-20211029101823584](images/image-20211029101823584.png)

> 上图来自：Java程序员King —— https://www.bilibili.com/video/BV11Q4y1r7aW?p=9



从源码的层面，这些长连接主要接口是： `org.apache.rocketmq.remoting.RemotingService` 

结果图如下：

![image-20211028201751282](images/image-20211028201751282.png)

从这个结构图中，我们看到：网络组件主要分两个角色：

- client    ——  Producer、Consumer、Broker（broker想NameServer注册自己的信息时作为client角色）
- server  ——  Broker、NameServer

并且这两个角色中间有的一些公用逻辑，封装到了抽象类中：`org.apache.rocketmq.remoting.netty.NettyRemotingAbstract`

> 在了解 `RemotingService` 之前，我们先了解一下 `RemotingCommand远程命令`
>
> `RemotingCommand` 可以理解为RocketMQ之间网络数据传输的实体类。
>
> `RemotingService` 在下一章：[RemotingService](#RemotingService)

# 远程命令 RemotingCommand

在RocketMQ中，无论是request还是response，都是使用`org.apache.rocketmq.remoting.protocol.RemotingCommand`类来表示的。

只不过使用如下方法，可以知道当前这个`远程命令`实例的类型是`request`还是`response`：

```java
    @JSONField(serialize = false)
    public RemotingCommandType getType() {
        if (this.isResponseType()) {
            return RemotingCommandType.RESPONSE_COMMAND;
        }

        return RemotingCommandType.REQUEST_COMMAND;
    }

    @JSONField(serialize = false)
    public boolean isResponseType() {
        int bits = 1 << RPC_TYPE;
        return (this.flag & bits) == bits;
    }
```

枚举：

```java
public enum RemotingCommandType {
    REQUEST_COMMAND,
    RESPONSE_COMMAND;
}
```



## 重要属性

```java
    /**
     * 请求id <br/>
     * 这是静态成员变量,所有请求使用同一个实例.
     */
    private static AtomicInteger requestId = new AtomicInteger(0);
    /**
     * 当前请求的请求id
     */
    private int opaque = requestId.getAndIncrement();
    /**
     * 请求code
     */
    private int code;
    /**
     * 请求体
     * 设置请求体的时候，就需要把请求体编译成字节码。
     * 解析请求体时，是根据 请求code 来区分是哪中请求。然后再把byte[]反序列化成 RemotingCommand。
     */
    private transient byte[] body;
```



## 重要方法

### 编码请求头

org.apache.rocketmq.remoting.protocol.RemotingCommand#encodeHeader()

> 其实就是把程序中表示请求的 `RemotingCommand` 根据RocketMQ自己定义的格式，进行转成byte[]字节流。
>
> 用于在网络I/O中传输数据。

![image-20211028204317885](images/image-20211028204317885.png)

上面是编码请求头，但是`body`请求体是不需要编码的。

因为body在设置进来的时候，已经是`byte[]字节流`了。



### 解码

org.apache.rocketmq.remoting.protocol.RemotingCommand#decode(byte[])

> 解码，就是从网络I/O中收到byte[]之后，需要把byte[]字节流转成 `RemotingCommand`，
>
> 便于在程序中使用。

```java
    public static RemotingCommand decode(final byte[] array) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        return decode(byteBuffer);              // 其实就是重载方法。主要看下面的方法。
    }
```

org.apache.rocketmq.remoting.protocol.RemotingCommand#decode(java.nio.ByteBuffer)

```java
    /**
     * 解码
     * 把字节流,转成 {@link RemotingCommand} 对象
     */
    public static RemotingCommand decode(final ByteBuffer byteBuffer) {
        // 字节流的总长度(总共多少个字节).
        int length = byteBuffer.limit();
        // 先获取4个字节,作为int类型.但是需要注意:这个int并不是一个整数.详见下面getHeaderLength(oriHeaderLen);方法.
        int oriHeaderLen = byteBuffer.getInt();
        // todo:疑问:再转成???另一种格式???   这有什么区别???
        int headerLength = getHeaderLength(oriHeaderLen);

        // 再从ByteBuffer中读取 headerLength 个字节  (也就是请求头)
        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        // 序列化类型:JSON 和 ROCKETMQ 两种.
        SerializeType protocolType = getProtocolType(oriHeaderLen);
        // 把请求头的byte[]字节流,反序列化成 RemotingCommand 实例.
        RemotingCommand cmd = headerDecode(headerData, protocolType);


        // 所有长度,减去int类型4个字节,再减去请求头的长度.就是请求体的长度.
        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            // 读取请求体
            byteBuffer.get(bodyData);
        }
        cmd.body = bodyData;

        return cmd;
    }

```







> 其实最重要的两个方法，就是编码和解码。
>
> 其他方法都是工具方法等。比如：
>
> 创建请求：
>
> ![image-20211102154928278](images/image-20211102154928278.png)
>
> 创建响应：
>
> ![image-20211102155022143](images/image-20211102155022143.png)







### ROCKETMQ数据传输协议格式

官方文档：[设计-通信机制-协议设计与编解码](https://gitee.com/anxiaole/rocketmq/blob/master/docs/cn/design.md#22-%E5%8D%8F%E8%AE%AE%E8%AE%BE%E8%AE%A1%E4%B8%8E%E7%BC%96%E8%A7%A3%E7%A0%81)



> 画外音：
>
> 不是很重要，第一次看源码时了解就行了。

再来说个概念，**序列化类型**：

```java
    /**
     * 请求需要序列化之后,通过netty网络框架发送出去.
     * 接收到的字节流也需要反序列化.
     * 那么序列化和反序列化的类型是那种的,就是这个字段控制的.
     * 默认是JSON类型的. {@link SerializeType#JSON}
     */
    private static SerializeType serializeTypeConfigInThisServer = SerializeType.JSON;

    static {
        // 可以通过系统属性配置.(但是启动之后,无法修改.)
        final String protocol = System.getProperty(SERIALIZE_TYPE_PROPERTY, System.getenv(SERIALIZE_TYPE_ENV));
        if (!isBlank(protocol)) {
            try {
                serializeTypeConfigInThisServer = SerializeType.valueOf(protocol);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("parser specified protocol error. protocol=" + protocol, e);
            }
        }
    }
```

JSON序列化都很好理解，就是把 `RemotingCommand` 转成json格式字符，然后在转成byte[]并使用netty发送出去。json格式的就不多说了。

RocketMQ自己定义了一种数据传输协议：

这种协议格式：todo：缺少一个图片。

具体方法详见：`org.apache.rocketmq.remoting.protocol.RocketMQSerializable#rocketMQProtocolDecode`

```java
    /**
     * rocketmq自定义的数据协议传输格式.
     *
     * @param headerArray netty网络传输过来的请求头字节流
     * @return
     */
    public static RemotingCommand rocketMQProtocolDecode(final byte[] headerArray) {
        RemotingCommand cmd = new RemotingCommand();
        ByteBuffer headerBuffer = ByteBuffer.wrap(headerArray);
        // int code(~32767)
        cmd.setCode(headerBuffer.getShort());
        // LanguageCode language
        cmd.setLanguage(LanguageCode.valueOf(headerBuffer.get()));
        // int version(~32767)
        cmd.setVersion(headerBuffer.getShort());
        // int opaque
        cmd.setOpaque(headerBuffer.getInt());
        // int flag
        cmd.setFlag(headerBuffer.getInt());
        // String remark
        int remarkLength = headerBuffer.getInt();
        if (remarkLength > 0) {
            byte[] remarkContent = new byte[remarkLength];
            headerBuffer.get(remarkContent);
            cmd.setRemark(new String(remarkContent, CHARSET_UTF8));
        }

        // HashMap<String, String> extFields
        int extFieldsLength = headerBuffer.getInt();
        if (extFieldsLength > 0) {
            byte[] extFieldsBytes = new byte[extFieldsLength];
            headerBuffer.get(extFieldsBytes);
            cmd.setExtFields(mapDeserialize(extFieldsBytes));
        }
        return cmd;
    }
```





### RemotingCommand 中的位运算









# RemotingService

从源码的层面，这些长连接主要接口是： `org.apache.rocketmq.remoting.RemotingService` 

结果图如下：

![image-20211028201751282](images/image-20211028201751282.png)

从这个结构图中，我们看到：网络组件主要分两个角色：

- client    ——  Producer、Consumer、Broker（broker想NameServer注册自己的信息时作为client角色）
- server  ——  Broker、NameServer

由于RocketMQ是使用netty作为网络I/O框架，所以

对于 client有实现：NettyRemotingClient

对于server有实现：NettyRemotingServer。

并且这两个角色中间有的一些公用逻辑，封装到了抽象类中：`org.apache.rocketmq.remoting.netty.NettyRemotingAbstract`



## RemotingService接口方法

```java
public interface RemotingService {
    void start();      // 启动

    void shutdown();   // 关闭

    void registerRPCHook(RPCHook rpcHook);   // 注册rpc钩子
}
```



## client启动：

org.apache.rocketmq.remoting.netty.NettyRemotingClient#start

```java
    @Override
    public void start() {
        // 设置默认的事件执行线程组.也就是工作线程池
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
            nettyClientConfig.getClientWorkerThreads(), // client执行线程数
            // 线程池的线程工厂
            new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "NettyClientWorkerThread_" + this.threadIndex.incrementAndGet());
                }
            });

        // 启动netty框架
        Bootstrap handler = this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.SO_KEEPALIVE, false)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
            .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
            .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (nettyClientConfig.isUseTLS()) {
                        if (null != sslContext) {
                            pipeline.addFirst(defaultEventExecutorGroup, "sslHandler", sslContext.newHandler(ch.alloc()));
                            log.info("Prepend SSL handler");
                        } else {
                            log.warn("Connections are insecure as SSLContext is null!");
                        }
                    }
                    // 设置channel处理器
                    pipeline.addLast(
                        defaultEventExecutorGroup,
                        new NettyEncoder(), // 编码器——RemotingCommand转字节流byte[]
                        new NettyDecoder(), // 解码器——字节流byte[]转RemotingCommand
                        new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()),
                        new NettyConnectManageHandler(), // 监听netty连接状态,并维护连接信息 —— NettyRemotingClient#channelTables
                        new NettyClientHandler()); // 处理收到的消息
                }
            });

        // 添加定时任务:延迟3秒之后,每隔1秒中执行一次:扫描 responseTable
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    NettyRemotingClient.this.scanResponseTable();
                } catch (Throwable e) {
                    log.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000);

        // 启动netty事件执行器
        if (this.channelEventListener != null) {
            this.nettyEventExecutor.start();
        }
    }

```



## server启动：

```java
    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
            nettyServerConfig.getServerWorkerThreads(),
            new ThreadFactory() {

                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
                }
            });

        prepareSharableHandlers();

        ServerBootstrap childHandler =
            this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
                .childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
                // 监听的端口号
                .localAddress(new InetSocketAddress(this.nettyServerConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                            .addLast(defaultEventExecutorGroup, HANDSHAKE_HANDLER_NAME, handshakeHandler)
                            .addLast(defaultEventExecutorGroup,
                                encoder,                  // 编码器——RemotingCommand转字节流byte[]
                                new NettyDecoder(),       // 解码器——字节流byte[]转RemotingCommand
                                new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()),
                                connectionManageHandler,  // 这个处理器除了打打日志,发个事件,其他没干啥事.
                                serverHandler             // 接收到消息之后,经过上面一系列的消息解码,最终的处理逻辑.
                            );
                    }
                });

        if (nettyServerConfig.isServerPooledByteBufAllocatorEnable()) {
            childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        try {
            // server绑定端口号,并启动.
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            // 设置端口号.
            this.port = ((InetSocketAddress) sync.channel().localAddress()).getPort();
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        }

        if (this.channelEventListener != null) {
            this.nettyEventExecutor.start();
        }

        // 定时任务:延时3秒,每隔1秒执行一次:扫描 responseTable.把其中已经超时的请求,给remove掉.
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    NettyRemotingServer.this.scanResponseTable();
                } catch (Throwable e) {
                    log.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000);
    }

```

## 解析



netty中的使用，这里就不讲了，如果对netty不太了解，那需要先去学习netty框架的使用。

主要逻辑就是设置的：`ChannelHandler`

比如client设置了：

```
                    // 设置channel处理器
                    pipeline.addLast(
                        defaultEventExecutorGroup,
                        new NettyEncoder(), // 编码器——RemotingCommand转字节流byte[]
                        new NettyDecoder(), // 解码器——字节流byte[]转RemotingCommand
                        new IdleStateHandler(0, 0, nettyClientConfig.getClientChannelMaxIdleTimeSeconds()),
                        new NettyConnectManageHandler(), // 监听netty连接状态,并维护连接信息 —— NettyRemotingClient#channelTables
                        new NettyClientHandler()); // 处理收到的消息
```

server设置了：

```
                        ch.pipeline()
                            .addLast(defaultEventExecutorGroup, HANDSHAKE_HANDLER_NAME, handshakeHandler)
                            .addLast(defaultEventExecutorGroup,
                                encoder,                  // 编码器——RemotingCommand转字节流byte[]
                                new NettyDecoder(),       // 解码器——字节流byte[]转RemotingCommand
                                new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()),
                                connectionManageHandler,  // 这个处理器除了打打日志,发个事件,其他没干啥事.
                                serverHandler             // 接收到消息之后,经过上面一系列的消息解码,最终的处理逻辑.
                            );
```

上面都设置了 `NettyEncoder` `NettyDecoder` 编码和解码器，

- 那么通过netty网络框架发送的byte[]字节流，解码成 `RemotingCommand` 就是在 `NettyDecoder` 中。

- 把 `NettyDecoder` 编码成byte[]字节流，就是在 `NettyEncoder` 中。

简单图示：

![image-20211104164833068](images/image-20211104164833068.png)



# NettyEncoder

```java
/**
 * netty消息的编码器
 * 把 {@link RemotingCommand} 编码成 byte[] —— 使用tpc协议在网络上传输的都是byte[]字节流
 *
 * 从netty框架层面:此类实现了 {@link MessageToByteEncoder#encode } 抽象方法,目的:自定义的消息实例转成字节流byte[]
 */
@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
    @Override
    public void encode(ChannelHandlerContext ctx, RemotingCommand remotingCommand, ByteBuf out) throws Exception {
        try {
            // 把请求头转码成 ByteBuffer
            ByteBuffer header = remotingCommand.encodeHeader();
            // 把字节流交给netty框架,让netty去发送
            out.writeBytes(header);
            // 获取请求体的byte[]字节流,
            byte[] body = remotingCommand.getBody();
            if (body != null) {
                // 再把请求体的字节流交给netty,让netty帮忙去发送出去.
                out.writeBytes(body);
            }
        } catch (Exception e) {
            RemotingUtil.closeChannel(ctx.channel());
        }
    }
}
```



# NettyDecoder

```java
/**
 * 使用netty框架,收到tpc协议的数据字节流之后,把字节流转成 {@link RemotingCommand}
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private static final int FRAME_MAX_LENGTH = 
        Integer.parseInt(System.getProperty("com.rocketmq.remoting.frameMaxLength", "16777216"));

    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }

            // 字节的转换.
            ByteBuffer byteBuffer = frame.nioBuffer();

            return RemotingCommand.decode(byteBuffer);
        } catch (Exception e) {
            RemotingUtil.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
        return null;
    }
}
```



> 以上，我们知道了数据包的编码和解码流程。
>
> 上面我们说`RemotingCommand`这个实体，既可以代表`request`的数据包，也可以代表`response`的数据包：
>
> ```java
>     @JSONField(serialize = false)
>     public RemotingCommandType getType() {
>         if (this.isResponseType()) {
>             return RemotingCommandType.RESPONSE_COMMAND;
>         }
> 
>         return RemotingCommandType.REQUEST_COMMAND;
>     }
> 
>     @JSONField(serialize = false)
>     public boolean isResponseType() {
>         int bits = 1 << RPC_TYPE;
>         return (this.flag & bits) == bits;
>     }
> ```
>
> 枚举：
>
> ```java
> public enum RemotingCommandType {
>     REQUEST_COMMAND,
>     RESPONSE_COMMAND;
> }
> ```
>
> 那么`request`的数据包是如何发送的？
>
> server端接收到`request`数据包，是如何处理的？
>
> server端处理完`request`之后，如何给client返回一个响应`response`？
>
> client端收到表示`response`的数据包，又会如何处理？







## request数据包如何发送？

解答这个问题之前，再来看一下RocketMQ中的各个角色：

![image-20211104170727448](images/image-20211104170727448.png)

> 在网络传输数据层面：
>
> - 生产者Producer 作为 client ，使用： NettyRemotingClient
>
> - 消费者Consumer 作为 client ，使用： NettyRemotingClient
>
> - NameServer作为 server，使用：NettyRemotingServer
>
> - 而Broker就比较特殊了，Broker和Producer、Consumer之间进行通信时，作为server端，
>
>   Broker和NameServer之间通信时，作为client端。
>
> 相当于：在Consumer或Producer启动时，使用的remoting远程数据传输层实现是NettyRemotingClient
>
> NameServer启动时，使用的remoting远程数据传输层实现是NettyRemotingServer
>
> 而特殊的Broker都使用了。

这里我们就用Producer和Broker之间，以发送mq业务消息的场景举例子：Producer是client，Broker是server。

### quickstart

```java
public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("my_test_group_name");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        Message msg = new Message("TopicTest", "TagA", ("Hello RocketMQ").getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult sendResult = producer.send(msg);
        System.out.printf("%s%n", sendResult);

        producer.shutdown();
    }
}
```

发送消息，主要就是创建出一个 `Message` 实例对象，包含如下属性：

- topic名称，这里设置的是：`TopicTest`
- tags ，这是设置的是`TagA`
- 消息体。byte[]字节数组。这里设置消息体是 `Hello RocketMQ` 字符串，然后转成byte[]字节数组。

然后使用 `producer.send(msg);` 把消息发送到broker。并且返回发送结果：`SendResult`

### send

org.apache.rocketmq.client.producer.DefaultMQProducer#send(org.apache.rocketmq.common.message.Message)

![image-20211105155522544](images/image-20211105155522544.png)

org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#send(org.apache.rocketmq.common.message.Message)

![image-20211105155545581](images/image-20211105155545581.png)

![image-20211105155604140](images/image-20211105155604140.png)

org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#sendDefaultImpl

下面就是Producer将消息写入到某Broker中的某Queue中，其经历了如下过程：
 - Producer发送消息之前，会先向NameServer发出获取Topic路由信息的请求,NameServer返回该[Topic的路由表]及[Broker列表]

 - Producer根据代码中指定的Queue选择策略，从Queue列表中选出一个队列，用于后续存储消息 详见：{@link  DefaultMQProducerImpl#selectOneMessageQueue}

 - 发送消息：Producer向选择出的Queue所在的Broker发出RPC请求，将消息发送到选择出的Queue

    - 实际发送消息之前，Produer对消息做一些特殊处理，例如，消息压缩、消息本身超过4M会报错

    - ```java
      int compressMsgBodyOverHowmuch = 1024 * 4;       // 默认压缩大于 4k 的消息体。
      int maxMessageSize = 1024 * 1024 * 4;            // 4M  允许的最大消息大小（以字节为单位）。
      ```

 - 网络通信使用netty

![image-20211105155708664](images/image-20211105155708664.png)



### tryToFindTopicPublishInfo

org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#tryToFindTopicPublishInfo

这方法主要是根据broker名称，从NameServer中找到topic的路由表——也就是topic信息及queue都在哪些broker上。

会先从本地缓存中找，如果本地缓存中没有，则连接NameServer查询，查询到之后，在保存到本地缓存中。

![image-20211105161127957](images/image-20211105161127957.png)

#### updateTopicRouteInfoFromNameServer

从方法名中：`从NameServer上查询并修改topic路由信息`。

![image-20211105161337767](images/image-20211105161337767.png)

org.apache.rocketmq.client.impl.MQClientAPIImpl#getTopicRouteInfoFromNameServer

这个方法是使用`remotingClient`发送请求的逻辑。修改本地缓存的逻辑这里没有粘贴出来。

![image-20211105161633903](images/image-20211105161633903.png)

上面使用`remotingClient`连接NameServer，查询到topic的路由信息。

本地缓存是保存到了 `MQClientInstance#topicRouteTable` 这个map中：

```java
/**
 * client端,本地路由表
 * 也可以理解为:路由表的本地缓存.
 * key: topic名称
 * value: TopicRouteData  topic的路由信息
 */
private final ConcurrentMap<String/* Topic */, TopicRouteData> topicRouteTable = new ConcurrentHashMap();
```

具体的处理逻辑自己有兴趣可以去看一看。



### selectOneMessageQueue

org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#selectOneMessageQueue

topic的路由表已经获取到了，无论是从本地缓存中获取的，还是从NameServer中获取的。

**由于topic对应多个queue——默认是一个topic需要创建4个queue。所以需要选择出，这条mq消息到底要发送到其中哪一个queue中的。**

![image-20211105163438145](images/image-20211105163438145.png)

> 画外音：
>
> 对于这个结构图我最开始是有疑问的：为什么TopicA在每个机器上都有Queue0这个队里呢？不应该是`分片`的吗？
>
> 其实有这个误解是因为之前了解过一些Elasticsearch的原理：**[为了分散负载而对分片进行重新分配](https://www.elastic.co/guide/cn/elasticsearch/guide/current/_scale_horizontally.html)**
>
> ![img](images/elas_0204.png)
>
> 但是RocketMQ的原理和Elasticsearch这种`分片`处理方式不一样：
>
> **在broker中，同一个topic，可以在不同的broker中创建，并且其队列数量可以不一样。**也就正如上图看到的这种结构。







### sendKernelImpl

org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#sendKernelImpl





































## server端接收到`request`数据包，是如何处理的？

## server端处理完`request`之后，如何给client返回一个响应`response`？

## client端收到表示`response`的数据包，又会如何处理？

