package com.github.tagwan.japm.const

import com.github.tagwan.japm.collect.ICollect
import com.github.tagwan.japm.collect.impl.AnnotationCollector
import com.github.tagwan.japm.collect.impl.SpringControlCollect
import com.github.tagwan.japm.collect.impl.SpringServiceCollect
import java.util.ArrayList

// 采集器集合
val COLLECTORS: ArrayList<ICollect> = arrayListOf(
    AnnotationCollector()
)

val keys: ArrayList<String> = arrayListOf("server", "key", "secret")

const val INTERFACE = "JampCollector"