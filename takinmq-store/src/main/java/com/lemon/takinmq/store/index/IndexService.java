package com.lemon.takinmq.store.index;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBComparator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import com.alibaba.fastjson.JSON;
import com.lemon.takinmq.store.DispatchRequest;

/**
 * 使用leveldb做的搜索操作
 * 改用rocksdb实现
 * 
 * 
 * @author WangYazhou
 * @date  2017年2月13日 下午8:02:26
 * @see
 */
public class IndexService {

    private final DB db;

    public IndexService() {
        this.db = init();
    }

    public void destroy() {

    }

    //存储一个消息 
    public void buildIndex(DispatchRequest req) {
        String indexKey = buildKey(req.getTopic(), req.getKeys());
        db.put(indexKey.getBytes(), JSON.toJSONBytes(req));
    }

    //构建索引的key
    private String buildKey(final String topic, final String key) {
        return topic + "#" + key;
    }

    //
    public QueryOffsetResult queryOffset(String topic, String key, int maxNum, long begin, long end) {
        List<Long> phyOffsets = new ArrayList<Long>(maxNum);
        long indexLastUpdateTimestamp = 0;
        long indexLastUpdatePhyoffset = 0;
        //.....
        return new QueryOffsetResult(phyOffsets, indexLastUpdateTimestamp, indexLastUpdatePhyoffset);
    }

    //初始化底层搜索用到的数据库
    private DB init() {
        try {
            //按正序排
            DBComparator comparator = new DBComparator() {
                public int compare(byte[] key1, byte[] key2) {
                    int v1 = Integer.parseInt(new String(key1));
                    int v2 = Integer.parseInt(new String(key2));
                    if (v1 > v2) {
                        return 1;
                    } else if (v1 == v2) {
                        return 0;
                    } else {
                        return -1;
                    }
                }

                public String name() {
                    return "index";
                }

                public byte[] findShortestSeparator(byte[] start, byte[] limit) {
                    return start;
                }

                public byte[] findShortSuccessor(byte[] key) {
                    return key;
                }
            };
            Options options = new Options();
            options.comparator(comparator);
            options.createIfMissing(true);
            DB db = Iq80DBFactory.factory.open(new File("data/index"), options);
            return db;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
