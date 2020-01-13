package com.hyc.netcache.file.cache

import com.hyc.netcache.NetCacheBean
import java.io.File
import java.util.LinkedList
import java.util.Queue

/**
 * @author: 贺宇成
 * @date: 2020-01-11 13:08
 * @desc:
 */
class FileCacheWriteTask(val mCacheDir: File?) : Runnable {
  private val mQueue: Queue<NetCacheBean<*>> = LinkedList()
  @Volatile var isRunning = false
    private set

  fun offerNetCache(netCacheBean: NetCacheBean<*>) {
    if (mQueue.contains(netCacheBean)) {
      synchronized(mQueue) {
        mQueue.remove(netCacheBean)
      }
    }
    mQueue.offer(netCacheBean)
  }

  private fun pollNetCache(): NetCacheBean<*>? {
    val netCacheBean: NetCacheBean<*>
    synchronized(mQueue) {
      netCacheBean = mQueue.poll()
    }
    return netCacheBean
  }

  override fun run() {
    var netCacheBean: NetCacheBean<*>?
    isRunning = true
    while ({ netCacheBean = pollNetCache();netCacheBean }() != null) {

    }
    isRunning = false
  }

}