package com.hyc.commonlib

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.btn_6
import kotlinx.android.synthetic.main.activity_main.tv_1
import kotlinx.android.synthetic.main.activity_main.tv_2
import kotlinx.android.synthetic.main.activity_main.tv_3
import kotlinx.android.synthetic.main.activity_main.tv_4
import kotlinx.android.synthetic.main.activity_main.tv_5

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initHighlightView()
  }

  private fun initHighlightView() {
    val guideView = GuideView(this)
    val guideUtil = HighlightGuideUtil(window.decorView as ViewGroup)
    guideUtil.showGuideView(guideView.mView, tv_1,tv_2,tv_3,tv_4,tv_5,btn_6) {
      guideView.setTitle("这个是按钮1")
      guideView.btnView.setOnClickListener {
        val index = guideView.getNextIndex()
        when (index) {
          0 -> {
            guideView.setTitle("这个是按钮2")
            guideUtil.updateHeightLightView(tv_2)
          }
          1 -> {
            guideView.setTitle("这个是按钮3")
            guideUtil.updateHeightLightView(tv_3)
          }
          2 -> {
            guideView.setTitle("这个是按钮4")
            guideUtil.updateHeightLightView(guideView.mView, heightLightView = *arrayOf(tv_4))
          }
          3 -> {
            guideView.setTitle("这个是按钮5")
            guideUtil.updateHeightLightView(tv_5)
          }
          4 -> {
            guideView.btnView.text = "完成"
            guideView.setTitle("这个是按钮6")
            guideUtil.updateHeightLightView(btn_6)
          }
          5 -> guideUtil.closeGuideView()
        }
      }
    }
  }
}
