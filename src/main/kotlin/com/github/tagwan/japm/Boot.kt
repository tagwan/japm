package com.github.tagwan.japm

import com.github.tagwan.japm.agent.AgentMain
import java.lang.instrument.Instrumentation


fun agentmain(args: String?, inst: Instrumentation?) {
    // pass
}

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