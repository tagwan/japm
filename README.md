## [About](README.md)

<h1 align="center">JAPM</h1>

<div align="center">

一个针对高并发、低延迟应用设计的高性能Jvm程序（java、kotlin, eg..）的性能监控和统计工具。


</div>


## Compile

如果你想要编译本项目, 你可能需要提前准备以下环境

| Target      | Version |
| ----------- | ----------- |
| Jdk      | 11       |
| Kotlin   | 1.5.31        |
| Gradle   | 6.8.3        |

编译
```bash
gradlew clean buid
```

## Usage

在 JVM 启动参数里加上以下两个参数
* `\-javaagent` : 指定代理jar
* `\-DJapmPropFile` : 指定配置文件(不指定的话，则启用[默认配置](./src/main/resources/japm-template.properties)

例如:
```bash
java -javaagent:E:\\tmp\\japm-0.0.1.jar -DJapmPropFile=E:\\tmp\\japm.properties `-jar application.jar`
```

## Issue
如果您有任何问题、疑问或者建议，您可以 [提交Issue](https://github.com/tagwan/japm/issues/new/choose)  ;-)