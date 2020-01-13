package com.hyc.netcache

import android.content.Context

/**
 * @author: 贺宇成
 * @date: 2020-01-10 15:10
 * @desc:
 */
object NetCachePoolFactory {

  inline fun newSpNetCachePool(context: () -> Context): NetCachePool {
    return SpNetCachePool(context())
  }

}