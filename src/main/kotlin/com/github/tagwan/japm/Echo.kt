package com.github.tagwan.japm

import org.slf4j.LoggerFactory

class Echo {
    fun echo(any: Any) {
        logger.info("Echo::echo()-->$any")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Echo::class.java)
    }
}

fun main() {
    Echo().echo(123)
}