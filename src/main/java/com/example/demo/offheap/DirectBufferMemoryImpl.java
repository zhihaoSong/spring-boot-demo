package com.example.demo.offheap;

import java.util.LinkedList;
import java.util.List;

public class DirectBufferMemoryImpl {

    private ConcurrentStringObjectDirectHashMap cache;
    private List<String> keys;
    protected long maxSize;

    public DirectBufferMemoryImpl(long maxSize) {

        this.maxSize = maxSize;
        this.cache = new ConcurrentStringObjectDirectHashMap();
        this.keys = new LinkedList<>();
    }

    public <T> T getIfPresent(String key,T defaultVal) {
        Object object = cache.get(key);
        if (object != null) {
            return (T) object;
        }
        return defaultVal;
    }

    public <T> void put(String key, T value) {
        if (cache.keySet().size() < maxSize) { // 缓存还有空间
            cache.put(key, value);
            keys.add(key);
        } else {                       // 缓存空间不足，需要删除一个
            if (cache.containsKey(key)) {
                keys.remove(key);
                cache.put(key, value);
                keys.add(key);
            } else {
                String oldKey = keys.get(0); // 最早缓存的key
                evict(oldKey);               // 删除最早缓存的数据 FIFO算法
                cache.put(key, value);
                keys.add(key);
            }
        }
    }

    public void evict(String key) {
        cache.remove(key);
        keys.remove(key);
    }

}