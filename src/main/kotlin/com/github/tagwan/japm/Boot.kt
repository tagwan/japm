package com.github.tagwan.japm

/*******************************************************************************
MIT License

Copyright (c) 2022 Espresso

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 ******************************************************************************/

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
    Application.init()
    Application.start(inst)
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

/**
 * Main
 *
 */
fun main() {
    // pass
}