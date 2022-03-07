package com.github.tagwan.japm.cfg

object ExcludeCfg {

    val packageNameSet: HashSet<String> = HashSet()
    val clazzNameSet: HashSet<String> = HashSet()
    val methodNameSet: HashSet<String> = HashSet()

    init {
        packageNameSet.add("\$java/.*?")
        packageNameSet.add("\$javax/.*?")
        packageNameSet.add("\$sun/.*?")
        packageNameSet.add("\$com/sun/.*?")
        packageNameSet.add("\$com/intellij/.*?")
        packageNameSet.add("com/github/tagwan/japm/")

        methodNameSet.add("main")
        methodNameSet.add("premain")
        methodNameSet.add("getClass") //java.lang.Object
        methodNameSet.add("hashCode") //java.lang.Object
        methodNameSet.add("equals") //java.lang.Object
        methodNameSet.add("clone") //java.lang.Object
        methodNameSet.add("toString") //java.lang.Object
        methodNameSet.add("notify") //java.lang.Object
        methodNameSet.add("notifyAll") //java.lang.Object
        methodNameSet.add("wait") //java.lang.Object
        methodNameSet.add("finalize") //java.lang.Object
        methodNameSet.add("afterPropertiesSet") //spring
    }

    /**
     * Validate
     *
     * @param fullName
     * @return true 无效
     */
    fun validate(fullName: String): Boolean {
        // com/intellij/rt/execution/application/AppMainV2$1
        for (str in packageNameSet) {
            val regex = Regex(str)
            val matched = regex.containsMatchIn(input = fullName)
            if (matched) {
                return true
            }
        }

        val clazzName = fullName.split("/").lastOrNull()
            ?: return false

        for (str in methodNameSet) {
            val regex = Regex(str)
            val matched = regex.containsMatchIn(input = fullName)
            if (matched) {
                return true
            }
        }
        return false
    }
}