package com.hyc.netcache

import android.content.Context
import android.content.SharedPreferences
import android.util.LruCache
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * @author: 贺宇成
 * @date: 2020-01-13 14:04
 * @desc: 使用SharedPreferences进行网络数据缓存
 */
class SpNetCachePool(context: Context) : NetCachePool {

  private val mLruCache: LruCache<String, NetCacheBean<*>> = LruCache(50)
  private val mSpCache: SharedPreferences =
    context.getSharedPreferences("BkNetCache", Context.MODE_PRIVATE)
  private val mGosn: Gson = Gson()

  override fun addNetCache(key: String, any: Any, cacheConfig: CacheConfig?) {
    val netCacheBean =
      NetCacheBean(key, System.currentTimeMillis(), cacheConfig ?: getDefaultConfig(), any)
    mLruCache.put(key, netCacheBean)
    mSpCache.edit().putString(key, mGosn.toJson(netCacheBean)).apply()
  }

  private fun getDefaultConfig(): CacheConfig {
    return CacheConfig(Long.MAX_VALUE)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> findNetCache(key: String, clazz: Class<T>, callback: (cacheData: T?) -> Unit) {
    mLruCache.get(key)?.let {
      callback(getRealData(it)?.data as? T)
      return
    }
    val json = mSpCache.getString(key, null)
    json?.let {
      try {
        val type = object : TypeToken<NetCacheBean<T>>() {}.type
        val data: NetCacheBean<T>? = mGosn.fromJson(json, type)
        data?.let {
          callback(getRealData(it)?.data as? T)
          return
        }
      } catch (e: JsonSyntaxException) {
        e.printStackTrace()
      }
    }
    callback(null)
  }

  override fun clearNetCache(key: String) {
    mLruCache.remove(key)
    mSpCache.edit().remove(key).apply()
  }

  override fun clearAllNetCache() {
    mLruCache.evictAll()
    mSpCache.edit().clear().apply()
  }

  private fun getRealData(cacheBean: NetCacheBean<*>): NetCacheBean<*>? {
    if (isTimeOut(cacheBean)) {
      return null
    }
    return mLruCache.put(cacheBean.url, cacheBean)
  }

  private fun isTimeOut(cacheBean: NetCacheBean<*>): Boolean {
    return System.currentTimeMillis() - cacheBean.saveTime > cacheBean.cacheConfig.lifeTime
  }

}