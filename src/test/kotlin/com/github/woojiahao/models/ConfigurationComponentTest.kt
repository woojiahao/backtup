package com.github.woojiahao.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.junit.Test
import kotlin.test.assertEquals

class ConfigurationComponentTest {
  @Test
  fun `toJson exports ConfigurationComponent as a JsonObject`() {
    assertComponentJson("default", "~", "Documents", "Pictures")
  }

  @Test
  fun `toJson exports ConfigurationComponent with empty items as blank array`() {
    assertComponentJson("phone", "/run/media/")
  }

  private fun assertComponentJson(name: String, path: String, vararg items: String) {
    val configurationComponent = ConfigurationComponent(name, path, *items)
    val componentJson = configurationComponent.toJson()
    val expectedJson = createJson(name, path, *items)
    assertEquals(expectedJson, componentJson)
  }

  private fun createJson(name: String, path: String, vararg items: String): JsonObject {
    val innerJson = JsonObject().apply {
      addProperty("path", path)
      val itemsArray = JsonArray().apply {
        items.forEach { add(it) }
      }
      add("items", itemsArray)
    }

    return JsonObject().apply {
      add(name, innerJson)
    }
  }
}