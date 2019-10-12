package com.github.woojiahao.models

import com.github.woojiahao.utility.replaceWithRootPath
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class Configuration(private val components: List<ConfigurationComponent>) {

  constructor(vararg components: ConfigurationComponent) : this(components.toList())

  val componentNames = components.map { it.name }

  fun hasComponent(component: String) = component in componentNames

  fun matchComponent(component: String) = components.first { it.name == component }

  fun files(component: String) =
    with(matchComponent(component)) {
      items.map { "$path/$it".replaceWithRootPath() }
    }

  fun addComponent(name: String, path: String): Configuration {
    val mutableComponents = components
      .toMutableList()
      .apply { this += ConfigurationComponent(name, path) }
      .toList()
    return Configuration(mutableComponents)
  }

  fun addItem(component: ConfigurationComponent, item: String): Configuration {
    val componentPosition = components.indexOf(component)
    val updatedComponent = component.addItem(item)
    val mutableComponents = components
      .toMutableList()
      .apply {
        removeAt(componentPosition)
        add(componentPosition, updatedComponent)
      }
      .toList()
    return Configuration(mutableComponents)
  }

  fun toJson(): JsonObject {
    val parent = JsonObject()
    components.forEach {
      val itemsArray = JsonArray().apply {
        it.items.forEach { item -> add(item) }
      }
      val componentJson = JsonObject().apply {
        addProperty("path", it.path)
        add("items", itemsArray)
      }

      parent.add(it.name, componentJson)
    }
    return parent
  }
}