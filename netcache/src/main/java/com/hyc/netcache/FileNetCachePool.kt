package com.hyc.netcache

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.LruCache
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * @author: 贺宇成
 * @date: 2020-01-10 15:25
 * @desc:
 */
class FileNetCachePool(context: Context) : NetCachePool {
  private val mLruCache: LruCache<String, NetCacheBean> = LruCache(100)
  private val mThreadPool: ExecutorService = ThreadPoolExecutor(
    0, 3,
    10000L, MILLISECONDS,
    LinkedBlockingQueue(100)
  )
  private val mFileCacheTask = FileCacheWriteTask(context.externalCacheDir)
  private val mFileNetCacheReadTasks = mutableMapOf<String, FileNetCacheReadTask<*>>()
  private val mHandler = FileCacheHandler()

  override fun addNetCache(key: String, any: Any, cacheConfig: CacheConfig?) {
    val netCacheBean =
      NetCacheBean(key, System.currentTimeMillis(), cacheConfig ?: getDefaultConfig(), any)
    mLruCache.put(key, netCacheBean)
    mFileCacheTask.offerNetCache(netCacheBean)
    if (!mFileCacheTask.isRunning) {
      mThreadPool.execute(mFileCacheTask)
    }
  }

  private fun getDefaultConfig(): CacheConfig {
    return CacheConfig(Long.MAX_VALUE)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> findNetCache(key: String, clazz: Class<T>, callback: (cacheData: T?) -> Unit) {
    val netCacheBean = mLruCache.get(key)
    netCacheBean?.let {
      val data = if (isTimeOut(netCacheBean)) null else it
      callback(data as T)
      return
    }

    var task: FileNetCacheReadTask<T>? =
      mFileNetCacheReadTasks[key] as? FileNetCacheReadTask<T>
    task?.let {
      //TODO 记录
      return
    }
    task = FileNetCacheReadTask(
      key,
      clazz,
      mHandler
    )
    //TODO 记录
    mFileNetCacheReadTasks[key] = task
    mThreadPool.execute(task)
  }

  private fun isTimeOut(cacheBean: NetCacheBean): Boolean {
    return System.currentTimeMillis() - cacheBean.saveTime > cacheBean.cacheConfig.lifeTime
  }

  internal class FileCacheHandler : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message?) {
      super.handleMessage(msg)
      //分发数据
    }
  }

}

