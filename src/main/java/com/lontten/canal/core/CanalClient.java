/* ------------------------------------------------------------
 *   Copyright 2024 lontten lontten@163.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * -------------------------------------------------------------
 * Project Name    :  canal-spring-boot-starter
 * Project Authors :  lontten   <lontten@163.com>
 * Contributors    :  xxxx   <xx@xx.com>
 *                 |  yyyy   <yy@yy.com>
 * Created On      : <2024-10-15>
 * Last Modified   : <2024-10-15>
 *
 * canal-spring-boot-starter: canal 的 springboot starter 实现
 * ------------------------------------------------------------*/
package com.lontten.canal.core;

import com.alibaba.otter.canal.client.CanalConnectors;
import com.lontten.canal.properties.LonttenCanalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;


@Component
public class CanalClient extends AbstractCanalClient {
    private static final Logger logger = LoggerFactory.getLogger(CanalClient.class);
    @Resource
    private LonttenCanalProperties properties;

    public void config() {
        if (properties.getMaxRetryTimes() != null) {
            CanalClientContext.maxRetryTimes = properties.getMaxRetryTimes();
        }
        if (properties.getRetryInterval() != null) {
            CanalClientContext.retryInterval = properties.getRetryInterval();
        }
        if (properties.getBatchSize() != null) {
            CanalClientContext.batchSize = properties.getBatchSize();
        }

        if (properties.getUsername() != null) {
            CanalClientContext.username = properties.getUsername();
        }
        if (properties.getPassword() != null) {
            CanalClientContext.password = properties.getPassword();
        }


        if (properties.getDestination() != null) {
            CanalClientContext.destination = properties.getDestination();
        }
        if (properties.getDbName() != null) {
            CanalClientContext.dbName = properties.getDbName();
        }


        if (properties.getZkServers() != null) {
            CanalClientContext.canalServeType = 3;
        } else if (properties.getClusters() != null && !properties.getClusters().isEmpty()) {
            CanalClientContext.canalServeType = 2;
        }
    }

    @PostConstruct
    public void init() {
        config();
        switch (canalServeType) {
            case 1:
                logger.info("单机模式");
                initSimpleCanalClient();
                break;
            case 2:
                logger.info("集群模式");
                initClusterCanalClient();
                break;
            case 3:
                logger.info("zookeeper 集群模式");
                initClusterZkCanalClient();
                break;
            default:
                logger.error("canal client 配置错误，请检查配置文件");
        }

        start();
        logger.info("canal client 初始化完成");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("## stop the canal client");
                stop();
            } catch (Throwable e) {
                logger.warn("##something goes wrong when stopping canal:", e);
            } finally {
                logger.info("## canal client is down.");
            }
        }));
        logger.info("canal client 启动完成");
    }

    public void initSimpleCanalClient() {
        var ip = properties.getIp();
        var port = properties.getPort();
        connector = CanalConnectors.newSingleConnector(new InetSocketAddress(ip, port), destination, username, password);
    }


    public void initClusterCanalClient() {
        var list = properties.getClusters()
                .stream()
                .map(v -> new InetSocketAddress(v.getIp(), v.getPort()))
                .toList();
        // 基于固定canal server的地址，建立链接，其中一台server发生crash，可以支持failover
        connector = CanalConnectors.newClusterConnector(list, destination, username, password);
    }

    public void initClusterZkCanalClient() {
        String zkServers = properties.getZkServers();
        // 基于zookeeper动态获取canal server的地址，建立链接，其中一台server发生crash，可以支持failover
        connector = CanalConnectors.newClusterConnector(zkServers, destination, username, password);
    }
}