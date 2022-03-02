package com.github.tagwan.japm.collect.impl

import com.github.tagwan.japm.collect.AbstractCollect
import com.github.tagwan.japm.collect.ICollect
import com.github.tagwan.japm.agent.AgentLoader
import com.github.tagwan.japm.agent.AgentLoader.MethodSrcBuild
import javassist.CtClass
import javassist.Modifier


/**
 * 通过@Service注解来查找方法
 */
class SpringServiceCollect : AbstractCollect(), ICollect {
    companion object {
        val INSTANCE = SpringServiceCollect()
        private var beginSrc: String? = null
        private var endSrc: String? = null
        private var errorSrc: String? = null

        init {
            var stringBuilder = StringBuilder()
            stringBuilder.append("com.github.tagwan.japm.collect.impl.SpringServiceCollect instance = ")
            stringBuilder.append("com.github.tagwan.japm.collect.impl.SpringServiceCollect.INSTANCE;\r\n")
            stringBuilder.append("com.github.tagwan.japm.collect.AbstractCollect.Statistics statics = instance.begin(\"%s\", \"%s\");")
            beginSrc = stringBuilder.toString()
            stringBuilder = StringBuilder()
            endSrc = stringBuilder.append("instance.end(stat);").toString()
            stringBuilder = StringBuilder()
            stringBuilder.append("instance.error(stat, e);")
            errorSrc = stringBuilder.toString()
        }
    }

    override fun isTarget(className: String, classLoader: ClassLoader, ctClass: CtClass): Boolean {
        try {
            for (obj in ctClass.annotations) {
                if (obj.toString().startsWith("@org.springframework.stereotype.Service")) {
                    //obj.toString().equals("@org.springframework.stereotype.Service") //这里注解如果有值传入，equal不能正确匹配
                    return true
                }
            }
        } catch (e: ClassNotFoundException) {
            //e.printStackTrace(); // 简单记录一下，这里ClassNotFoundException
            System.err.println(e.message)
        }
        return false
    }

    @Throws(Exception::class)
    override fun transform(
        classLoader: ClassLoader,
        className: String,
        classfileBuffer: ByteArray,
        ctClass: CtClass
    ): ByteArray {
        val aloader = AgentLoader(className, classLoader, ctClass)
        for (method in ctClass.declaredMethods) {

            // 屏蔽非公共方法
            if (!Modifier.isPublic(method.modifiers)) {
                continue
            }

            // 屏蔽静态方法
            if (Modifier.isStatic(method.modifiers)) {
                continue
            }

            if (Modifier.isNative(method.modifiers)) {
                continue
            }

            // 对目标方法插入监听器
            val srcBuild = MethodSrcBuild()
            srcBuild.setBeginSrc(String.format(beginSrc!!, className, method.name))
            srcBuild.setEndSrc(endSrc!!)
            srcBuild.setErrorSrc(errorSrc!!)
            aloader.updateMethod(method, srcBuild)
        }
        return aloader.toByteCode()
    }

    /**
     * 统计信息
     */
    class ServiceStatistics(statistics: Statistics) : Statistics(statistics) {

        // 服务名字
        var serviceName: String? = null

        // 方法名字
        var methodName: String? = null
    }

    override fun begin(className: String?, method: String?): Statistics {
        val serviceStatistics = ServiceStatistics(super.begin(className, method))
        serviceStatistics.serviceName = className
        serviceStatistics.methodName = method
        serviceStatistics.logType = "service"
        return serviceStatistics
    }

    override fun sendStatistic(statistics: Statistics) {
        // pass
    }
}
