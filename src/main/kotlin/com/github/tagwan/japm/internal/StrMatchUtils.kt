package com.github.tagwan.japm.internal


object StrMatchUtils {
    /**
     * 该方法用于判断 str 是否满足 exp 表达式
     * 注意：目前只处理 '*' 作为模糊匹配
     *
     *
     * 参考链接如下：
     * 1、https://www.cnblogs.com/daleyzou/p/9535134.html
     * 2、https://shmilyaw-hotmail-com.iteye.com/blog/2154716
     */
    fun isMatch(str: String, exp: String): Boolean {
        var strIndex = 0
        var expIndex = 0
        var starIndex = -1 //记录上一个 '*' 的位置
        while (strIndex < str.length) {
            val pkgChar = str[strIndex]
            val expChar = if (expIndex < exp.length) exp[expIndex] else '\u0000'
            if (pkgChar == expChar) { //字符相等
                strIndex++
                expIndex++
            } else if (expChar == '*') { //遇到'*', 记录'*'的位置，并记录 expIndex 和 match
                starIndex = expIndex
                expIndex++
            } else if (starIndex != -1) { //不是上述两种情况，无法匹配，因此回溯
                expIndex = starIndex + 1
                strIndex++
            } else { //其他情况， 直接返回false
                return false
            }
        }

        //检测 exp 尾部是否全部都为 '*'
        while (expIndex < exp.length && exp[expIndex] == '*') {
            expIndex++
        }

        //若 exp 尾部全部为 '*'，说明匹配
        return expIndex == exp.length
    }
}