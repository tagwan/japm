package com.github.tagwan.japm.collect

import java.io.*
import java.lang.reflect.InvocationTargetException
import java.net.InetAddress
import java.net.URL
import java.net.URLEncoder
import java.net.UnknownHostException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


/**
 * 收集器通用的功能
 *
 * 开始信息、结束信息、异常信息、统计上传信息。
 *
 * 减少对应用系统影响
 * 1. 所有上传在后台线程完成
 * 2. 保证上传所占用的资源是可控的
 * 3. 采样率的配置
 */
abstract class AbstractCollect {
    companion object {

        //统一线程池
        private var threadService: ExecutorService? = null
        private var localIp: String? = null
        private var rejectedCount: Long = 0

        fun getAnnotationValue(key: String, annotationDesc: String?): String? {
            val regex = String.format("value=\\{\".*\"\\}")
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(annotationDesc)
            return if (matcher.find()) {
                matcher.group().substring(key.length + 3, matcher.group().length)
            } else null
        }

        private fun toJson(obj: Any): String {
//        HashMap<String, Object> itemMap = new HashMap<>();
//        itemMap.put("TYPE", false);
//        itemMap.put(JsonWriter.SKIP_NULL_FILELDS,  true);
//        String json  = JsonWriter.objectToJson(obj, itemMap);
//        return json;
            return ""
        }

        fun getMD5(content: String): String {
            try {
                // 生成一个MD5加密计算摘要
                val md = MessageDigest.getInstance("MD5")
                // 计算md5函数
                md.update(content.toByteArray())
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
            return ""
        }

        init {
            // 采样率配置。避免断层
            // 采样率自动调节、手动调节
            // 随机的方式 、轮训方式
            /**
             * 核心线程：50
             * 最大线程：200
             * 最大队列：1000
             */
            threadService = ThreadPoolExecutor(20,
                100,
                20000L,
                TimeUnit.MILLISECONDS,
                object : LinkedBlockingQueue<Runnable?>() {
                    fun rejectedExecution(r: Runnable?, executor: ThreadPoolExecutor) {
                        rejectedCount++
                        System.err.println("update Task rejected from" + executor.toString() + "rejectedCount: " + rejectedCount)
                    }
                }
            )
            try {
                localIp = InetAddress.getLocalHost().hostAddress //9:36'36
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }
        }
    }

    //@NotProguard
    open fun begin(className: String?, method: String?): Statistics {
        val statistics = Statistics()
        statistics.begin = System.currentTimeMillis()
        statistics.createTime = System.currentTimeMillis()
        return statistics
    }

    fun end(stat: Statistics) {
        stat.end = System.currentTimeMillis()
        stat.userTime = stat.end!! - stat.begin!!
        sendStatistic(stat)
    }

    /**
     * 发送统计信息
     * @param statistics
     */
    abstract fun sendStatistic(statistics: Statistics)

    fun error(statistics: Statistics?, throwable: Throwable?) {
        if (statistics != null) {
            statistics.errorMsg = throwable!!.message
            statistics.errorType = throwable.javaClass.name
            if (throwable is InvocationTargetException) {
                statistics.errorType = throwable.targetException.message
                statistics.errorMsg = throwable.targetException.message
            }
        }
        if (throwable != null) {
            sendErrorStackByHttp("", throwable)
        }
    }

    /**
     * 异常堆栈传递
     * @param s
     * @param throwable
     */
    private fun sendErrorStackByHttp(s: String, throwable: Throwable) {}
    protected fun sendStatisticByHttp(statistics: Statistics, type: String) {
        // 发送至监控中心
        statistics.keyId = System.getProperty("\$bit_key")
        execHttp(type, statistics)
    }

    private fun execHttp(type: String, data: Any) {
        val runn = Runnable {
            try {
                val remoteUrl = System.getProperty("\$bit_server")
                //  remoteUrl += "?";
                val key = System.getProperty("\$bit_key")
                val secret = System.getProperty("\$bit_secret")
                val currentTime = System.currentTimeMillis()
                // 计算签名
                var sign = secret + key + type + currentTime + secret
                sign = getMD5(sign.toUpperCase())
                var params = ""
                params += "type=$type"
                params += "&sign=$sign"
                params += "&key=$key"
                params += "&time=$currentTime"
                params += "&data=" + URLEncoder.encode(toJson(data), "UTF-8")
                val url = URL(remoteUrl)
                var out: PrintWriter? = null
                var `in`: BufferedReader? = null
                var result = ""
                try {
                    val realUrl = URL(remoteUrl)
                    // 打开和Url之间的链接
                    val conn = realUrl.openConnection()
                    // 设置通用的请求属性
                    conn.setRequestProperty("accept", "*/*")
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    conn.useCaches = false
                    // 发送POST必须设置下面两行
                    conn.doOutput = true
                    conn.doInput = true
                    conn.connectTimeout = 5000
                    conn.readTimeout = 5000

                    // 获取URLConnection对象对应的输出流
                    out = PrintWriter(OutputStreamWriter(conn.getOutputStream(), "UTF-8"))
                    // 发送请求参数
                    out.print(params)
                    // flush输出流的缓冲
                    out.flush()
                    // 定义BufferedReader输入流来读取URL的相应
                    `in` = BufferedReader(InputStreamReader(conn.getInputStream(), "UTF-8"))
                    var line: String
                    while (`in`.readLine().also { line = it } != null) {
                        result += line
                    }
                    if ("ok" != result) {
                        println("bit apm upload fail :$result")
                    }
                } catch (e: Exception) {
                    throw RuntimeException("上传失败")
                } finally {
                    try {
                        out?.close()
                        `in`?.close()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        threadService!!.execute(runn)
    }

    /**
     * 统计信息
     */
    open class Statistics {
        var begin: Long? = null
        var end: Long? = null
        var userTime: Long? = null
        var errorMsg: String? = null
        var errorType: String? = null
        var createTime: Long? = null
        var keyId: String? = null
        var ip = localIp
        var logType: String? = null

        constructor()

        constructor(copy: Statistics) {
            begin = copy.begin
            end = copy.end
            createTime = copy.createTime
            errorMsg = copy.errorMsg
            errorType = copy.errorType
            keyId = copy.keyId
            ip = copy.ip
            logType = copy.logType
            userTime = copy.userTime
        }

        fun toJsonString(): String {
            val sb = StringBuilder("{")
            if (begin != null) sb.append("\"begin\": ").append(begin)
            if (end != null) sb.append("\"end\": ").append(end)
            if (errorMsg != null) sb.append("\"errorMsg\": ").append(errorMsg)
            if (errorType != null) sb.append("\"errorType\": ").append(errorType)
            if (createTime != null) sb.append("\"createTime\": ").append(createTime)
            if (sb.substring(1, 2) == ",") sb.delete(1, 2)
            sb.append("}")
            return sb.toString()
        }
    }
}
