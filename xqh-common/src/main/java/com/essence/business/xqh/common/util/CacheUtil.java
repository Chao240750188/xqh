package com.essence.business.xqh.common.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 缓存工具类
 * 
 * @author NoBugNoCode
 *
 * 2019年9月25日 下午5:28:26
 */
public class CacheUtil {
    private static final String path = "ehcache.xml";
    private static CacheManager manager = CacheManager.create(CacheUtil.class.getClassLoader().getResourceAsStream("ehcache.xml"));

    private CacheUtil() {
    }

    public static boolean save(String cacheName, Object key, Object value) {
        Cache cache = manager.getCache(cacheName);
        if (cache.isElementInMemory(key)) {
            return false;
        } else {
            Element element = new Element(key, value);
            cache.put(element);
            return true;
        }
    }

    public static boolean saveOrUpdate(String cacheName, Object key, Object value) {
        Cache cache = manager.getCache(cacheName);
        Element element = new Element(key, value);
        cache.put(element);
        return true;
    }

    public static Object get(String cacheName, Object key) {
        Cache cache = manager.getCache(cacheName);
        if (cache.isElementInMemory(key)) {
            Element element = cache.get(key);
            return null == element ? null : element.getObjectValue();
        } else {
            return null;
        }
    }

    public static Cache getCache(String cacheName) {
        return manager.getCache(cacheName);
    }

    public static boolean delete(String cacheName, Object key) {
        Cache cache = manager.getCache(cacheName);
        return cache.remove(key);
    }

    public static void clear() {
        manager.clearAll();
    }

    static {
        if (null == manager) {
            throw new IllegalArgumentException("初始化缓存失败，请确认配置文件位置！");
        }
    }
}
