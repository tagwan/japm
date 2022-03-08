package com.github.tagwan.japm.cfg

class StatisticsConfig(path: String) : AbstractProperties(path, "statistics.") {
    val httpServer: String by prop
    val elasticsearchServer: String by prop
}