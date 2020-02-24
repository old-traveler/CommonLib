package com.hyc.commonlib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.graphics.Color

/**
 * @author: 贺宇成
 * @date: 2020-02-24 16:57
 * @desc:
 */
class ButtonLoadView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  private val mPaint: Paint = Paint()
  private val mColor : Int = Color.WHITE
  private val mAlphas : IntArray = IntArray(3)
  private var mAnimator: ValueAnimator? = null
  private var mCurrentIndex : Int = 0

  init {
    mPaint.isAntiAlias = true
    mPaint.color = Color.WHITE
  }

  private fun getRadius(): Int {
    return height.coerceAtLeast(10) shr 1
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    canvas ?: return
    val radius = getRadius().toFloat()
    drawCircle(canvas, radius, radius, 0)
    drawCircle(canvas, (width shr 1).toFloat(), radius, 1)
    drawCircle(canvas, (width - radius), radius, 2)
  }

  private fun drawCircle(canvas: Canvas, x: Float, radius: Float, index : Int) {
    canvas.drawCircle(x, radius, radius, mPaint)
    mPaint.alpha = mAlphas[index]
  }

  fun startLoading() {
    if (mAnimator?.isRunning == true) {
      stopLoading()
    }
    mAlphas[0] = 255
    mAlphas[1] = 85
    mAlphas[2] = 0
    mCurrentIndex = 0
    mAnimator = ValueAnimator.ofInt(255, 0)
    mAnimator!!.duration = 500
    mAnimator!!.repeatCount = INFINITE
    mAnimator!!.repeatMode = ValueAnimator.RESTART
    mAnimator!!.addUpdateListener { animation ->
      if (mAlphas[mCurrentIndex] == 0){
        mCurrentIndex = getNextIndex(mCurrentIndex)
      }
      mAlphas[mCurrentIndex] -= 15
      mAlphas[getNextIndex(mCurrentIndex)] += 10
      mAlphas[getNextIndex(mCurrentIndex + 1)] += 5
      invalidate()
    }
    //启动动画
    mAnimator!!.start()
  }

  private fun getNextIndex(index: Int) : Int{
    return (index + 1) % 3
  }

  fun stopLoading() {
    mAnimator ?: return
    if (mAnimator!!.isRunning) {
      mAnimator!!.cancel()
    }
  }

}