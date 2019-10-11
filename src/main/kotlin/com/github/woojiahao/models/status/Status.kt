package com.github.woojiahao.models.status

sealed class Status<T>(default: T, val value: T = default) {
  class Fail<T>(default: T, private vararg val errorComponents: String): Status<T>(default) {
    val error = errorComponents.joinToString("\n")
  }

  class Success<T>(value: T): Status<T>(value)
}

