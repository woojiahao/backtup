package com.github.woojiahao

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.woojiahao.models.Status
import com.github.woojiahao.utility.loadConfiguration

val configurationStatus = loadConfiguration()
val configuration = configurationStatus.value

class Backtup : CliktCommand() {
  override fun run() { }
}

class ListComponents : CliktCommand(name = "ls") {
  private val component by argument("Backup component").default("*")

  override fun run() {
    when {
      component == "*" -> {
        echo("List of components present in .backup.json.")
        configuration.componentNames.forEachIndexed { index, s ->
          echo("[$index] $s")
        }
      }
      configuration.hasComponent(component) -> {
        echo("Files to backup listed under $component")
        configuration.files(component).forEachIndexed { index, s ->
          echo("[$index] $s")
        }
      }
      else -> {
        echo("$component is not listed in .backup.json.\nAdd to the configuration file to use it.", err = true)
      }
    }
  }
}

fun main(args: Array<String>) =
  when (configurationStatus) {
    is Status.Fail -> object : CliktCommand() {
      override fun run() {
        echo(configurationStatus.error, err = true)
      }
    }
    is Status.Success -> Backtup().subcommands(ListComponents())
  }.main(args)
