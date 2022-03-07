package com.github.tagwan.japm.internal

import org.slf4j.LoggerFactory
import java.io.*
import java.util.*


object PropertiesUtils {

    private val logger = LoggerFactory.getLogger(PropertiesUtils::class.java)
    private var properties: Properties = Properties()

    fun init() {
        if (System.getProperty("JapmPropFile") == null) {
            logger.info("启动参数没有指定`JapmPropFile`， 启用默认配置")
            val uri = this.javaClass.classLoader.getResource("japm-template.properties")
            System.setProperty("JapmPropFile", uri?.path ?: "")
        }
        val path = System.getProperty("JapmPropFile")
        val bufferedReader = BufferedReader(FileReader(path))
        properties.load(bufferedReader)
    }

    fun getProperty(key: String): String {
        try {
            return properties.getProperty(key)
        } catch (e: Exception) {
            logger.error("key::$key 没有获取到配置值，请检查配置")
            return ""
        }
    }

    @Throws(IOException::class)
    fun openText(path: String = "src/main/resources/banner.txt"): String {
        val file = File(path)
        val fis = FileInputStream(file)
        val isr = InputStreamReader(fis)
        val br = BufferedReader(isr)
        var data: String? = null
        var str = ""
        while (br.readLine().also { data = it } != null) {
            str += "$data\n"
        }
        br.close()
        isr.close()
        fis.close()
        return str
    }


}