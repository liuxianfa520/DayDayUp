# 一、父子类中存在相同字段

dubbo接口的参数如果是javaBean，那么父类存在的属性，子类就不能存在了。

否则在provider方接收不到此属性的值。

比如：父类中存在字段int id;  子类中也存在int id;则在provider方，经过序列化，getId()方法会返回null。



# 二、不要使用 java.sql.Date

dubbo接口的参数中，如果是时间类型，不要使用java.sql.Date

要使用java.util.Date，否则在provider方接收不到此属性的值。





# 三、序列化

dubbo接口的参数都需要实现序列化接口！

