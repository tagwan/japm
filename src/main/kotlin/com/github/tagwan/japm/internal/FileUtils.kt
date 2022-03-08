package com.github.tagwan.japm.internal

import java.io.*


object FileUtils {

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