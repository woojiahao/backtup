package com.github.woojiahao

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.woojiahao.models.Status
import com.github.woojiahao.utility.loadConfiguration

val configuration = loadConfiguration()

class Backtup : CliktCommand() {
  override fun run() {}
}

class List : CliktCommand() {
  val category by argument("Backup category").default("*")
  override fun run() {
    echo(category)
  }
}

fun main(args: Array<String>) =
  when (configuration) {
    is Status.Fail -> object : CliktCommand() {
      override fun run() {
        echo(configuration.error, err = true)
      }
    }
    is Status.Success -> Backtup().subcommands(List())
  }.main(args)
