package com.hyc.netcache

import android.content.Context
import android.util.LruCache
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author: 贺宇成
 * @date: 2020-01-10 15:25
 * @desc:
 */
class FileNetCachePool(context: Context) : NetCachePool {
  private val mLruCache: LruCache<String, NetCacheBean> = LruCache(100)
  private val mThreadPool: ExecutorService = Executors.newCachedThreadPool()
  private val mCacheDir = context.externalCacheDir

  override fun addNetCache(key: String, any: Any, cacheConfig: CacheConfig?) {
    mLruCache.put(
      key,
      NetCacheBean(System.currentTimeMillis(), cacheConfig ?: getDefaultConfig(), any)
    )

  }

  private fun getDefaultConfig(): CacheConfig {
    return CacheConfig(Long.MAX_VALUE)
  }

  override fun <T> findNetCache(key: String, clazz: Class<T>, callback: (cacheData: T?) -> Unit) {

  }

}