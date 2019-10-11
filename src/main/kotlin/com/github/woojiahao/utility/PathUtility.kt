package com.github.woojiahao.utility

val rootPath
  get() = System.getProperty("user.home")

fun String.replaceWithRootPath(target: String = "~") = replace(target, rootPath)

fun path(vararg parts: String) = parts.joinToString("/") { it.replaceWithRootPath() }
