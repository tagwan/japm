package com.github.tagwan.japm.cfg

import com.github.tagwan.japm.internal.StrMatchUtils

object ProfilingFilter {

    val excludePackagePrefix: HashSet<String> = HashSet() // 不需要注入的 Package前缀 集合
    val excludePackageExp: HashSet<String> = HashSet() // 不需要注入的 Package表达式 集合
    val includePackagePrefix: HashSet<String> = HashSet()
    val includePackageExp: HashSet<String> = HashSet()
    val excludeMethods: HashSet<String> = HashSet()
    val excludeClassLoader: HashSet<String> = HashSet()

    init {
        // 默认不注入的 package
        excludePackagePrefix.add("java/")
        excludePackagePrefix.add("javax/")
        excludePackagePrefix.add("sun/")
        excludePackagePrefix.add("com/sun/")
        excludePackagePrefix.add("com/intellij/")

        // 不注入 japm 本身
        excludePackagePrefix.add("com/github/tagwan/japm/")

        // 默认注入的 package
        includePackagePrefix.add("net/paoding/rose/jade/context/JadeInvocationHandler") //Jade
        includePackagePrefix.add("org/apache/ibatis/binding/MapperProxy") //Mybatis
        includePackagePrefix.add("com/alibaba/dubbo/rpc/proxy/InvokerInvocationHandler") //DUBBO
        includePackagePrefix.add("org/apache/dubbo/rpc/proxy/InvokerInvocationHandler") //DUBBO
        includePackagePrefix.add("com/alipay/sofa/rpc/proxy/jdk/JDKInvocationHandler") //SOFA jdk-proxy
        includePackagePrefix.add("com/weibo/api/motan/proxy/RefererInvocationHandler") //Motan

        //默认不注入的method
        excludeMethods.add("main")
        excludeMethods.add("premain")
        excludeMethods.add("getClass") //java.lang.Object
        excludeMethods.add("hashCode") //java.lang.Object
        excludeMethods.add("equals") //java.lang.Object
        excludeMethods.add("clone") //java.lang.Object
        excludeMethods.add("toString") //java.lang.Object
        excludeMethods.add("notify") //java.lang.Object
        excludeMethods.add("notifyAll") //java.lang.Object
        excludeMethods.add("wait") //java.lang.Object
        excludeMethods.add("finalize") //java.lang.Object
        excludeMethods.add("afterPropertiesSet") //spring

    }

    private fun preprocess(pkg: String): String? {
        return pkg.replace('.', '/').trim { it <= ' ' }
    }

    private fun isMatch(innerClassName: String, pkgPrefixSet: Set<String>, pkgExpSet: Set<String>): Boolean {
        for (prefix in pkgPrefixSet) {
            if (innerClassName.startsWith(prefix)) {
                return true
            }
        }
        for (exp in pkgExpSet) {
            if (StrMatchUtils.isMatch(innerClassName, exp)) {
                return true
            }
        }
        return false
    }

    /**
     * @param innerClassName : 形如: cn/myperf4j/core/ProfilingFilter
     * @return : true->不需要修改字节码  false->需要修改字节码
     */
    fun isNotNeedInject(innerClassName: String?): Boolean {
        if (innerClassName == null) {
            return false
        }
        return if (innerClassName.indexOf('$') >= 0) {
            true
        } else ProfilingFilter.isMatch(innerClassName, excludePackagePrefix, excludePackageExp)
    }

    /**
     * @param innerClassName : 形如: cn/myperf4j/core/ProfilingFilter
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    fun isNeedInject(innerClassName: String?): Boolean {
        return if (innerClassName == null) {
            false
        } else isMatch(innerClassName, includePackagePrefix, includePackageExp)
    }


    /**
     * @param methodName
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    fun isNotNeedInjectMethod(methodName: String?): Boolean {
        if (methodName == null) {
            return false
        }
        return if (isSpecialMethod(methodName)) {
            true
        } else excludeMethods.contains(methodName)
    }

    private fun isSpecialMethod(methodName: String): Boolean {
        val symbolIndex = methodName.indexOf('$')
        if (symbolIndex < 0) {
            return false
        }
        val leftParenIndex = methodName.indexOf('(')
        return leftParenIndex < 0 || symbolIndex < leftParenIndex
    }

    /**
     * 是否是不需要注入的类加载器
     *
     * @param classLoader
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    fun isNotNeedInjectClassLoader(classLoader: String?): Boolean {
        return excludeClassLoader.contains(classLoader)
    }
}