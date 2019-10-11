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
    val DEFAULT
      get() = ConfigurationComponent("default", "~")

    fun fromJson(jsonObject: JsonObject): Status<List<ConfigurationComponent>> {
      val components = mutableListOf<ConfigurationComponent>()
      jsonObject.entrySet().forEach {
        if (!it.value.isJsonObject) {
          return Status.Fail(
            emptyList(),
            "Invalid .backup.json format.",
            "Values must be JSON objects.",
            "Refer to Backtup documentation for more information about .backup.json."
          )
        }

        val information = it.value.asJsonObject

        if (!information.has("path")) {
          return Status.Fail(
            emptyList(),
            "Invalid .backup.json format.",
            "Must contain a path property.",
            "Refer to Backtup documentation for more information about .backup.json."
          )
        }

        if (!information["path"].asJsonPrimitive.isString) {
          return Status.Fail(
            emptyList(),
            "Invalid .backup.json format.",
            "Property \"path\" must have a value of string.",
            "Refer to Backtup documentation for more information about .backup.json"
          )
        }

        if (information.has("items") && !information["items"].isJsonArray) {
          return Status.Fail(
            emptyList(),
            "Invalid .backup.json format.",
            "Property \"items\" must have a value of a JSON array.",
            "Refer to Backtup documentation for more information about .backup.json"
          )
        }

        val name = it.key
        val path = information["path"].asString
        val items = information["items"].asJsonArray.map { item -> item.asString }
        val component = ConfigurationComponent(name, path, *items.toTypedArray())
        components += component
      }
      return Status.Success(components)
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

    return EqualsBuilder()
      .append(name, other.name)
      .append(path, other.path)
      .append(items, other.items)
      .isEquals
  }
}