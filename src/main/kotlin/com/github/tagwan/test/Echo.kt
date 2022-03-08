package com.github.tagwan.test

import org.slf4j.LoggerFactory

interface IEcho {

}

abstract class BaseEcho {

}

class EchoImpl: BaseEcho(), IEcho {
    var e: EchoImpl? = null

    fun echo(any: Any) {
        Thread.sleep(3000)
        logger.info("Echo::echo()-->$any")
    }

    fun show1() {
        if (e == null)
            return

        e!!.echo("123")

        val a = "456"

        e!!.echo("123")
    }

    fun show2() {
        val s = e
        if (s == null)
            return

        s.echo("123")

        val a = "456"

        s.echo("123")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EchoImpl::class.java)
    }
}

fun main() {
    val obj = EchoImpl()
    obj.echo(1111)
}