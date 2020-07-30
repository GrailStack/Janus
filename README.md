## 启动参数设置
以下三种方式选一种
1.jvm option: -Denv=dev -Dcluster=default -Dlocal=true

2.环境变量设置: ENV=dev
3./opt/settings/server.properties 里设置属性 env=dev
## Spring Cloud转发测试
一方面接收请求，一方面还得作为客户端去发起请求

1.启动Janus-admin保证，下面请求能访问通

http://localhost:8084/admin/menu/manage/allMenu

2.访问网关Server的,进行去前缀转发
http://localhost:8081/janus-admin/admin/menu/manage/allMenu

## 问题
1.启动时注册中心连接失败，是否该停止启动？

2.转发时,后端超时时间设置,返回

3.启动时Load全局Filter时,需要check; done


## 网关Server的监控设计

https://www.cnblogs.com/cjsblog/p/11556029.html


## 备份yml文件


http://api.janus.com/admin1/menu/manage/allMenu3


http://admin.janus.com









