package com.github.woojiahao

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.woojiahao.models.Status
import com.github.woojiahao.utility.loadConfiguration

val configuration = loadConfiguration()

class Backtup : CliktCommand() {
  override fun run() {
    echo("hello world")
  }
}

class ErrorCommand : CliktCommand() {
  override fun run() {
    configuration as Status.Fail
    echo(configuration.error, err = true)
  }
}

class List : CliktCommand() {
  val category by argument("Backup category").default("*")
  override fun run() {
    echo(category)
  }
}

fun main(args: Array<String>) {
  val command = when(configuration) {
    is Status.Fail -> ErrorCommand()
    is Status.Success -> Backtup().subcommands(List())
  }
  command.main(args)
}
