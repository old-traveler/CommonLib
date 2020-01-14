package com.hyc.netcache

/**
 * @author: 贺宇成
 * @date: 2020-01-10 14:44
 * @desc:
 */
interface NetCachePool {
  /**
   * 添加或更新一个以key为标识的网络缓存数据，并使用默认的cacheConfig
   */
  fun addNetCache(key: String, any: Any)

  /**
   * 添加或更新一个以key为标识的网络缓存数据
   *
   * @param key 网络缓存标识，建议直接使用url
   * @param any 需被缓存的数据对象
   * @param cacheConfig 缓存配置相关信息，不填即为默认配置
   *
   */
  fun addNetCache(key: String, any: Any, cacheConfig: CacheConfig)

  /**
   * 通过key查找对应的网络缓存数据
   *
   * @param key   网络缓存标识，建议直接使用url
   * @param callback 查询结果回调接口
   *
   */
  fun <T> findNetCache(key: String, callback: (cacheData: T?) -> Unit)

  /**
   * 清除标示为key的网络缓存数据(包括内存和本地缓存数据)
   *
   * @param key 对应添加和查找缓存所使用的标识
   *
   */
  fun clearNetCache(key: String)

  /**
   * 清除所有网络缓存数据(包括内存和本地缓存数据)
   */
  fun clearAllNetCache()

}