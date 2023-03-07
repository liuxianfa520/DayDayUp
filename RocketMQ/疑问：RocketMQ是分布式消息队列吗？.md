~~RocketMQ不是分布式消息队列~~

~~因为：每个queue （topic可以相同，但是queueId不同）只会在唯一一个master的broker上。~~







2023-03-07

今天感觉RocketMQ是分布式消息队列

从外部看，RocketMQ最小数据单位为topic，而一个topic在内部实现上，有由多个queue组成。

每个queue可以在不同的broker上。

也就是对于一个topic的数据，进行分片。



那这就是分布式的概念！！！

