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

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.lontten.canal.service.CanalEventHandler;
import com.lontten.canal.util.TimeUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AbstractCanalClient extends CanalClientContext {
    private static Map<String, CanalEventHandler> esSyncHandleMap;
    @Resource
    private List<CanalEventHandler> esSyncHandleList;


    @PostConstruct
    public void init() {
        esSyncHandleMap = esSyncHandleList.stream()
                .collect(Collectors.toMap(CanalEventHandler::tableName, Function.identity()));
    }


    protected void start() {
        Assert.notNull(connector, "无法根据配置建立链接。");
        thread = new Thread(this::process);

        thread.setUncaughtExceptionHandler(handler);
        running = true;
        thread.start();
    }

    protected void stop() {
        if (!running) {
            return;
        }
        running = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }


    public void process() {
        while (running) {
            int retry = retryCount.incrementAndGet();
            if (maxRetryTimes != -1 && retry > maxRetryTimes) {
                logger.error("canal client 重连失败，超过最大重连次数，退出");
                throw new RuntimeException("canal client 重连失败，超过最大重连次数，退出");
            }
            if (retry == 0) {
                logger.info("canal client 建立连接中.....");
            } else {
                TimeUtil.sleep(1000 * retryInterval);
                logger.info("canal client 第{}次，重连中.....", retry);
            }
            // 创建链接
            try {
                processCore();
            } catch (Exception ignored) {
            }
        }
    }


    protected void processCore() {
        while (running) {
            try {
                connector.connect();
                connector.subscribe();
                logger.info("canal client 订阅中......");
                // 连接成功，重置次数
                retryCount.set(0);
                while (running) {
                    Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                    long batchId = message.getId();
                    List<CanalEntry.Entry> entries = message.getEntries();
                    int size = entries.size();
                    if (batchId == -1 || size == 0) {
                        TimeUtil.sleep(1000L);
                    } else {
//                        System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                        handleEntry(entries);
                    }
                }
            } catch (Throwable e) {
                logger.error("process error!", e);
                TimeUtil.sleep(1000L);

                connector.rollback(); // 处理失败, 回滚数据
            } finally {
                logger.info("canal client,运行异常，释放链接。");
                connector.disconnect();
            }
        }
    }


    private void handleEntry(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry, e);
            }

            CanalEntry.EventType eventType = rowChage.getEventType();
            String schemaName = entry.getHeader().getSchemaName();
            if (!dbName.equals(schemaName)) {
                continue;
            }
            String tableName = entry.getHeader().getTableName();
//            System.out.println(String.format("================ binlog[%s:%s] , name[%s,%s] , eventType : %s",
//                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
//                    dbName, tableName,
//                    eventType));
            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                Long id = getId(eventType, rowData);
                if (id == null) {
                    continue;
                }
                CanalEventHandler handle = esSyncHandleMap.get(tableName);
                if (handle == null) {
                    continue;
                }
                try {
//                    log.info("sync tableName:{},eventType:{},id:{}", tableName, eventType, id);
                    handle.sync(eventType, id, rowData);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("sync error,tableName:{},eventType:{},id:{}", tableName, eventType, id, e);
                }
            }
        }
    }

    private Long getId(CanalEntry.EventType eventType, CanalEntry.RowData row) {
        List<CanalEntry.Column> columnsList = row.getAfterColumnsList();
        if (eventType == CanalEntry.EventType.DELETE) {
            columnsList = row.getBeforeColumnsList();
        }
        for (CanalEntry.Column column : columnsList) {
            if (column.getIsKey()) {
                String value = column.getValue();
                return Long.parseLong(value);
            }
        }
        return null;
    }

}