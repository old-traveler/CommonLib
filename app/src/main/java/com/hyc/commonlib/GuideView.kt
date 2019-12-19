package com.hyc.commonlib

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * @author: 贺宇成
 * @date: 2019-12-19 16:34
 * @desc:
 */
class GuideView(context : Context) {

  @SuppressLint("InflateParams")
  val mView : View =
    LayoutInflater.from(context).inflate(R.layout.layout_guide_view,null,false)
  val btnView : Button
  private val tvTitle : TextView

  private var index = 0

  fun getNextIndex() = index++

  fun setTitle(title : String?){
    tvTitle.text = title
  }

  init {
    btnView = mView.findViewById(R.id.btn_ok)
    tvTitle = mView.findViewById(R.id.tv_title)
  }







}