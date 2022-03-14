package com.github.tagwan.japm.internal

import com.sun.management.OperatingSystemMXBean
import org.apache.log4j.Logger
import java.io.*
import java.lang.management.ManagementFactory
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap


object ComputerMonitorUtil {
    private val osName = System.getProperty("os.name")
    private const val CPUTIME = 500
    private const val PERCENT = 100
    private const val FAULTLENGTH = 10
    private val logger = Logger.getLogger(ComputerMonitorUtil::class.java)
    //睡500ms
// 取进程信息// 如果是window系统
    /**
     * 功能：获取Linux和Window系统cpu使用率
     */
    val cpuUsage: Double
        get() {
            // 如果是window系统
            if (osName.toLowerCase().contains("windows")
                    || osName.toLowerCase().contains("win")) {
                return try {
                    val procCmd = (System.getenv("windir")
                            + "//system32//wbem//wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount")
                    // 取进程信息
                    val c0 = readCpu(Runtime.getRuntime().exec(procCmd)) //第一次读取CPU信息
                    Thread.sleep(CPUTIME.toLong()) //睡500ms
                    val c1 = readCpu(Runtime.getRuntime().exec(procCmd)) //第二次读取CPU信息
                    if (c0 != null && c1 != null) {
                        val idletime = c1[0] - c0[0] //空闲时间
                        val busytime = c1[1] - c0[1] //使用时间
                        val cpusage = java.lang.Double.valueOf(PERCENT * busytime * 1.0 / (busytime + idletime))
                        val b1 = BigDecimal(cpusage)
                        b1.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                    } else {
                        0.0
                    }
                } catch (ex: Exception) {
                    logger.debug(ex)
                    0.0
                }
            } else {
                try {
                    val map1 = cpuinfo()
                    Thread.sleep(CPUTIME.toLong())
                    val map2 = cpuinfo()
                    val user1 = map1["user"].toString().toLong()
                    val nice1 = map1["nice"].toString().toLong()
                    val system1 = map1["system"].toString().toLong()
                    val idle1 = map1["idle"].toString().toLong()
                    val user2 = map2["user"].toString().toLong()
                    val nice2 = map2["nice"].toString().toLong()
                    val system2 = map2["system"].toString().toLong()
                    val idle2 = map2["idle"].toString().toLong()
                    val total1 = user1 + system1 + nice1
                    val total2 = user2 + system2 + nice2
                    val total = total2 - total1.toFloat()
                    val totalIdle1 = user1 + nice1 + system1 + idle1
                    val totalIdle2 = user2 + nice2 + system2 + idle2
                    val totalidle = totalIdle2 - totalIdle1.toFloat()
                    val cpusage = total / totalidle * 100
                    val b1 = BigDecimal(cpusage.toDouble())
                    return b1.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                } catch (e: InterruptedException) {
                    logger.debug(e)
                }
            }
            return 0.toDouble()
        }

