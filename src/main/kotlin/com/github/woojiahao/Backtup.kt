package com.github.woojiahao

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.woojiahao.models.Status
import com.github.woojiahao.utility.loadConfiguration
import com.github.woojiahao.utility.replaceWithRootPath
import com.github.woojiahao.utility.writeConfiguration
import java.io.File

val configurationStatus = loadConfiguration()
val configuration = configurationStatus.value
val acceptedComponentNames = mutableListOf<String>().apply {
  this += configuration.componentNames
  this += "*"
}

class Backtup : CliktCommand() {
  override fun run() {}
}

class ListComponents : CliktCommand(help = "List all components within a backup configuration file", name = "ls") {
  private val component by argument(
    "COMPONENT",
    "Component of the backup configuration file. If specified, will display the individual files to backup in a component."
  ).default("*")

  override fun run() {
    if (component !in acceptedComponentNames) {
      throw UsageError("Invalid component name. Accepted component names: ${acceptedComponentNames.joinToString(", ")}")
    }

    when {
      component == "*" -> {
        echo("List of components present in .backup.json.")
        configuration.componentNames.forEachIndexed { index, s ->
          echo("[${index + 1}] $s")
        }
      }
      // TODO: When configuration is empty, write different prompt
      configuration.hasComponent(component) -> {
        val files = configuration.files(component)
        if (files.isEmpty()) {
          echo("No items in \"$component\". Add new items with \"backtup add $component [ITEM]\".")
        } else {
          echo("Files to backup listed under $component")
          configuration.files(component).forEachIndexed { index, s ->
            echo("[${index + 1}] $s")
          }
        }
      }
    }
  }
}

class AddComponent : CliktCommand(help = "Add a new component to the backup configuration file", name = "add") {
  private val component by argument(
    name = "COMPONENT",
    help = "Component to add new items to. If not specified, add new component to configuration."
  ).default("*")

  override fun run() {
    if (component !in acceptedComponentNames) {
      throw UsageError("Invalid component name. Accepted component names: ${acceptedComponentNames.joinToString(", ")}")
    }

    when {
      component == "*" -> addComponent()
      component in acceptedComponentNames -> {
      }
    }
  }

  private fun addComponent() {
    echo("Adding new component to configuration.")
    val name = TermUi.prompt("Name of new component") {
      if (it in acceptedComponentNames) throw UsageError("$it is already an accepted component name.")
      it
    }
    name ?: throw UsageError("$name cannot be null.")
    echo("Component name: $name")

    val path = TermUi.prompt("Path of new component") {
      if (!File(it.replaceWithRootPath()).exists()) throw UsageError("$it is not a valid path.")
      it
    }
    path ?: throw UsageError("$path cannot be null.")
    echo("Path for $name: $path")
    val updatedConfiguration = configuration.addComponent(name, path)
    writeConfiguration(updatedConfiguration)
    echo("Component created. To add items to the component, edit .backup.json or use \"backtup add $name [ITEM]\"")
  }
}

fun main(args: Array<String>) =
  when (configurationStatus) {
    is Status.Fail -> object : CliktCommand() {
      override fun run() {
        echo(configurationStatus.error, err = true)
      }
    }
    is Status.Success -> Backtup()
      .subcommands(
        ListComponents(),
        AddComponent()
      )
  }.main(args)
