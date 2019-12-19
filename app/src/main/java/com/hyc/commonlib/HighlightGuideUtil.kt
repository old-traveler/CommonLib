package com.hyc.commonlib

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver.OnDrawListener
import android.widget.ImageView
import android.widget.LinearLayout
import java.lang.IllegalArgumentException

/**
 * @author: 贺宇成
 * @date: 2019-12-17 16:13
 * @desc: 高亮引导工具类
 * 构造方法传入window的decorView
 * onClickListener可不传，传入即可监听"高亮View"的点击事件
 * 对高亮View做了映射转化，在onClick方法中会传入高亮View对应的真实View
 */
class HighlightGuideUtil(
  private val mRootView: ViewGroup,
  private val onClickListener: OnClickListener? = null
) : OnClickListener {

  private var mListeners: MutableList<OnDrawListener> = mutableListOf()
  private val mImageViews: MutableList<ImageView> = mutableListOf()
  private var mGuideView: View? = null
  private val viewMapping: MutableMap<View, View> = mutableMapOf()

  init {
    require(mRootView !is LinearLayout) { "mRootView cannot be LinearLayout" }
  }

  /**
   * @param guideView 引导背景View
   * @param highlightView 需要高亮View 支持多个View同时高亮
   * @param showListener 当引导视图展示时回掉函数
   */
  fun showGuideView(guideView: View, vararg highlightView: View, showListener: () -> Unit) {
    mGuideView = guideView
    require(highlightView.isNotEmpty()) { "must have a highlightView" }
    getViewBitmap(highlightView[0])?.let {
      showGuideViewInternal(*highlightView, showListener = showListener)
      return
    }
    highlightView.forEach { view ->
      val listener = object : OnDrawListener {
        override fun onDraw() {
          if (removeOnDrawListener(view, this) && mListeners.size == 0) {
            mRootView.post {
              showGuideViewInternal(*highlightView, showListener = showListener)
            }
          }
        }
      }
      mListeners.add(listener)
      view.viewTreeObserver.addOnDrawListener(listener)
    }

  }

  /**
   * 移除绘制监听，并且返回结构
   * @return 移除是否成功
   */
  private fun removeOnDrawListener(view: View, listener: OnDrawListener): Boolean {
    val res = mListeners.remove(listener)
    if (res) {
      mRootView.post {
        view.viewTreeObserver.removeOnDrawListener(listener)
      }
    }
    return res
  }

  private fun showGuideViewInternal(
    vararg heightLightView: View,
    showListener: () -> Unit
  ) {
    showListener()
    mRootView.addView(mGuideView)
    heightLightView.forEach { view ->
      addHeightLightView(view)
    }
  }

  /**
   * 添加单个高亮View
   * 1、先获取当前View在window中的位置
   * 2、将View转化成Bitmap（截图）
   * 3、将Bitmap加载到ImageView中并且
   * 设置ImageView的位置为当前View的位置
   * 达到遮盖原有View，假装高亮的效果
   */
  private fun addHeightLightView(view: View) {
    val intAny = IntArray(2)
    view.getLocationInWindow(intAny)
    val imageView = ImageView(mRootView.context)
    mImageViews.add(imageView)
    imageView.setImageBitmap(getViewBitmap(view))
    onClickListener?.let {
      viewMapping[imageView] = view
      imageView.setOnClickListener(this)
    }
    mRootView.addView(imageView)
    (imageView.layoutParams as? MarginLayoutParams)?.let {
      it.leftMargin = intAny[0]
      it.topMargin = intAny[1]
      it.width = view.measuredWidth
      it.height = view.measuredHeight
    }
  }

  /**
   * 更新背景引导View和高亮View
   * 注意：必须要调用showGuideView方法之后调用此方法更新
   * 此方法会清除原因的覆盖物，并且更新背景和新高亮的View
   */
  fun updateHeightLightView(guideView: View? = null, vararg heightLightView: View) {
    mImageViews.forEach {
      mRootView.removeView(it)
    }
    guideView?.let {
      mRootView.removeView(mGuideView)
      mGuideView = it
      mRootView.addView(mGuideView)
    }
    heightLightView.forEach {
      addHeightLightView(it)
    }
  }

  /**
   * 仅更新高亮View
   * 注意：必须要调用showGuideView方法之后调用此方法更新
   * 此方法会先清除原有的高亮View，再添加新的高亮View
   */
  fun updateHeightLightView(vararg heightLightView: View) {
    mImageViews.forEach {
      mRootView.removeView(it)
    }
    heightLightView.forEach {
      addHeightLightView(it)
    }
  }

  private fun getViewBitmap(view: View): Bitmap? {
    val width = view.measuredWidth
    val height = view.measuredHeight
    if (width <= 0 || height <= 0) return null
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    canvas.save()
    return bitmap
  }

  /**
   * 关闭引导和高亮View
   * 并且释放所有资源
   * 注意：调用此方法之后即表示引导结束，不能再调用展示高亮的方法
   */
  fun closeGuideView() {
    mRootView.removeView(mGuideView)
    mImageViews.forEach {
      mRootView.removeView(it)
    }
    mImageViews.clear()
    viewMapping.clear()
    mListeners.clear()
  }

  override fun onClick(v: View?) {
    onClickListener?.onClick(viewMapping[v])
  }

}