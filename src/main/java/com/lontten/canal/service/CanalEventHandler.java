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
package com.lontten.canal.service;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

public interface CanalEventHandler {
    String tableName();

    default void sync(CanalEntry.EventType eventType, Long id, CanalEntry.RowData rowData) {
        switch (eventType) {
            case INSERT:
                insert(id, rowData.getAfterColumnsList());
                break;
            case UPDATE:
                update(id, rowData.getBeforeColumnsList(), rowData.getAfterColumnsList());
                break;
            case DELETE:
                delete(id, rowData.getBeforeColumnsList());
                break;
            default:
                break;
        }
    }

    default void insert(Long id, List<CanalEntry.Column> list) {
    }

    default void update(Long id, List<CanalEntry.Column> oldList, List<CanalEntry.Column> newList) {
    }

    default void delete(Long id, List<CanalEntry.Column> list) {
    }
}
