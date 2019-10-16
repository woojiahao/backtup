package com.github.woojiahao.models

import com.github.woojiahao.extensions.modify
import com.github.woojiahao.utility.replaceWithRootPath
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class Configuration(private val components: List<ConfigurationComponent>) {

  constructor(vararg components: ConfigurationComponent) : this(components.toList())

  constructor(
    originalComponents: List<ConfigurationComponent>,
    modification: MutableList<ConfigurationComponent>.() -> Unit
  ) : this(originalComponents.modify(modification))

  val componentNames = components.map { it.name }

  fun hasComponent(component: String) = component in componentNames

  fun matchComponent(component: String) = components.first { it.name == component }

  fun files(component: String) =
    with(matchComponent(component)) {
      items.map { "$path/$it".replaceWithRootPath() }
    }

  fun addComponent(name: String, path: String) =
    Configuration(components) {
      add(ConfigurationComponent(name, path))
    }

  fun addItem(component: ConfigurationComponent, item: String) =
    Configuration(components) {
      val componentPosition = components.indexOf(component)
      removeAt(componentPosition)
      add(componentPosition, component.addItem(item))
    }

  fun update(originalComponent: ConfigurationComponent, updatedComponent: ConfigurationComponent) =
    Configuration(components) {
      val componentPosition = components.indexOf(originalComponent)
      removeAt(componentPosition)
      add(componentPosition, updatedComponent)
    }


  fun toJson(): JsonObject {
    val parent = JsonObject()
    components.forEach {
      val itemsArray = JsonArray().apply { it.items.forEach { item -> add(item) } }
      val componentJson = JsonObject().apply {
        addProperty("path", it.path)
        add("items", itemsArray)
      }

      parent.add(it.name, componentJson)
    }
    return parent
  }
}