    /**
     * 功能：Linux CPU使用信息
     */
    fun cpuinfo(): Map<*, *> {
        var inputs: InputStreamReader? = null
        var buffer: BufferedReader? = null
        val map: MutableMap<String, Any> = HashMap()
        try {
            inputs = InputStreamReader(FileInputStream("/proc/stat"))
            buffer = BufferedReader(inputs)
            var line: String? = ""
            while (true) {
                line = buffer.readLine()
                if (line == null) {
                    break
                }
                if (line.startsWith("cpu")) {
                    val tokenizer = StringTokenizer(line)
                    val temp: MutableList<String> = ArrayList()
                    while (tokenizer.hasMoreElements()) {
                        val value = tokenizer.nextToken()
                        temp.add(value)
                    }
                    map["user"] = temp[1]
                    map["nice"] = temp[2]
                    map["system"] = temp[3]
                    map["idle"] = temp[4]
                    map["iowait"] = temp[5]
                    map["irq"] = temp[6]
                    map["softirq"] = temp[7]
                    map["stealstolen"] = temp[8]
                    break
                }
            }
        } catch (e: Exception) {
            logger.debug(e)
        } finally {
            try {
                buffer!!.close()
                inputs!!.close()
            } catch (e2: Exception) {
                logger.debug(e2)
            }
        }
        return map
    }// 总的物理内存+虚拟内存
    // 剩余的物理内存
    /**
     * 功能：Linux 和 Window 内存使用率
     */
    val memUsage: Double
        get() {
            if (osName.toLowerCase().contains("windows")
                    || osName.toLowerCase().contains("win")) {
                try {
                    val osmxb = ManagementFactory
                            .getOperatingSystemMXBean() as OperatingSystemMXBean
                    // 总的物理内存+虚拟内存
                    val totalvirtualMemory = osmxb.totalSwapSpaceSize
                    // 剩余的物理内存
                    val freePhysicalMemorySize = osmxb.freePhysicalMemorySize
                    val usage = (1 - freePhysicalMemorySize * 1.0 / totalvirtualMemory) * 100
                    val b1 = BigDecimal(usage)
                    return b1.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                } catch (e: Exception) {
                    logger.debug(e)
                }
            } else {
                val map: MutableMap<String, Any> = HashMap()
                var inputs: InputStreamReader? = null
                var buffer: BufferedReader? = null
                try {
                    inputs = InputStreamReader(FileInputStream("/proc/meminfo"))
                    buffer = BufferedReader(inputs)
                    var line: String? = ""
                    while (true) {
                        line = buffer.readLine()
                        if (line == null) break
                        var beginIndex = 0
                        var endIndex = line.indexOf(":")
                        if (endIndex != -1) {
                            val key = line.substring(beginIndex, endIndex)
                            beginIndex = endIndex + 1
                            endIndex = line.length
                            val memory = line.substring(beginIndex, endIndex)
                            val value = memory.replace("kB", "").trim { it <= ' ' }
                            map[key] = value
                        }
                    }
                    val memTotal = map["MemTotal"].toString().toLong()
                    val memFree = map["MemFree"].toString().toLong()
                    val memused = memTotal - memFree
                    val buffers = map["Buffers"].toString().toLong()
                    val cached = map["Cached"].toString().toLong()
                    val usage = (memused - buffers - cached).toDouble() / memTotal * 100
                    val b1 = BigDecimal(usage)
                    return b1.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                } catch (e: Exception) {
                    logger.debug(e)
                } finally {
                    try {
                        buffer!!.close()
                        inputs!!.close()
                    } catch (e2: Exception) {
                        logger.debug(e2)
                    }
                }
            }
            return 0.0
        }// df -hl 查看硬盘空间
    // 保留2位小数

