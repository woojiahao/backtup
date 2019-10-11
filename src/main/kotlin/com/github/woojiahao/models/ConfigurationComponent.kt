package com.github.woojiahao.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import sun.security.krb5.Config

class ConfigurationComponent(
  val name: String,
  val path: String,
  vararg val items: String
) {
  companion object {
    fun fromJson(jsonObject: JsonObject): List<ConfigurationComponent> {
      val components = mutableListOf<ConfigurationComponent>()
      jsonObject.entrySet().forEach {
        val name = it.key
        val path = it.value.asJsonObject["path"].asString
        val items = it.value.asJsonObject["items"].asJsonArray.map { item -> item.asString }
        val component = ConfigurationComponent(name, path, *items.toTypedArray())
        components += component
      }
      return components
    }
  }

  fun toJson(): JsonObject {
    val componentInfo = JsonObject().apply {
      addProperty("path", path)
      val itemsArray = JsonArray().apply {
        items.forEach { add(it) }
      }
      add("items", itemsArray)
    }

    return JsonObject().apply {
      add(name, componentInfo)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (other !is ConfigurationComponent) return false
    return name == other.name &&
      path == other.path &&
      items.toList() == other.items.toList()
  }
}