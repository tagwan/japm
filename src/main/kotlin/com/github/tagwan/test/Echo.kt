package com.github.tagwan.test

import org.slf4j.LoggerFactory

interface IEcho {

}

abstract class BaseEcho {

}

class EchoImpl : BaseEcho(), IEcho {
    fun echo(any: Any) {
        Thread.sleep(3000)
        logger.info("Echo::echo()-->$any")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EchoImpl::class.java)
    }
}

fun main() {
    val obj = EchoImpl()
    obj.echo("hello world")
}