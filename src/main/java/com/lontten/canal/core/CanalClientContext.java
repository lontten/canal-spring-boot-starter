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


import com.alibaba.otter.canal.client.CanalConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class CanalClientContext {
    protected final static Logger logger = LoggerFactory.getLogger(CanalClientService.class);
    /**
     * canal服务端类型，1：单机，2：集群,3：集群-zookeeper
     */
    protected static volatile int canalServeType = 1;

    protected volatile boolean running = false;
    protected Thread.UncaughtExceptionHandler handler = (t, e) -> logger.error("parse events has an error", e);
    protected Thread thread = null;
    protected CanalConnector connector;

    public void setConnector(CanalConnector connector) {
        this.connector = connector;
    }

    protected static boolean enableLog = true;

    /**
     * 轮训间隔，默认1秒
     */
    protected static Integer batchInterval = 1;

    /**
     * 轮训批量大小，默认 1000
     */
    protected static Integer batchSize = 1000;


    // 重试间隔，单位秒，默认60秒
    protected static long retryInterval = 60;
    // 重试次数
    protected final AtomicInteger retryCount = new AtomicInteger(-1);
    // 最大重试次数
    protected static int maxRetryTimes = -1;


    protected static String username = "";
    protected static String password = "";


    protected static String destination = "example";
    protected static String dbName;
}