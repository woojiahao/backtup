package com.github.woojiahao

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int

class Backtup : CliktCommand() {
  override fun run() {
    echo(getFormattedHelp())
  }
}

class List : CliktCommand() {
  override fun run() {

  }
}

fun main(args: Array<String>) = Backtup()
  .subcommands(List())
  .main(args)
