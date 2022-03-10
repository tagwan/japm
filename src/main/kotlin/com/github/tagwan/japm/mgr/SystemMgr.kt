package com.github.tagwan.japm.mgr

import org.slf4j.LoggerFactory

object SystemMgr {

    private val properties = System.getProperties()
    private val logger = LoggerFactory.getLogger(SystemMgr::class.java)

    fun init() {

        logger.info("os.name: ${properties.getProperty("os.name")}, " +
            "java.version: ${properties.getProperty("java.version")}, " +
            "java.home: ${properties.getProperty("java.home")}"
        )
    }
}

//键   相关值的描述
//java.version    Java 运行时环境版本
//java.vendor     Java 运行时环境供应商
//java.vendor.url     Java 供应商的 URL
//java.home   Java 安装目录
//java.vm.specification.version   Java 虚拟机规范版本
//java.vm.specification.vendor    Java 虚拟机规范供应商
//java.vm.specification.name  Java 虚拟机规范名称
//java.vm.version     Java 虚拟机实现版本
//java.vm.vendor  Java 虚拟机实现供应商
//java.vm.name    Java 虚拟机实现名称
//java.specification.version  Java 运行时环境规范版本
//java.specification.vendor   Java 运行时环境规范供应商
//java.specification.name     Java 运行时环境规范名称
//java.class.version  Java 类格式版本号
//java.class.path     Java 类路径
//java.library.path   加载库时搜索的路径列表
//java.io.tmpdir  默认的临时文件路径
//java.compiler   要使用的 JIT 编译器的名称
//java.ext.dirs   一个或多个扩展目录的路径
//os.name     操作系统的名称
//os.arch     操作系统的架构
//os.version  操作系统的版本
//file.separator  文件分隔符（在 UNIX 系统中是“/”）
//path.separator  路径分隔符（在 UNIX 系统中是“:”）
//line.separator  行分隔符（在 UNIX 系统中是“/n”）
//user.name   用户的账户名称
//user.home   用户的主目录
//user.dir    用户的当前工作目录