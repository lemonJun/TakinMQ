/*
 * Copyright (C) 2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iq80.leveldb.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.util.Slice;
import org.iq80.leveldb.util.Slices;

import java.util.List;
import java.util.Map.Entry;

import static com.google.common.collect.Lists.newArrayList;

/**
 * leveldb支持原子操作
 * 就是可以将多个操作放在一个writebatch中打包，执行时，writebatch中所有的写操作打包成一条日志放在log中
 * 这里即使依次往memtable中插入时机器挂了也没事，重启里recover会将log中这个writebatch重做,原子性得到保证
 *
 * @author WangYazhou
 * @date  2017年2月14日 下午12:06:07
 * @see
 */
public class WriteBatchImpl implements WriteBatch {
    private final List<Entry<Slice, Slice>> batch = newArrayList();
    private int approximateSize;

    public int getApproximateSize() {
        return approximateSize;
    }

    public int size() {
        return batch.size();
    }

    @Override
    public WriteBatchImpl put(byte[] key, byte[] value) {
        Preconditions.checkNotNull(key, "key is null");
        Preconditions.checkNotNull(value, "value is null");
        batch.add(Maps.immutableEntry(Slices.wrappedBuffer(key), Slices.wrappedBuffer(value)));
        approximateSize += 12 + key.length + value.length;
        return this;
    }

    public WriteBatchImpl put(Slice key, Slice value) {
        Preconditions.checkNotNull(key, "key is null");
        Preconditions.checkNotNull(value, "value is null");
        batch.add(Maps.immutableEntry(key, value));
        approximateSize += 12 + key.length() + value.length();
        return this;
    }

    @Override
    public WriteBatchImpl delete(byte[] key) {
        Preconditions.checkNotNull(key, "key is null");
        batch.add(Maps.immutableEntry(Slices.wrappedBuffer(key), (Slice) null));
        approximateSize += 6 + key.length;
        return this;
    }

    public WriteBatchImpl delete(Slice key) {
        Preconditions.checkNotNull(key, "key is null");
        batch.add(Maps.immutableEntry(key, (Slice) null));
        approximateSize += 6 + key.length();
        return this;
    }

    @Override
    public void close() {
    }

    /**
     * 这里实现比较有意思，value有值则认为是添加   无值则认为是删除
     * 数据并不会真正删除 而是打上一个删除标记
     * 真正的删除是延时的，因为相同的记录可能会存在memtable immutable table sstable中的
     * @param handler
     */
    public void forEach(Handler handler) {
        for (Entry<Slice, Slice> entry : batch) {
            Slice key = entry.getKey();
            Slice value = entry.getValue();
            if (value != null) {
                handler.put(key, value);
            } else {
                handler.delete(key);
            }
        }
    }

    public interface Handler {
        void put(Slice key, Slice value);

        void delete(Slice key);
    }
}
