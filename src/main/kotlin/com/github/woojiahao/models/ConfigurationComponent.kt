package com.github.woojiahao.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class ConfigurationComponent(
  val name: String,
  val path: String,
  vararg val items: String
) {
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
}