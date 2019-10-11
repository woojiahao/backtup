package com.github.woojiahao.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

class ConfigurationComponent(
  val name: String,
  val path: String,
  vararg val items: String
) {
  companion object {
    val default
      get() = ConfigurationComponent("default", "~")

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

  override fun hashCode() = HashCodeBuilder(17, 37)
    .append(name)
    .append(path)
    .append(items)
    .toHashCode()

  override fun equals(other: Any?): Boolean {
    other ?: return false
    if (other !is ConfigurationComponent) return false
    if (other == this) return true

    return EqualsBuilder()
      .append(name, other.name)
      .append(path, other.path)
      .append(items, other.items)
      .isEquals
  }
}