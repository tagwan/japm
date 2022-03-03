package com.github.tagwan.japm.collect.impl

import com.github.tagwan.japm.agent.AgentLoader
import com.github.tagwan.japm.collect.AbstractCollect
import com.github.tagwan.japm.collect.ICollect
import javassist.CtClass
import javassist.Modifier

class AnnotationCollector(
    val annoName: String = "com.github.tagwan.test"
)  : AbstractCollect(), ICollect {

    override fun sendStatistic(statistics: Statistics) {
        //pass
    }

    override fun isTarget(className: String, classLoader: ClassLoader, ctClass: CtClass): Boolean {
        try {
            for (obj in ctClass.annotations) {

                // 这里注解如果有值传入，equal不能正确匹配
                if (obj.toString().startsWith("@$annoName")) {
                    return true
                }
            }
        } catch (e: RuntimeException) {

            // 简单记录一下，这里ClassNotFoundException
            System.err.println(e.message)
        }
        return false
    }

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

            val srcBuild = AgentLoader.MethodSrcBuild()
            aloader.updateMethod(method, srcBuild)

        }
        return aloader.toByteCode()
    }

}