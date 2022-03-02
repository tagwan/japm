package com.github.tagwan.japm

import com.github.tagwan.japm.agent.AgentMain
import java.lang.instrument.Instrumentation


/**
 * Premain
 *
 * <p>
 *     premain 的代理 jar 包需要在 Java 程序启动时指定，
 *     并且只能在类加载之前修改字节码，类被加载之后就无能为力了
 *
 * @param agentArgs
 * @param inst
 */
fun premain(agentArgs: String?, inst: Instrumentation) {
    if (agentArgs != null) {
        val paramGroup = agentArgs.split(",").toTypedArray()
        for (param in paramGroup) {
            val keyValue = param.split("=").toTypedArray()
            if (AgentMain.keys.contains(keyValue[0])) {
                System.setProperty("\$bit" + keyValue[0], keyValue[1])
            }
        }
    }

    if (System.getProperty("\$bit_server") == null) {
        System.setProperty("\$bit_server", "http://api.dagwan.com/receive")
    }

    // 这里可以通过硬编码，将所有采集器放入其中
    val agentMain = AgentMain()
    inst.addTransformer(agentMain)
}

/**
 * Agentmain
 *
 * <p>
 *     为了弥补这个缺点，JDK 1.6 引入了新的 agentmain 用于支持在类加载后再次加载该类，
 *     也就是重定义类，在重定义的时候可以修改类
 *
 * <p>
 *     阿里开源的 Java诊断工具 Arthas 就是基于 agentmain
 *
 * @param args
 * @param inst
 */
fun agentmain(args: String?, inst: Instrumentation?) {
    // pass
}