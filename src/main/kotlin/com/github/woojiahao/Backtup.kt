package com.github.woojiahao

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.woojiahao.extensions.modify
import com.github.woojiahao.models.ConfigurationComponent
import com.github.woojiahao.models.Status
import com.github.woojiahao.utility.*
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

val configurationStatus = loadConfiguration()
val configuration = configurationStatus.value
val acceptedComponentNames = listOf("*", *configuration.componentNames.toTypedArray())

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
      component == "*" -> listAllComponents()
      configuration.hasComponent(component) -> listComponentItems(component)
    }
  }

  private fun listAllComponents() {
    echo("List of components present in .backup.json.")
    configuration.componentNames.forEachIndexed { index, s ->
      echo("[${index + 1}] $s")
    }
  }

  private fun listComponentItems(component: String) {
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

class AddComponent : CliktCommand(help = "Add a new component to the backup configuration file", name = "add") {
  private val component by argument(
    name = "COMPONENT",
    help = "Component to add new items to. If not specified, add new component to configuration."
  ).default("*")

  private val item by argument(
    name = "ITEM",
    help = "Item to be added to given component. Component specified must be an existing component."
  ).default("")

  override fun run() {
    if (component !in acceptedComponentNames) {
      throw UsageError("Invalid component name. Accepted component names: ${acceptedComponentNames.joinToString(", ")}")
    }

    when {
      component == "*" -> addComponent()
      configuration.hasComponent(component) -> {
        if (item.trim().isEmpty()) throw UsageError("Item must be specified.")
        else addItem(component, item)
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

  private fun addItem(component: String, item: String) {
    val configurationComponent = configuration.matchComponent(component)
    val itemPath = path(configurationComponent.path, item)
    if (!File(itemPath).exists()) throw UsageError("$itemPath does not exist.")
    if (configurationComponent.hasItem(item)) throw UsageError("$item already exists under \"$component\"")

    echo("Adding new item to \"$component\".")
    echo("Path for \"$component\" is ${configurationComponent.path.replaceWithRootPath()}")

    val updatedConfiguration = configuration.addItem(configurationComponent, item)
    writeConfiguration(updatedConfiguration)
    echo("Item added to \"$component\" pointing to $itemPath")
  }
}

class EditComponent : CliktCommand(help = "Edit a component within the backup configuration file", name = "edit") {
  private val component by argument(
    help = "Name of component to edit.",
    name = "COMPONENT"
  ).validate {
    require(it in configuration.componentNames) {
      "Component must be an existing component. View all components with \"backtup ls\""
    }
  }

  private val matchingComponent by lazy { configuration.matchComponent(component) }

  private val path by option("-p", "--path", help = "New path value")

  private val name by option("-n", "--name", help = "New name value")

  override fun run() {
    val newPath = (path ?: matchingComponent.path).replaceWithRootPath()
    val newName = name ?: matchingComponent.name

    if (!File(newPath).exists()) throw UsageError("Path provided does not exist on local machine.")
    if (newName in configuration.componentNames.modify { remove(matchingComponent.name) }) {
      throw UsageError("Name provided is already in use.")
    }

    val updatedComponent = ConfigurationComponent(newName, newPath, *matchingComponent.items)
    val updatedConfiguration = configuration.update(matchingComponent, updatedComponent)
    writeConfiguration(updatedConfiguration)
    echo("Component \"$component\" has been updated.")
  }
}

class RunComponent : CliktCommand(help = "Run backtup to backup files to a target device", name = "run") {
  private val component by argument(
    help = "Name of component to use for backup.",
    name = "COMPONENT"
  ).validate {
    require(it in configuration.componentNames) {
      "Component must be an existing component. View all components with \"backtup ls\""
    }
  }

  private val destination by argument(
    help = "Destination for backup to be sent to.",
    name = "DESTINATION"
  ).validate {
    require(File(it.replaceWithRootPath()).exists()) {
      "Destination does not exist on local file system."
    }
  }

  override fun run() {
    val timestamp = ofPattern("dd-MM-yyyy_HH:mm").format(LocalDateTime.now())
    val backupFolder = path(destination, timestamp)
    cmd("mkdir $backupFolder")
    configuration.files(component).forEach {
      if (!File(it).exists()) throw UsageError("$it listed in \"$component\" does not exist.")
      cmd("rsync -avPR --exclude=\".git\" --exclude=\"venv\" --exclude=\"node_modules\" $it $backupFolder")
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
    is Status.Success -> Backtup()
      .subcommands(
        ListComponents(),
        AddComponent(),
        EditComponent(),
        RunComponent()
      )
  }.main(args)
