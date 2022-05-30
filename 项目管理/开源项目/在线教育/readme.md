链接：https://pan.baidu.com/s/1_88RU0ikLgkXNk_ltYy2Sw 提取码：8d3n

数据库：链接：https://pan.baidu.com/s/1hn3vmR_zFqwSZIFKGtKfrw 提取码：vk1v













## 1. 技术介绍

核心技术：SpringBoot+mybatis;

前端：layui；

开发工具：idea；

数据库：mysql5.7；

模版引擎：thymeleaf；

安全框架：SpringSecurity；

日志框架：logback；

数据库连接池：druid；

在线编辑器：ckeditor；

图片轮播组件：jQuerySwipeslider；

## 2.功能介绍

本项目分前台用户界面功能和后台管理功能；

前台用户界面功能：

- 滚动大条幅展示重要通知和课程或者活动；
- 展示课程，根据实际业务需求，展示课程推荐，最新课程，免费课程，实战课程；
- 课程搜索，用户输入指定课程关键字，可以搜索查询，也可以根据课程类别分类，和类型进行搜索；
- 课程详细展示
- 用户登陆
- 在线支付

后台管理功能：

- 管理员登录
- 课程管理
- 课程类别管理
- 用户管理
- 授课老师管理
- 订单管理
- 菜单管理
- 友情链接管理
- 系统属性管理
- 自定义帖子管理
- 轮转图片帖子管理

## 3. 前端

### 3.1 首页

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1fqfFWGSMqnW3diaXDXYaNcjss41BHBB0ClQvH7jqpRQbpicTszrYzd5w/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 3.2 课程

提供按照课程的类别，类型以及搜索框进行快速查询相关课程

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1EmaOQBkwTfuxgy8HfgicOicBIhH5n7o73Fkiaxb0TF6yZHwhDHcmZh6qg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

点击任意一门课程，免费课程可以直接观看，vip课程则需要通过支付宝或者微信缴费开通vip进行观看

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1bYhPbO4bgQ3R2LCmsHTOpQxic7LGLapq2rp1PUN6tUE3a3CIWKZMvrw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1ON6TA281L3t5dhr2qIF0pe9JsiagOZBCwOBXS0iaxsoQpia4OHjKLWA3w/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 3.3 登入

学习课程时候需要登录才能观看相关视频资料

![图片](images/640-16538814688554.png)

登入后可以查看个人中心的相关功能

![图片](images/640-16538814688555.png)

在我的订单界面可以查看已经购买的课程

![图片](images/640-16538814688556.png)

### 3.4 商品兑换

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1Mxe5vJX2g5QINajiaXZyFFuKq5UVnE7R3kibBQgPibjjXFB1k4uS1HhsA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 3.5 课程发布

在课程发布页面可以提交发布的课程资料

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1fZJfbG9fnIib3icXLiaaSgfjkBxYCvbauZrXAY9OlJ7ibz8uiaqYFN3FfpA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

在我的发布页面可以查看所有已经发布的课程相关信息，查看审核状态

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1xaNa7dhdSkicnk6lFZLKzfgqDtZntapZianJFWfJdlgPmc9SeictA9GuA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

## 4. 后端

### 4.1 登录

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1ak2QHRXIu4M8z9C4nJvEEMar5yTIwWen8jf7v0icfbZxLEp9C8O4T9A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 4.2 系统管理

包括用户管理，角色管理，菜单管理，可以查看对应的信息并添加，导入，修改或删除

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1lBRJ46PzKTXIuibHB4BcpgIqVSyLYkQdncuzEkNnVuzwv1t0rCNFiaEg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

角色管理界面可以为角色分配权限

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1KuictrVs9BibRmla8ISkOFkajuGKMxPH6NEgDBsJib7GU58lrCUdzCSPA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 4.3 课程管理

可以添加课程，对课程进行分类管理：公共课程，专业课程，免费课程等

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1JEZp3picbqxlAmujNQNBHJCVyzltNLzFguXXiaCmkIen8HtADJ7r7W9g/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1YTLOaXuXiaZLd8sOb81blCicibZy963IXCLOGzIJDNqicumo1nm1GpTJOQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

在类别管理中可以添加课程的分类信息

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw16zYAD3Vibia09ia8TWedEEG2SNqic9iaTSB4H6dAvxVQeSP50Zw6t7p6ffg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

在审核功能处可以对上传的视频进行审核

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1ftby9pbTicFHNlDqNs6uAHzicf1RqSjGzxJaicBG8cpwBOzIQ4Yt4eIiaw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 4.4 教师管理

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1icyHlhcJP6nWuHOXSMdDPnXCCtC7tYiahibA96zYib09oBIiavLfIHCXIyA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 4.5 导航菜单

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1n5uq4WQib82mf4zXC4JLk6MKickicCLI567J20hicjg7qj1Y7eFia54vjdQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 4.6 轮播管理

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1haFVR9B0ZhCSCic57emOUibN07iaLbwKrYVoBRzsORBI9u2kibBX2mFmyQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 4.7 通知管理

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw18THWz0m1NsaAVvr8G4239WgYibyYGumCOez1MniaBicrDY0pBtZTGY55A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 4.8 礼品管理

![图片](https://mmbiz.qpic.cn/mmbiz_png/OwiaX7M4K6dH9R6vmgib5SRTXJWDVfVfw1ibRWkrOkGna3xlMXyGZiabb7oZwQIatuhStibluZa4miapOTUvATthbvGg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)
