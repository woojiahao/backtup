package com.github.woojiahao.models

sealed class Status<T>(val value: T) {
  class Fail<T>(default: T, vararg val errorComponents: String): Status<T>(default) {
    val error = errorComponents.joinToString("\n")
  }

  class Success<T>(value: T): Status<T>(value)
}

