<img src="https://avatars2.githubusercontent.com/u/67695022" alt="janus-logo" width="30%">
================

## 1.What is Janus?(什么是杰纳斯)

杰纳斯(Janus),守护企业IT门户或微服务门户的两面神!如果您觉得Janus不错，欢迎Star！

>更多文档链接:http://janus.xujin.org

## 2.Janus Team

| Github Id | 角色 | github地址|
| -------- | -------- | -------- |
|  SoftwareKing  | PPMC | https://github.com/SoftwareKing|
|  wolfgump  | PPMC| https://github.com/wolfgump |
|  tbkk  | PPMC| https://github.com/tbkk |


## 3.启动测试

### 启动参数设置
以下三种方式选一种
1.jvm option: -Denv=dev -Dcluster=default -Dlocal=true

2.环境变量设置: ENV=dev
3./opt/settings/server.properties 里设置属性 env=dev

### Spring Cloud转发测试

一方面接收请求，一方面还得作为客户端去发起请求

1.启动Janus-admin保证，下面请求能访问通

http://localhost:8084/admin/menu/manage/allMenu

2.访问网关Server的,进行去前缀转发
http://localhost:8081/janus-admin/admin/menu/manage/allMenu


http://api.janus.com/admin1/menu/manage/allMenu3


http://admin.janus.com









