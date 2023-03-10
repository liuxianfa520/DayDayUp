# redis客户端连接服务端

这里我使用的是

 ![image-20230310113836883](images/image-20230310113836883.png)

连接的redis服务



redis客户端和服务端之间，使用的是 redis 自定义的一种协议：`RESP`，此协议基于`TCP`协议。

## 先看`RESP`协议交互过程：

![image-20230310114008543](images/image-20230310114008543.png)



## 包含TCP协议的交互过程：

![image-20230310114051101](images/image-20230310114051101.png)

![image-20230310114059972](images/image-20230310114059972.png)





