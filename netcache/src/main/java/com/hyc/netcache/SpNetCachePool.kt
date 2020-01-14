package com.hyc.netcache

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
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

  private val spVersionCodeKey = "BkNetCacheVersionCode"
  private val spNetCachePrefix = "BkNetCache_"
  private val mLruCache: LruCache<String, NetCacheBean<*>> = LruCache(100)
  private val mSpCache: SharedPreferences
  private val mGosn: Gson = Gson()

  init {
    val versionSp = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    val oldCode = versionSp.getLong(spVersionCodeKey, 0)
    var appVersionCode = 0L
    try {
      val packageInfo = context.applicationContext
        .packageManager
        .getPackageInfo(context.packageName, 0)
      appVersionCode = if (VERSION.SDK_INT >= VERSION_CODES.P) {
        packageInfo.longVersionCode
      } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode.toLong()
      }
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
    }
    if (appVersionCode > oldCode) {
      //app进行了升级，需要删除原来的网络缓存并更新网络缓存版本号
      context.getSharedPreferences("$spNetCachePrefix$oldCode", Context.MODE_PRIVATE).edit().clear()
        .apply()
      versionSp.edit().putLong(spVersionCodeKey, appVersionCode).apply()
    }
    val newCode = appVersionCode.coerceAtLeast(oldCode)
    mSpCache = context.getSharedPreferences("$spNetCachePrefix$newCode", Context.MODE_PRIVATE)
  }

  override fun addNetCache(key: String, any: Any) {
    addNetCache(key, any, getDefaultConfig())
  }

  override fun addNetCache(key: String, any: Any, cacheConfig: CacheConfig) {
    val netCacheBean =
      NetCacheBean(key, System.currentTimeMillis(), cacheConfig, any)
    mLruCache.put(key, netCacheBean)
    mSpCache.edit().putString(key, mGosn.toJson(netCacheBean)).apply()
  }

  private fun getDefaultConfig(): CacheConfig {
    return CacheConfig(Long.MAX_VALUE)
  }

  override fun <T> findNetCache(key: String, callback: (cacheData: T?) -> Unit) {
    callback(findNetCache(key))
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> findNetCache(key: String): T? {
    mLruCache.get(key)?.let {
      return getRealData(it)?.data as? T
    }
    val json = mSpCache.getString(key, null)
    json ?: return null
    try {
      val type = object : TypeToken<NetCacheBean<T>>() {}.type
      val data: NetCacheBean<T>? = mGosn.fromJson(json, type)
      return getRealData(data, false)?.data as? T
    } catch (e: JsonSyntaxException) {
      e.printStackTrace()
    }
    return null
  }

  override fun clearNetCache(key: String) {
    mLruCache.remove(key)
    mSpCache.edit().remove(key).apply()
  }

  override fun clearAllNetCache() {
    mLruCache.evictAll()
    mSpCache.edit().clear().apply()
  }

  private fun getRealData(cacheBean: NetCacheBean<*>?, isSaved: Boolean = true): NetCacheBean<*>? {
    cacheBean ?: return null
    if (isTimeOut(cacheBean)) {
      return null
    }
    if (!isSaved) {
      mLruCache.put(cacheBean.url, cacheBean)
    }
    //返回未超时的网络缓存数据
    return cacheBean
  }

  private fun isTimeOut(cacheBean: NetCacheBean<*>): Boolean {
    return System.currentTimeMillis() - cacheBean.saveTime > cacheBean.cacheConfig.lifeTime
  }

}