    /**
     * Window 和Linux 得到磁盘的使用率
     *
     * @return
     * @throws Exception
     */
    @get:Throws(Exception::class)
    val diskUsage: Double
        get() {
            var totalHD = 0.0
            var usedHD = 0.0
            return if (osName.toLowerCase().contains("windows")
                    || osName.toLowerCase().contains("win")) {
                var allTotal: Long = 0
                var allFree: Long = 0
                var c = 'A'
                while (c <= 'Z') {
                    val dirName = "$c:/"
                    val win = File(dirName)
                    if (win.exists()) {
                        allTotal += win.totalSpace
                        allFree += win.freeSpace
                    }
                    c++
                }
                var precent = (1 - allFree * 1.0 / allTotal) * 100
                val b1 = BigDecimal(precent)
                precent = b1.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                precent
            } else {
                val rt = Runtime.getRuntime()
                val p = rt.exec("df -hl") // df -hl 查看硬盘空间
                var `in`: BufferedReader? = null
                try {
                    `in` = BufferedReader(InputStreamReader(p.inputStream))
                    var str: String? = null
                    var strArray: Array<String>? = null
                    while (`in`.readLine().also { str = it } != null) {
                        var m = 0
                        strArray = str!!.split(" ").toTypedArray()
                        for (tmp in strArray) {
                            if (tmp.trim { it <= ' ' }.length == 0) continue
                            ++m
                            if (tmp.indexOf("G") != -1) {
                                if (m == 2) {
                                    if (tmp != "" && tmp != "0") totalHD += tmp.substring(0, tmp.length - 1).toDouble() * 1024
                                }
                                if (m == 3) {
                                    if (tmp != "none" && tmp != "0") usedHD += tmp.substring(0, tmp.length - 1).toDouble() * 1024
                                }
                            }
                            if (tmp.indexOf("M") != -1) {
                                if (m == 2) {
                                    if (tmp != "" && tmp != "0") totalHD += tmp.substring(0, tmp.length - 1).toDouble()
                                }
                                if (m == 3) {
                                    if (tmp != "none" && tmp != "0") usedHD += tmp.substring(0, tmp.length - 1).toDouble()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    logger.debug(e)
                } finally {
                    `in`!!.close()
                }
                // 保留2位小数
                var precent = usedHD / totalHD * 100
                val b1 = BigDecimal(precent)
                precent = b1.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                precent
            }
        }

    // window读取cpu相关信息
    private fun readCpu(proc: Process): LongArray? {
        val retn = LongArray(2)
        try {
            proc.outputStream.close()
            val ir = InputStreamReader(proc.inputStream)
            val input = LineNumberReader(ir)
            var line = input.readLine()
            if (line == null || line.length < FAULTLENGTH) {
                return null
            }
            val capidx = line.indexOf("Caption")
            val cmdidx = line.indexOf("CommandLine")
            val rocidx = line.indexOf("ReadOperationCount")
            val umtidx = line.indexOf("UserModeTime")
            val kmtidx = line.indexOf("KernelModeTime")
            val wocidx = line.indexOf("WriteOperationCount")
            var idletime: Long = 0
            var kneltime: Long = 0 //读取物理设备时间
            val usertime: Long = 0 //执行代码占用时间
            while (input.readLine().also { line = it } != null) {
                if (line!!.length < wocidx) {
                    continue
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount
                val caption = substring(line, capidx, cmdidx - 1).trim { it <= ' ' }
                // System.out.println("caption:"+caption);
                val cmd = substring(line, cmdidx, kmtidx - 1).trim { it <= ' ' }
                // System.out.println("cmd:"+cmd);
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue
                }
                val s1 = substring(line, kmtidx, rocidx - 1).trim { it <= ' ' }
                val s2 = substring(line, umtidx, wocidx - 1).trim { it <= ' ' }
                val digitS1 = getDigit(s1)
                val digitS2 = getDigit(s2)

// System.out.println("s1:"+digitS1.get(0));
// System.out.println("s2:"+digitS2.get(0));
                if (caption == "System Idle Process" || caption == "System") {
                    if (s1.length > 0) {
                        if (digitS1[0] != "" && digitS1[0] != null) {
                            idletime += java.lang.Long.valueOf(digitS1[0]).toLong()
                        }
                    }
                    if (s2.length > 0) {
                        if (digitS2[0] != "" && digitS2[0] != null) {
                            idletime += java.lang.Long.valueOf(digitS2[0]).toLong()
                        }
                    }
                    continue
                }
                if (s1.length > 0) {
                    if (digitS1[0] != "" && digitS1[0] != null) {
                        kneltime += java.lang.Long.valueOf(digitS1[0]).toLong()
                    }
                }
                if (s2.length > 0) {
                    if (digitS2[0] != "" && digitS2[0] != null) {
                        kneltime += java.lang.Long.valueOf(digitS2[0]).toLong()
                    }
                }
            }
            retn[0] = idletime
            retn[1] = kneltime + usertime
            return retn
        } catch (ex: Exception) {
            logger.debug(ex)
        } finally {
            try {
                proc.inputStream.close()
            } catch (e: Exception) {
                logger.debug(e)
            }
        }
        return null
    }

    /**
     * 从字符串文本中获得数字
     *
     * @param text
     * @return
     */
    private fun getDigit(text: String): List<String?> {
        val digitList: MutableList<String?> = ArrayList()
        digitList.add(text.replace("\\D".toRegex(), ""))
        return digitList
    }

    /**
     * 由于String.subString对汉字处理存在问题（把一个汉字视为一个字节)，因此在 包含汉字的字符串时存在隐患，现调整如下：
     *
     * @param src
     * 要截取的字符串
     * @param start_idx
     * 开始坐标（包括该坐标)
     * @param end_idx
     * 截止坐标（包括该坐标）
     * @return
     */
    private fun substring(src: String?, start_idx: Int, end_idx: Int): String {
        val b = src!!.toByteArray()
        var tgt = ""
        for (i in start_idx..end_idx) {
            tgt += b[i].toChar()
        }
        return tgt
    }
}

fun main(args: Array<String>) {
    val cpuUsage = ComputerMonitorUtil.cpuUsage
    // 当前系统的内存使用率
    val memUsage = ComputerMonitorUtil.memUsage
    // 当前系统的硬盘使用率
    val diskUsage = ComputerMonitorUtil.diskUsage
    println("cpuUsage:$cpuUsage")
    println("memUsage:$memUsage")
    println("diskUsage:$diskUsage")
}