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

  @Test
  fun `fromJson converts JsonObject to ConfigurationComponent`() {
    val json = createJson("default", "~", "Documents", "Pictures")
    val configurationComponent = ConfigurationComponent.fromJson(json)
    val expectedComponent = ConfigurationComponent("default", "~", "Documents", "Pictures")
    assertEquals(expectedComponent, configurationComponent[0])
  }

  @Test
  fun `fromJson converts JsonObject with multiple components to list of ConfigurationComponent`() {
    val json = createJson("default", "~", "Documents", "Pictures")
    val phoneJson = createInnerJson("/run/media", "Gallery")
    json.add("phone", phoneJson)
    val expectedComponents = listOf(
      ConfigurationComponent("default", "~", "Documents", "Pictures"),
      ConfigurationComponent("phone", "/run/media", "Gallery")
    )
    val configurationComponents = ConfigurationComponent.fromJson(json)
    assertEquals(expectedComponents, configurationComponents)
  }

  private fun assertComponentJson(name: String, path: String, vararg items: String) {
    val configurationComponent = ConfigurationComponent(name, path, *items)
    val componentJson = configurationComponent.toJson()
    val expectedJson = createJson(name, path, *items)
    assertEquals(expectedJson, componentJson)
  }

  private fun createJson(name: String, path: String, vararg items: String) =
    JsonObject().apply {
      add(name, createInnerJson(path, *items))
    }

  private fun createInnerJson(path: String, vararg items: String) =
    JsonObject().apply {
      addProperty("path", path)
      val itemsArray = JsonArray().apply {
        items.forEach { add(it) }
      }
      add("items", itemsArray)
    }
}