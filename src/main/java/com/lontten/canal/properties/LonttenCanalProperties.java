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
package com.lontten.canal.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;


@Data
@ConfigurationProperties(
        prefix = LonttenCanalProperties.LonttenCanalPropertiesProfix,
        ignoreUnknownFields = false
)
public class LonttenCanalProperties {
    public static final String LonttenCanalPropertiesProfix = "lontten.canal";

    /**
     * 目的地，（可以理解成 canal server的监听器的多个实例），canal server 可以配置多个，监听不同的mysql，实现一种对mysql 消息的分组功能
     * 例如：我们有 数据库 ln在线商城(ln_shop)，ln跨境电商(ln_shop2)，ln酒店(ln_hotel)，ln旅游社(ln_travel)
     * 可以配置
     * 目的地 a 然后监听 数据库：ln_shop ln_shop2
     * 目的地 b 然后监听 数据库：ln_hotel ln_travel
     * 。
     * 一般 情况下，按照官方文档，启用 一个 默认名字 example 的 destination，就可以了。
     */
    private String destination;
    /**
     * canal server 账号
     */
    private String username;
    /**
     * canal server 密码
     */
    private String password;


    private String dbName;


    /**
     * 重试间隔，单位秒，默认60秒
     */
    private Long retryInterval;
    /**
     * 最大重试次数,默认-1，无限制
     */
    private Integer maxRetryTimes;
    /**
     * 批量大小，默认 1000
     */
    private Integer batchSize;


    /**
     * canal server ip,默认 127.0.0.1
     */
    private String ip = "127.0.0.1";
    /**
     * canal server 端口,默认 11111
     */
    private Integer port = 11111;


    /**
     * zookeeper 地址
     */
    private String zkServers;

    /**
     * 集群配置
     */
    private ArrayList<Cluster> clusters = new ArrayList<>();

    @Data
    public static class Cluster {
        /**
         * canal server ip
         */
        private String ip;
        /**
         * canal server 端口
         */
        private Integer port;
    }


}
