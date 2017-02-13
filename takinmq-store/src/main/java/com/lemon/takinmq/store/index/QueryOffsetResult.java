/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lemon.takinmq.store.index;

import java.util.List;

/**
 * 根据索引查询消息，返回结果
 * @author lemon
 */
public class QueryOffsetResult {
    private final List<Long> phyOffsets; //所有数据的物理位移
    private final long indexLastUpdateTimestamp;//最后更新的时间
    private final long indexLastUpdatePhyoffset;//最后更新的物理位移

    public QueryOffsetResult(List<Long> phyOffsets, long indexLastUpdateTimestamp, long indexLastUpdatePhyoffset) {
        this.phyOffsets = phyOffsets;
        this.indexLastUpdateTimestamp = indexLastUpdateTimestamp;
        this.indexLastUpdatePhyoffset = indexLastUpdatePhyoffset;
    }

    public List<Long> getPhyOffsets() {
        return phyOffsets;
    }

    public long getIndexLastUpdateTimestamp() {
        return indexLastUpdateTimestamp;
    }

    public long getIndexLastUpdatePhyoffset() {
        return indexLastUpdatePhyoffset;
    }
}
