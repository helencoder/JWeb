package com.helencoder.domain.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
/**
 * ehcache缓存控制器
 *
 * Created by zhenghailun on 2018/5/9.
 */
public class EhcacheUtil {
    private static CacheManager manager = null;
    private static String configFile = "ehcache.xml";
    static{
        try {
            manager = CacheManager.create(EhcacheUtil.class.getClassLoader().getResourceAsStream(configFile));
        } catch (CacheException e) {
            e.printStackTrace();
        }
    }

    public static void put(String cacheName, Serializable key,Serializable value){
        manager.getCache(cacheName).put(new Element(key, value));
    }

    public static Serializable get(String cacheName,Serializable key){
        try {
            Element e = manager.getCache(cacheName).get(key);
            if(e==null)return null;
            return e.getValue();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (CacheException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearCache(String cacheName){
        try {
            manager.getCache(cacheName).removeAll();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static void remove(String cacheName,Serializable key){
        manager.getCache(cacheName).remove(key);
    }
}
