package com.hyc.netcache

/**
 * @author: 贺宇成
 * @date: 2020-01-10 14:44
 * @desc:
 */
interface NetCachePool {

  fun addNetCache(key: String, any: Any, cacheConfig: CacheConfig? = null)

  fun <T> findNetCache(key: String, clazz: Class<T>, callback: (cacheData: T?) -> Unit)

  fun clearNetCache(key: String)

  fun clearAllNetCache()

}