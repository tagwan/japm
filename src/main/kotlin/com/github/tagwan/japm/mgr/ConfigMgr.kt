package com.github.tagwan.japm.mgr

import com.github.tagwan.japm.cfg.MetricsConfig
import com.github.tagwan.japm.cfg.RequireConfig
import com.github.tagwan.japm.cfg.StatisticsConfig
import com.github.tagwan.japm.const.DEFAULT_CFG
import com.github.tagwan.japm.const.PROPERTY_BOOT
import org.slf4j.LoggerFactory

object ConfigMgr {

    lateinit var requireCfg: RequireConfig
    lateinit var metricsCfg: MetricsConfig
    lateinit var statisticsCfg: StatisticsConfig
    private val logger = LoggerFactory.getLogger(ConfigMgr::class.java)

    var packageName: String? = null
    var interfaceName: String? = null
    var baseName: String? = null
    var clazzName: String? = null
    var methodName: String? = null

    fun init() {
        if (System.getProperty(PROPERTY_BOOT) == null) {
            logger.info("启动参数没有指定`$PROPERTY_BOOT`， 启用默认配置~")
            val uri = this.javaClass.classLoader.getResource(DEFAULT_CFG)
            System.setProperty(PROPERTY_BOOT, uri?.path ?: "")
        }
        val path = System.getProperty(PROPERTY_BOOT)
        requireCfg = RequireConfig(path)
        metricsCfg = MetricsConfig(path)
        statisticsCfg = StatisticsConfig(path)

        build()
    }

    private fun build() {
        packageName = requireCfg.packageName.splitCfg()
        interfaceName = requireCfg.interfaceName.splitCfg()
        baseName = requireCfg.baseName.splitCfg()
        clazzName = requireCfg.clazzName.splitCfg()
        methodName = requireCfg.methodName.splitCfg()
        logger.info("指定包名:${packageName ?: "*"}, " +
            "指定接口名:${interfaceName ?: "*"}, " +
            "指定基类名:${baseName ?: "*"}, " +
            "指定类名:${clazzName ?: "*"}, " +
            "指定方法名:${methodName ?: "*"}")
    }

    private fun String?.splitCfg(): String? {
        if (this == null) {
            return null
        }

        val new = this.trim()
        if (new.isBlank() || "*" == new) {
            return null
        }

        return new.replace('.', '/')
    }

    fun validatePackage(name: String): Boolean {
        val str = packageName
            ?: return true
        return name.startsWith(str)
    }

    fun validateBase(name: String?): Boolean {
        val str = baseName
            ?: return true
        return str == name
    }

    fun validateInterface(names: HashSet<String>): Boolean {
        val str = interfaceName
            ?: return true
        return names.contains(str)
    }
}