# 查看配置的DNS服务器IP

win+r

ipconfig /all

![image-20230306201650576](images/image-20230306201650576.png)





# 使用wireshark监控此适配器：

![image-20230306201836896](images/image-20230306201836896.png)

- ip协议下面是，UDP，也就是说，DNS查询网络请求，是基于UDP协议的。

- DNS（query）协议格式：

  ![image-20230306202020381](images/image-20230306202020381.png)

- DNS（response）协议格式：

  ![image-20230306202226720](images/image-20230306202226720.png)







# TCP协议握手、挥手

![image-20230306202556772](images/image-20230306202556772.png)