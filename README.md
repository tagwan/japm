## [About](README.md)

<h1 align="center">JAPM</h1>

<div align="center">

一个针对高并发、低延迟应用设计的高性能Jvm程序（java、kotlin, eg..）的性能监控和统计工具。


</div>

## Usage
在 JVM 启动参数里加上以下两个参数
* -javaagent:/path/japm-1.0.jar
* -DJapmPropFile=/path/japm.properties

```bash
java -javaagent:E:\\tmp\\japm-0.0.1.jar -DJapmPropFile=E:\\tmp\\japm.properties `-jar application.jar`
```

## Issue
如果您有任何问题、疑问或者建议，您可以 [提交Issue](https://github.com/tagwan/japm/issues/new/choose)  ：）