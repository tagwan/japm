//package com.github.tagwan.japm.agent
//
//import com.github.tagwan.japm.const.COLLECTORS
//import javassist.ClassPool
//import javassist.LoaderClassPath
//import java.lang.instrument.ClassFileTransformer
//import java.lang.instrument.IllegalClassFormatException
//import java.security.ProtectionDomain
//import java.util.concurrent.ConcurrentHashMap
//
//
///**
// * 入口方法
// *
// * @data 2022/03/02
// * @author jdg
// */
//open class ApmAgentTransformer : ClassFileTransformer {
//
//    private val classPoolMap: MutableMap<ClassLoader, ClassPool> = ConcurrentHashMap()
//
//    @Throws(IllegalClassFormatException::class)
//    override fun transform(
//        loader: ClassLoader?,
//        className: String?,
//        classBeingRedefined: Class<*>?,
//        protectionDomain: ProtectionDomain,
//        classfileBuffer: ByteArray
//    ): ByteArray? {
//        var clazzName = className
//        if (clazzName == null || loader == null
//            || loader.javaClass.name == "sun.reflect.DelegatingClassLoader"
//            || loader.javaClass.name == "javax.management.remote.rmi"
//            || clazzName.indexOf("\$Proxy") != -1
//            || clazzName.startsWith("java")
//        ) {
//            return null
//        }
//
//        if (!classPoolMap.containsKey(loader)) {
//            val classPool = ClassPool()
//            classPool.insertClassPath(LoaderClassPath(loader))
//            classPoolMap[loader] = classPool
//        }
//
//        val cp = classPoolMap[loader]
//            ?: return null
//
//        try {
//            clazzName = clazzName.replace("/".toRegex(), ".")
//            val clazz = cp[className]
//            for (c in COLLECTORS) {
//                if (c.isTarget(clazzName, loader, clazz)) {
//
//                    // 仅限定只能转换一次
//                    return c.transform(loader, clazzName, classfileBuffer, clazz)
//                }
//            }
//        } catch (e: Throwable) {
//            e.printStackTrace()
//        }
//        return classfileBuffer
//    }
//}