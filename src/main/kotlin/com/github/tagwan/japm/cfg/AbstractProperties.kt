package com.github.tagwan.japm.cfg

import com.github.tagwan.japm.internal.PropertiesDelegate


abstract class AbstractProperties(path: String, prefix: String) {
    protected val prop = PropertiesDelegate(path, prefix)
}