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
package com.lontten.canal.util;

import com.alibaba.otter.canal.protocol.CanalEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

@Slf4j
public class CanalUtil {

    @NonNull
    public static CanalEntry.Column getField(List<CanalEntry.Column> list, String fieldName) {
        for (CanalEntry.Column column : list) {
            if (column.getName().equals(fieldName)) {
                return column;
            }
        }
        log.error("CanalUtil getFieldMust 无此 字段：{}", fieldName);
        throw new RuntimeException("CanalUtil getFieldMust 无此 字段：" + fieldName);
    }

    @Nullable
    public static String getFieldValue(List<CanalEntry.Column> list, String fieldName) {
        CanalEntry.Column field = getField(list, fieldName);
        // 当字段为空时，getValue() 返回 空字符串，所以 需要判断一下
        if (field.getIsNull()) {
            return null;
        }
        return field.getValue();
    }

    @Nullable
    public static Long getFieldValueLong(List<CanalEntry.Column> list, String fieldName) {
        CanalEntry.Column field = getField(list, fieldName);
        // 当字段为空时，getValue() 返回 空字符串，所以 需要判断一下
        if (field.getIsNull()) {
            return null;
        }
        return Long.parseLong(field.getValue());
    }
}
