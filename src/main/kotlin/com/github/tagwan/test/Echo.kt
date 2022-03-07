package com.github.tagwan.test

import org.slf4j.LoggerFactory

class Echo {
    fun echo(any: Any) {
        Thread.sleep(5000)
        val ss = 1123231
        logger.info("Echo::echo()-->$ss")
        logger.info("Echo::echo()-->$any")
        logger.info("Echo::echo()-->$any")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Echo::class.java)
    }
}

fun main() {
    val obj = Echo()
    obj.echo(1111)
}