package com.hyc.commonlib

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_home.list

/**
 * @author: 贺宇成
 * @date: 2020-01-10 14:17
 * @desc:
 */
class HomeActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    val dataList = mutableListOf<String>()
    dataList.add("高亮工具类")
    dataList.add("拉起微信")
    list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList)
    list.setOnItemClickListener { _, _, position, _ ->
      if (position == 1) {
        openWeChat()
        return@setOnItemClickListener
      }
      val activity = when (position) {
        0 -> MainActivity::class.java
        else -> null
      }
      activity?.let {
        startActivity(Intent(this, it))
      }
    }

  }

  private fun openWeChat() {
    val intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
    if (intent != null) {
      startActivity(intent)
    }
  }

}