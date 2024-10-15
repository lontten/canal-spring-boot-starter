

# canal-spring-boot-starter

<a href="https://gitmoji.dev">
  <img
    src="https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg?style=flat-square"
    alt="Gitmoji"
  />
</a>

#### 组件简介

> 基于 [Canal ](https://github.com/alibaba/canal) 整合的 Starter


> 支持 Spring Boot 2.x
> 
> canal-client 版本 1.1.7

#### 使用说明

##### 1、Spring Boot 项目添加 Maven 依赖

``` xml
<dependency>
    <groupId>io.github.lontten</groupId>
    <artifactId>canal-spring-boot-starter</artifactId>
    <version>2.117.0.RELEASE</version>
</dependency>
```

##### 2、Spring Boot 项目添加 Gradle (Kotlin) 依赖

``` kotlin
implementation("io.github.lontten:canal-spring-boot-starter:2.117.0.RELEASE")
```

##### 3、Spring Boot 项目添加 Gradle 依赖

``` kotlin
implementation 'io.github.lontten:canal-spring-boot-starter:2.117.0.RELEASE'
```

##### 2、使用示例

###### 2.1、根据实际业务需求选择不同的客户端模式

在`application.yml`文件中增加如下配置

```yaml
lontten:
  canal:
    destination: example   # 订阅的 canal 订阅实例
    dbName: demo        # 数据库名字

    retryInterval: 60   # 重试间隔，单位秒，默认60秒
    maxRetryTimes: 10    # 最大重试次数,默认-1，无限制
    batchSize: 1000      # canal client 每次拉取事件数量大小，默认 1000

    username: canal       # canal server 账号
    password: canal       # canal server 密码

    ip: 127.0.0.1       # 单例模式 canal server ip,默认 127.0.0.1
    port: 11111         # 单例模式 canal server 端口,默认 11111

    zkServers: 127.0.0.1:2181   # zk集群模式：zookeeper 地址


```

###### 2.1、CanalEventHandler 消费者模式

创建Service DemoCanalEventHandler，实现实体处理器 CanalEventHandler 接口

```java

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.lontten.canal.service.CanalEventHandler;
import com.lontten.canal.util.CanalUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class DemoCanalEventHandler implements CanalEventHandler {

    @Override
    public String tableName() {
        return "t_demo";
    }

    @Override
    public void insert(Long id, List<CanalEntry.Column> list) {

    }

    @Override
    public void update(Long id, List<CanalEntry.Column> oldList, List<CanalEntry.Column> newList) {
        CanalEntry.Column uidColumn = CanalUtil.getField(oldList, "uid");
        log.info("更新时，是否更新了 uid:{}", uidColumn.getUpdated());
        if (uidColumn.getUpdated()) {
            if (uidColumn.getIsNull()) {
                log.info("将uid 的值 更新为 null");
            }
        }
    }

    @Override
    public void delete(Long id, List<CanalEntry.Column> list) {
    }
}

```
