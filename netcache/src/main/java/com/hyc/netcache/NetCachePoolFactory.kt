package com.hyc.netcache

import android.content.Context

/**
 * @author: 贺宇成
 * @date: 2020-01-10 15:10
 * @desc:
 */
class NetCachePoolFactory {

  fun fileNetCachePool(context: Context) : NetCachePool{
    return FileNetCachePool(context)
  }

}