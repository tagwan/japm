//package com.github.tagwan.japm.collect
//
//import javassist.CtClass
//
///**
// * 采集器
// *
// * @data 2022/03/02
// * @author jdg
// */
//interface ICollect {
//
//    /**
//     * 是否为采集目标
//     *
//     * @param className
//     * @param classLoader
//     * @param ctClass
//     * @return
//     */
//    fun isTarget(className: String, classLoader: ClassLoader, ctClass: CtClass): Boolean
//
//    /**
//     * 对采集目标进行转换
//     *
//     * @param classLoader
//     * @param className
//     * @param classfileBuffer
//     * @param ctClass
//     * @return
//     */
//    fun transform(classLoader: ClassLoader, className: String, classfileBuffer: ByteArray, ctClass: CtClass): ByteArray
//}