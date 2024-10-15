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
package com.lontten.canal.service.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.lontten.canal.service.CanalEventHandler;
import com.lontten.canal.util.CanalUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
//@Service
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
