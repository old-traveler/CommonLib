package com.hyc.netcache

/**
 * @author: 贺宇成
 * @date: 2020-01-10 15:55
 * @desc:
 */
data class NetCacheBean(
  val saveTime : Long,
  val cacheConfig : CacheConfig,
  val data : Any
)