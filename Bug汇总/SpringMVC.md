一、SpringMVC的接口定义如下:

```java
@RequestMapping("/update.json")
@ResponseBody
public Boolean update(UserEntity user, Integer entId) {
    
}
```

- 如果使用GET请求，把参数全都拼接到url后面，则spring可以成功绑定参数。

- 如果使用POST请求，
  - 使用form表单传参，则spring可以成功绑定参数。
  - 把参数转成jsonString放到requestBody中传参，则spring并不能成功进行参数绑定。（entId会为null）





二、

