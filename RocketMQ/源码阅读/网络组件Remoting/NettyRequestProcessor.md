在 [网络组件Remoting.md](网络组件Remoting.md) 前言部分，我们知道了RocketMQ中各个角色之间要发送各式各样的数据包，其实就是client给server端发送请求，server端去处理请求。

那么对于**server端处理请求这个概念**，声明了一个接口来表示：

# Netty请求处理器

org.apache.rocketmq.remoting.netty.NettyRequestProcessor

```java
public interface NettyRequestProcessor {

    /**
     * 处理请求，并返回响应
     *
     * @param ctx        netty网络I/O通道等上线文
     * @param request    请求
     * @return response  响应
     */
    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception;

    /**
     * 是否拒绝处理请求
     *
     * @return true:拒绝处理此请求.
     */
    boolean rejectRequest();

}
```

从上面接口中，我们看到只有两个方法：

- 处理请求方法 processRequest，参数有两个：一个`ctx`表示网络通道上线文，另一个`request`表示请求。
- `rejectRequest()`  方法，表示是否要拒绝处理此请求。



# NameServer中的请求处理器

在RocketMQ中，NameServer是作为server端的，那么就需要接收client端的请求、处理请求、返回响应，所以就需要有对应的请求处理器。

NameServer中的请求处理器只有一个：org.apache.rocketmq.namesrv.processor.DefaultRequestProcessor

更多，详见文档：[NameServer中的请求处理器](..\NameServer\请求处理器DefaultRequestProcessor.md)

> 画外音：
>
> 其实NameServer的请求处理器还是很好理解的，
>
> 我们只需要理解NameServer需要处理哪些请求就行了.而且不同的请求类型都是有不同的处理逻辑。
>


