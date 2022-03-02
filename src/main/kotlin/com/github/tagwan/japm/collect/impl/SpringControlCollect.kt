package com.github.tagwan.japm.collect.impl

import com.github.tagwan.japm.collect.AbstractCollect
import com.github.tagwan.japm.collect.ICollect
import com.github.tagwan.japm.loader.AgentLoader
import com.github.tagwan.japm.loader.AgentLoader.MethodSrcBuild
import javassist.CtClass
import javassist.CtMethod
import javassist.Modifier
import java.util.*


/**
 * demo 这里并没有完全实现
 * 采集目标： 开始时间、结束时间、用时、类名、方法名、URL地址(可以采集servlet，不在controller层)
 */
class SpringControlCollect : AbstractCollect(), ICollect {
    private var requestUrl: String? = null

    companion object {
        val INSTANCE = SpringControlCollect()
        private var beginSrc: String? = null
        private var endSrc: String? = null
        private var errorSrc: String? = null

        init {
            var stringBuilder = StringBuilder()
            stringBuilder.append("com.github.tagwan.japm.collect.impl.SpringControlCollect instance = ")
            stringBuilder.append("com.github.tagwan.japm.collect.impl.SpringControlCollect.INSTANCE;\r\n")
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
            val count = Arrays.stream(ctClass.annotations)
                .filter { obj: Any ->
                    obj.toString().startsWith("@org.springframework.stereotype.Controller")
                }.count()
            if (count > 0) return true
        } catch (e: ClassNotFoundException) {
            // 工程依赖另外的jar，找不到，正常情况
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

            // 屏蔽本地方法
            if (Modifier.isNative(method.modifiers)) {
                continue
            }

            // 必须带上RequestMapping注解
            if (getRequestMappingValue(method).also { requestUrl = it } == null) {
                continue  //10-40'55''
            }
            val srcBuild = MethodSrcBuild()
            srcBuild.setBeginSrc(String.format(beginSrc!!, className, method.name))
            srcBuild.setEndSrc(endSrc!!)
            srcBuild.setErrorSrc(errorSrc!!)
            aloader.updateMethod(method, srcBuild)
        }
        return aloader.toByteCode()
    }

    private fun getRequestMappingValue(method: CtMethod): String {
        return ""
    }

    override fun sendStatistic(statistics: Statistics) {}
}
