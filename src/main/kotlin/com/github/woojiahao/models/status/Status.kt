package com.github.woojiahao.models.status

sealed class Status<T>(val value: T) {
  class Fail<T>(default: T, vararg errorComponents: String): Status<T>(default) {
    val error = errorComponents.joinToString("\n")
  }

  class Success<T>(value: T): Status<T>(value)
}

