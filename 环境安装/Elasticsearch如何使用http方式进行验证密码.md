# 使用docker安装elk

[github: docker-elk](https://github.com/deviantony/docker-elk/)



# 基于http访问Elasticsearch

```shell
curl 'http://localhost:9200/' \
  -H 'Authorization: Basic ZWxhc3RpYzplbGFzdGlj' \
  --compressed
```

如上，需要把Elasticsearch的账号密码转成 `Authorization`请求头，通过这种方式进行鉴权。

![image-20220729160451264](images/image-20220729160451264.png)