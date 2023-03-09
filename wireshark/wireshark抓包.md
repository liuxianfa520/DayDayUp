# 查看DNS服务器IP

win+r

ipconfig /all

![image-20230306201650576](images/image-20230306201650576.png)







# DNS协议

![image-20230306201836896](images/image-20230306201836896.png)

- ip协议下面是，UDP，也就是说，DNS查询网络请求，是基于UDP协议的。

- DNS（query）协议格式：

  ![image-20230306202020381](images/image-20230306202020381.png)

- DNS（response）协议格式：

  ![image-20230306202226720](images/image-20230306202226720.png)





# TCP协议

## 握手、挥手

![image-20230306202556772](images/image-20230306202556772.png)







## MSL

在 TCP 四次挥手结束后，客户端的端口会处于 TIME_WAIT一段时间(2*MSL)，这期间端口不会被释放，从而导致端口被占满。

MSL(Maximum Segment Lifetime，**报文最大生存时间**)

TCP Segment在网络上的存活时间不会超过MSL（RFC793定义了MSL为2分钟，Linux设置成了30s）

> **如果客户端和服务端的确需要进行多次通信，则开启 keep-alive 是更好的选择**，例如在微服务架构中，通常微服务的使用方和提供方会长期有交流。
>
> **在一些 TPS/QPS 很高的 REST 服务中，如果使用的是短连接（即没有开启keep-alive），则很可能发生客户端端口被占满的情形**。
>
> 这是由于短时间内会创建大量TCP 连接，而在 TCP 四次挥手结束后，客户端的端口会处于 TIME_WAIT一段时间(2*MSL)，
>
> 这期间端口不会被释放，从而导致端口被占满。这种情况下最好使用长连接。



## MTU MSS

![image-20230309134642621](images/image-20230309134642621.png)





## [大量的TIME_WAIT状态的解决办法](https://www.cnblogs.com/soft-engineer/p/15117468.html)







# ICMP协议

ping命令使用的是ICMP协议，从抓包截图中看到，

ICMP协议的request数据包，在IP头部设置的TTL=64

而从下图中，看到reply响应的数据包，TTL=49,

64-49=15，这说明ping命令的请求数据包，在网络中经过了15个网络设备。

![image-20230308205537068](images/image-20230308205537068.png)





ping命令输出：

![image-20230308205926803](images/image-20230308205926803.png)

从抓包看到，由于我们ping的是百度的域名，所以需要先去DNS服务器查询ip地址。DNS请求数据包、DNS响应数据包。

向百度的ip发送了四次数据包，也就对应四组ICMP协议：

![image-20230308205913833](images/image-20230308205913833.png)





















# HTTP协议

## 使用curl命令发送请求（window电脑）

curl -X GET --location "http://localhost:8080/hello?name=1111111111"

![image-20230308213953219](images/image-20230308213953219.png)

对于一个http协议，这是完整的请求流程：

- 由于http基于TCP，所以，先三次握手，建立TCP连接。
- 然后客户端使用TCP数据传输层协议，给服务端发送数据，
  - 发送的数据符合http协议格式。都是在TCP数据body中传输的。
  - 所以http协议，是应用层协议。
- 接收方，也就是server端在收到数据后，会给发送方也就是client发送Seq=1 ACK=100
  - 因为TCP数据包，长度是99，server收到后，进行ACK时，会在长度+1作为ACK的值。
  - 表示可以开始接收第100字节的数据了。
- http服务处理完请求结果后，会给client返回数据。
- client收到返回数据后，会给server发送TCP ACK，表示收到返回的数据了。
- 然后进行四次挥手，断开TCP连接。





## 使用idea 的HTTP Client组件发送请求

### Connection: keep-alive

![image-20230308215943431](images/image-20230308215943431.png)



![image-20230308220114017](images/image-20230308220114017.png)

如果设置 `Connection: keep-alive` ，由于使用完TCP连接之后，client没有把TCP连接断开，

达到tomcat最大连接数的时，并且tomcat的线程都在`RUNNING`时，此时新的client再发送请求，就会被阻塞。





### Connection: close

![image-20230308220613138](images/image-20230308220613138.png)

![image-20230308220730371](images/image-20230308220730371.png)



### Tomcat最大连接数

启动时，tomcat默认连接数是10，

默认10，最小也是10；

![image-20230309144457827](images/image-20230309144457827.png)



```yaml
server:
  tomcat:
  	# 这里设置最大连接数为15
    max-connections: 15
```

当服务端收到的请求，超过10个连接数的时候，会创建新的连接。

![image-20230309160315474](images/image-20230309160315474.png)

- 当任务处理完毕，http的这些线程会 `TIMED_WAITING` 状态，也就是等着有新的http连接过来。

- 一段时间后（问题：这个时间是多久，有什么配置项？）会重新变成10个线程，并且线程状态会变成：`WAITING`

![image-20230309160442818](images/image-20230309160442818.png)

> 画外音：
>
> tomcat这相关原理，抽空还是要学一下。













### 请求未处理完时shutdown server

修改controller接口，让处理慢一点：

```java
    @RequestMapping("hello")
    public String hello(String name) {
        helloService.hello(name);

        ThreadUtil.sleep(10, TimeUnit.SECONDS); // 这里等待10秒钟。

        return "controller hello " + name;
    }
```

发送请求：

```shell
curl -X GET --location "http://localhost:8080/hello?name=anxiaole"
```

当服务端响应未返回的时候，我尝试了两种方式关闭SpringBoot服务，分别进行了抓包：



1）使用kill -9直接杀掉服务端进程，抓包如下：

![image-20230309151748048](images/image-20230309151748048.png)

- 服务端被kill -9 时，server端直接给client发送`RST`报文。告诉client重置TCP连接。
- 此时client使用`新端口号`，尝试与server端重新建立TCP连接——发送`SYN`报文。
- 但是由于此时server端已经被kill，所以服务端不会`ACK`，而是继续返回`RST`。



2）直接把SpringBoot在idea中关掉：

![image-20230309151035875](images/image-20230309151035875.png)

抓包：

![image-20230309151250049](images/image-20230309151250049.png)

> 可以看到，在idea中点击关闭按钮时，
>
> 8080端口所在的服务端，会主动给client发送`FIN`报文，跟client端断开。
>
> 这也相当于优雅关闭。







### 服务端未启动【RST】

当服务端未启动时，端口不存在。

```
curl -X GET --location "http://localhost:8080/hello?name=anxiaole"
```

![image-20230309150035097](images/image-20230309150035097.png)

当目标端口不存在，发送方会收到【RST】类型的TCP报文。

![image-20230309150317809](images/image-20230309150317809.png)

![image-20230309150400020](images/image-20230309150400020.png)

此时发送方会尝试重试几次：

![image-20230309150544983](images/image-20230309150544983.png)

> 画外音：
>
> 想到以前看到的面试题：
>
> ​	如果TCP连接建立后，传输部分数据图中，TCP双方有任意一方kill -9，或直接断电 ，
>
> ​	此时TCP连接会如何处理？
>
> 个人理解：
>
> ​	假设有TCP连接：双方分别用A和B表示：
>
> ​	B被kill -9，则B会给A发送`RST`报文：告诉A，此tcp连接有问题，如果仍然需要TCP连接发送数据，你可以重新创建一个TCP连接。
>
> ​	此时，收到`RST`的A，就会使用一个新的端口号，重新发起TCP三次握手，尝试创建TCP连接。







