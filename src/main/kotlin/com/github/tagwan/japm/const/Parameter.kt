package com.github.tagwan.japm.const

const val PROPERTY_BOOT = "JapmPropFile"
const val DEFAULT_CFG = "japm-template.properties"
const val BANNER = "banner.txt"
const val METHOD_START = "injectOnStart" // 注入方法中需要执行的初始方法的名字
const val METHOD_OVER = "injectOnOver"  // 注入方法中需要执行的结束方法的名字

// region jvm中的类型描述符, 对象是L加上路径和分号，数组加上左中括号
const val BYTE = "B"
const val CHAR = "C"
const val DOUBLE = "D"
const val FLOAT = "F"
const val INT = "I"
const val LONG = "J"
const val SHORT = "S"
const val BOOL = "Z"
const val REF = "L"
const val ARRAY = "["
const val VOID = "V"
// endregion

const val OBJECT = "java/lang/Object"
const val STRING = "java/lang/String"
const val FUNC_VOID_STRING = "($REF$STRING;)$VOID" // "(Ljava/lang/String;)V"

const val INSTANCE = "INSTANCE" // kotlin中object获取的默认

// region JAVA Version
const val JAVA7 = 7
const val JAVA8 = 8
const val JAVA11 = 11
// endregion