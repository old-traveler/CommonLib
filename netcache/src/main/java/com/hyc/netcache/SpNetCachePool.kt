package com.hyc.netcache

/**
 * @author: 贺宇成
 * @date: 2020-01-13 14:04
 * @desc:
 */
class SpNetCachePool : NetCachePool{

  override fun addNetCache(key: String, any: Any, cacheConfig: CacheConfig?) {

  }

  override fun <T> findNetCache(key: String, clazz: Class<T>, callback: (cacheData: T?) -> Unit) {

  }


}