# snow-flake-zk-spring-boot-starter
## 使用方法
```xml
<dependency>
   <groupId>com.github.maojx0630</groupId>
   <artifactId>snow-flake-zk-spring-boot-starter</artifactId>
   <version>0.1</version>
</dependency>
```
配置zk地址
```yaml
zk:
  connect: 127.0.0.1:2181
```
调用IdUtils的next或nextStr即可
##注意
* 若启动时zk不可用,且开启探测则会使用默认workerId
* [所有配置信息](https://github.com/maojx0630/snow-flake-zk-spring-boot-starter/blob/master/src/main/java/com/github/maojx0630/snowFlakeZk/ZookeeperConfig.java)