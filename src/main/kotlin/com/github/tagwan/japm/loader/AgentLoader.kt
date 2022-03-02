package com.github.tagwan.japm.loader

import javassist.*
import java.io.IOException


/**
 * 装载器
 * 构建代理监听环境
 * 为目标类载入代理监听
 */
/**
 * Agent loader
 *
 * @param className
 * @param loader  [ClassLoader]
 * @param ctClass [CtClass]
 *
 * @data 2022/03/02
 * @author jdg
 */
class AgentLoader(
    private val className: String,
    private val loader: ClassLoader,
    private val ctClass: CtClass
) {

    @Throws(CannotCompileException::class)
    fun updateMethod(method: CtMethod, srcBuild: MethodSrcBuild) {
        val methodName = method.name
        // 重构被代理的方法名称

        // 基于原方法复制生成代理方法
        val agentMethod = CtNewMethod.copy(method, methodName, ctClass, null)
        agentMethod.name = "$method\$agent"
        ctClass.addMethod(agentMethod)

        // 原方法重置为代理执行
        method.setBody(srcBuild.buildSrc(method))
    }

    /**
     * 生成新的class字节码
     *
     * @return
     * @throws IOException
     * @throws CannotCompileException
     */
    @Throws(IOException::class, CannotCompileException::class)
    fun toByteCode(): ByteArray {
        return ctClass.toBytecode()
    }

    class MethodSrcBuild {
        private var beginSrc: String? = null
        private var endSrc: String? = null
        private var errorSrc: String? = null

        fun setBeginSrc(beginSrc: String): MethodSrcBuild {
            this.beginSrc = beginSrc
            return this
        }

        fun setEndSrc(endSrc: String): MethodSrcBuild {
            this.endSrc = endSrc
            return this
        }

        fun setErrorSrc(errorSrc: String): MethodSrcBuild {
            this.errorSrc = errorSrc
            return this
        }

        fun buildSrc(method: CtMethod): String {
            var result: String
            return try {
                val template = if (method.returnType.name == "void") VOID_SOURCE else SOURCE
                val bSrc = if (beginSrc == null) "" else beginSrc!!
                val eSrc = if (errorSrc == null) "" else errorSrc!!
                val enSrc = if (endSrc == null) "" else endSrc!!
                String.format(template, bSrc, method.name, eSrc, enSrc)
            } catch (e: NotFoundException) {
                throw RuntimeException(e)
            }
        }
    }
}

const val SOURCE = ("{\n"
        + "%s"
        + "    Object result = null;\n"
        + "    try{\n"
        + "        result=(\$w)%s\$agent($$);\n"
        + "    } catch (Throwable e) {\n"
        + "%s"
        + "        throw e;\n"
        + "    } finally{\n"
        + "%s"
        + "    }\n"
        + "    return (\$r)result;\n"
        + "}\n")

const val VOID_SOURCE = ("{\n"
        + "%s"
        + "    try{\n"
        + "        %s\$agent($$);\n"
        + "    } catch (Throwable e) {\n"
        + "%s"
        + "        throw e;\n"
        + "    } finally{\n"
        + "%s"
        + "    }\n"
        + "}\n")
