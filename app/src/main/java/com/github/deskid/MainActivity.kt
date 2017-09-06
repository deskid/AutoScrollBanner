package com.github.deskid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = ArrayList<String>()
        list.add("https://pic1.zhimg.com/v2-7cd211af8086f64bf917b9af8e5d75e0.jpg")
        list.add("https://pic2.zhimg.com/v2-ecbf5cd0ebf0a54e5b6d8bddc011b3c1.jpg")
        list.add("https://pic3.zhimg.com/v2-14fef8aaf78a16852970b6940f43e90e.jpg")
        list.add("https://pic2.zhimg.com/v2-7795ae292a7938baed2c0b4e4db5db91.jpg")
        list.add("https://pic3.zhimg.com/v2-e1bd60730d5fc3bbcc0fa5fc6c8c31f6.jpg")

        banner.setImageData(list)
    }
}
