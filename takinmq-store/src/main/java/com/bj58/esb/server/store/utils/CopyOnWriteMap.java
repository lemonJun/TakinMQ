package com.bj58.esb.server.store.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 来自于mina项目<a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */

public class CopyOnWriteMap<K, V> implements Map<K, V>, Cloneable, Serializable {
    private static final long serialVersionUID = 788933834504546710L;

    private volatile Map<K, V> internalMap;


    public CopyOnWriteMap() {
        this.internalMap = new HashMap<K, V>();
    }


    @Override
    public synchronized int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.internalMap == null ? 0 : this.internalMap.hashCode());
        return result;
    }


    @Override
    public synchronized boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CopyOnWriteMap other = (CopyOnWriteMap) obj;
        if (this.internalMap == null) {
            if (other.internalMap != null) {
                return false;
            }
        }
        else if (!this.internalMap.equals(other.internalMap)) {
            return false;
        }
        return true;
    }


    public CopyOnWriteMap(final int initialCapacity) {
        this.internalMap = new HashMap<K, V>(initialCapacity);
    }


    public CopyOnWriteMap(final Map<K, V> data) {
        this.internalMap = new HashMap<K, V>(data);
    }


    @Override
    public V put(final K key, final V value) {
        synchronized (this) {
            final Map<K, V> newMap = new HashMap<K, V>(this.internalMap);
            final V val = newMap.put(key, value);
            this.internalMap = newMap;
            return val;
        }
    }


    @Override
    public V remove(final Object key) {
        synchronized (this) {
            final Map<K, V> newMap = new HashMap<K, V>(this.internalMap);
            final V val = newMap.remove(key);
            this.internalMap = newMap;
            return val;
        }
    }


    @Override
    public void putAll(final Map<? extends K, ? extends V> newData) {
        synchronized (this) {
            final Map<K, V> newMap = new HashMap<K, V>(this.internalMap);
            newMap.putAll(newData);
            this.internalMap = newMap;
        }
    }


    @Override
    public void clear() {
        synchronized (this) {
            this.internalMap = new HashMap<K, V>();
        }
    }


    @Override
    public int size() {
        return this.internalMap.size();
    }


    @Override
    public boolean isEmpty() {
        return this.internalMap.isEmpty();
    }


    @Override
    public boolean containsKey(final Object key) {
        return this.internalMap.containsKey(key);
    }


    @Override
    public boolean containsValue(final Object value) {
        return this.internalMap.containsValue(value);
    }


    @Override
    public V get(final Object key) {
        return this.internalMap.get(key);
    }


    @Override
    public Set<K> keySet() {
        return this.internalMap.keySet();
    }


    @Override
    public Collection<V> values() {
        return this.internalMap.values();
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.internalMap.entrySet();
    }


    @Override
    public synchronized String toString() {
        return "CopyOnWriteMap [internalMap=" + this.internalMap + "]";
    }


    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}