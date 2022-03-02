package com.github.tagwan.japm

import com.github.tagwan.japm.collect.ICollect
import com.github.tagwan.japm.collect.impl.SpringControlCollect
import com.github.tagwan.japm.collect.impl.SpringServiceCollect
import javassist.ClassPool
import javassist.LoaderClassPath
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.IllegalClassFormatException
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * 入口方法
 *
 * @data 2022/03/02
 * @author jdg
 */
open class AgentMain : ClassFileTransformer {

    private val classPoolMap: MutableMap<ClassLoader, ClassPool> = ConcurrentHashMap()

    companion object {

        protected var agentMain: AgentMain? = null

        // 采集器集合
        private val collects: ArrayList<ICollect> = arrayListOf(
            SpringControlCollect(),
            SpringServiceCollect()
        )

        private val keys: ArrayList<String> = arrayListOf("server", "key", "secret")

        /**
         * javaagent是在虚拟机启动之后加载的，
         * 我们需要在它的manifest文件中指定Agent-Class属性，
         * 它的值是javaagent的实现类，这个实现类需要实现一个agentmain方法
         *
         * @param args
         * @param inst Instrumentation
         */
        fun agentmain(args: String?, inst: Instrumentation?) {
            // pass
        }

        /**
         * 如果javaagent是在JVM启动时通过命令行参数加载的，
         * 情况会不太一样，需要在它的manifest文件中指定Premain-Class属性，
         * 它的值是javaagent的实现类，这个实现类需要实现一个premain方法。
         * @param agentArgs
         * @param inst
         */
        fun premain(agentArgs: String?, inst: Instrumentation) {
            if (agentArgs != null) {
                val paramGroup = agentArgs.split(",").toTypedArray()
                for (param in paramGroup) {
                    val keyValue = param.split("=").toTypedArray()
                    if (keys.contains(keyValue[0])) {
                        System.setProperty("\$bit" + keyValue[0], keyValue[1])
                    }
                }
            }
            if (System.getProperty("\$bit_server") == null) {
                System.setProperty("\$bit_server", "http://api.dagwan.com/receive")
            }

            // 这里可以通过硬编码，将所有采集器放入其中
            agentMain = AgentMain()
            inst.addTransformer(agentMain)
        }
    }

    @Throws(IllegalClassFormatException::class)
    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain,
        classfileBuffer: ByteArray
    ): ByteArray? {
        var clazzName = className
        if (clazzName == null || loader == null
            || loader.javaClass.name == "sun.reflect.DelegatingClassLoader"
            || loader.javaClass.name == "javax.management.remote.rmi"
            || clazzName.indexOf("\$Proxy") != -1
            || clazzName.startsWith("java")
        ) {
            return null
        }

        if (!classPoolMap.containsKey(loader)) {
            val classPool = ClassPool()
            classPool.insertClassPath(LoaderClassPath(loader))
            classPoolMap[loader] = classPool
        }

        val cp = classPoolMap[loader]
            ?: return null

        try {
            clazzName = clazzName.replace("/".toRegex(), ".")
            val clazz = cp[className]
            for (c in collects) {
                if (c.isTarget(clazzName, loader, clazz)) {

                    // 仅限定只能转换一次
                    return c.transform(loader, clazzName, classfileBuffer, clazz)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return classfileBuffer
    }
}
