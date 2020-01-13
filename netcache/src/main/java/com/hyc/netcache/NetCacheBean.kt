package com.hyc.netcache

/**
 * @author: 贺宇成
 * @date: 2020-01-10 15:55
 * @desc:
 */
data class NetCacheBean<T>(
  val url : String,
  val saveTime : Long,
  val cacheConfig : CacheConfig,
  val data : T
){
  override fun equals(other: Any?): Boolean {
    if (other is NetCacheBean<*>){
      return other.url == url
    }
    return false
  }

  override fun hashCode(): Int {
    var result = url.hashCode()
    result = 31 * result + saveTime.hashCode()
    result = 31 * result + cacheConfig.hashCode()
    result = 31 * result + data.hashCode()
    return result
  }
}