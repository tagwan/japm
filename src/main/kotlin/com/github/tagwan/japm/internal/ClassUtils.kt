package com.github.tagwan.japm.internal

import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URL
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.stream.Collectors


/*
* 一个工具类，获取指定包下的所有文件
*/
object ClassUtils {
    /**
     * 给我一个包名，获取该包下的所有class文件
     * @param packageName 包名 com.xxx.xxx
     * @param isRecursive 是否递归
     * @return 返回class文件的集合
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    fun getClassList(packageName: String, isRecursive: Boolean): ArrayList<Class<*>> {

        //声明一个返回List
        val classList: ArrayList<Class<*>> = ArrayList()
        //将对应的包名转换为路径
        try {
            //Enumeration枚举接口，当中有两个方法，一个是判断是否有下一个元素，还有一个是取到下一个元素
            val urls: Enumeration<URL> =
                Thread.currentThread().contextClassLoader.getResources(packageName.replace('.', '/'))
            while (urls.hasMoreElements()) {
                val url: URL = urls.nextElement()
                //System.out.println(url);	//	file:/D:/SXTJava/annotation/bin/annotation
                //拿到文件的协议
                val protocol: String = url.protocol
                //如果是file
                if ("file" == protocol) {
                    //取到文件的路径
                    val packagePath: String = url.path // /D:/SXTJava/annotation/bin/annotation
                    addClass(classList, packagePath, packageName, isRecursive)
                } else if ("jar" == protocol) { //如果是jar包的情况:此情况没有测试
                    val jarURLConnection: JarURLConnection = url.openConnection() as JarURLConnection
                    val jarFile: JarFile = jarURLConnection.jarFile //取到jar包下的文件
                    val jarEntries: Enumeration<JarEntry> = jarFile.entries()
                    while (jarEntries.hasMoreElements()) { //遍历jarEnyries
                        val jarEntry: JarEntry = jarEntries.nextElement() //取到元素
                        val jarEntryName: String = jarEntry.name //取到文件名
                        if (jarEntryName.endsWith(".class")) { //如果文件名以.class结尾，将对应的文件添加至集合中
                            val name = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                            println(name)
                            val className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                .replace("/".toRegex(), ".") //取到类名
                            if (isRecursive || className.substring(0, className.lastIndexOf(".")) == packageName) {
                                classList.add(Class.forName(className))
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return classList
    }

    /**
     * 根据注解筛选对应的class文件
     * @param packageName 包名
     * @param annotationClass 注解类
     * @param isRecursive 是否递归
     * @return
     */
    fun getClassListByAnnotation(
        packageName: String,
        annotationClass: Class<out Annotation>,
        isRecursive: Boolean
    ): List<Class<*>> {
        var classList: ArrayList<Class<*>> = ArrayList()
        try {
            classList = this.getClassList(packageName, isRecursive)
            return classList.stream().filter {
                it.isAnnotationPresent(annotationClass)
            }.collect(Collectors.toList())
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return classList
    }

    /**
     * 将对应包名下的所有.class文件加入到classList集合中
     * @param classList 存放classList文件的集合
     * @param packagePath 包路径
     * @param packageName 包名
     * @param isRecursive 是否递归
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    private fun addClass(
        classList: MutableList<Class<*>>, packagePath: String, packageName: String,
        isRecursive: Boolean
    ) {
        //取到路径下所有的对应的文件，包括.class文件和目录
        val files: Array<File> = this.getClassFiles(packagePath) ?: return
        for (file in files) {
            //取到文件名
            val fileName: String = file.name //Column.class
            if (file.isFile) { //如果取到的是文件
                //取到对应的类名,这里的类名是权限定名
                val className = getClassName(packageName, fileName)
                classList.add(Class.forName(className))
            } else {
                if (isRecursive) {
                    ///D:/SXTJava/annotation/bin/annotation+包名（fileName:test）
                    val subPackagePath = getSubPackagePath(packagePath, fileName)
                    val subPackageName = getSubPackageName(packageName, fileName)
                    //递归调用自己
                    addClass(classList, subPackagePath, subPackageName, isRecursive)
                }
            }
        }
    }

    /**
     * 获取子包名
     * @param packageName
     * @param fileName
     * @return
     */
    private fun getSubPackageName(packageName: String?, fileName: String): String {
        var subPackageName = fileName
        if (packageName != null && "" != packageName) {
            subPackageName = "$packageName.$subPackageName"
        }
        return subPackageName
    }

    /**
     * 获取子目录
     * @param packagePath 包的路径
     * @param fileName 文件的路径
     * @return
     */
    private fun getSubPackagePath(packagePath: String?, fileName: String): String {
        var subPackagePath = fileName
        if (packagePath != null && "" != packagePath) {
            subPackagePath = "$packagePath/$subPackagePath"
        }
        return subPackagePath
    }

    /**
     * 根据传入的包名和文件名返回对应类的全限定名
     * @param packageName 包名
     * @param fileName 文件名 	类名.后缀名
     * @return 包名.类名
     */
    private fun getClassName(packageName: String?, fileName: String): String {
        var className = fileName.substring(0, fileName.indexOf("."))
        if (packageName != null && "" != packageName) {
            className = "$packageName.$className"
        }
        return className
    }

    /**
     * 获取class文件
     * @param packagePath
     * @return
     */
    private fun getClassFiles(packagePath: String): Array<File>? {
        //FileFilter文件过滤器
        return File(packagePath).listFiles { file ->
            file.isFile && file.name.endsWith(".class") || file.isDirectory
        }
    }
}