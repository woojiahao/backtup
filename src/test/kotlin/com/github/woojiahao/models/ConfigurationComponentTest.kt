package com.github.woojiahao.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

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
  fun `Backup file must contain only string-object pairs`() {
    val singleProperty = JsonObject().apply {
      addProperty("dummy", "string")
    }
    val singlePropertyStatus = ConfigurationComponent.fromJson(singleProperty)
    assertTrue(singlePropertyStatus is Status.Fail)

    val multiProperty = createJson("default", "~").apply {
      addProperty("dummy", "string")
    }
    val multiPropertyStatus = ConfigurationComponent.fromJson(multiProperty)
    assertTrue(multiPropertyStatus is Status.Fail)
  }

  @Test
  fun `Backup component must have path property`() {
    val failJson = JsonObject().apply {
      add("default", JsonObject().apply { add("items", JsonArray()) })
    }
    val failStatus = ConfigurationComponent.fromJson(failJson)
    assertTrue(failStatus is Status.Fail)

    val successJson = JsonObject().apply {
      add("default", JsonObject().apply {
        addProperty("path", "~")
        add("items", JsonArray())
      })
    }
    val successState = ConfigurationComponent.fromJson(successJson)
    assertTrue(successState is Status.Success)
  }

  @Test
  fun `Backup component path property must be a string`() {
    val failJson = JsonObject().apply {
      add("default", JsonObject().apply { add("path", JsonArray()) })
    }
    val failStatus = ConfigurationComponent.fromJson(failJson)
    assertTrue(failStatus is Status.Fail)

    val successJson = JsonObject().apply {
      add("default", JsonObject().apply { addProperty("path", "~") })
    }
    val successStatus = ConfigurationComponent.fromJson(successJson)
    assertTrue(successStatus is Status.Success)
  }

  @Test
  fun `Backup component items propery must be an array`() {
    val failJson = JsonObject().apply {
      add(
        "default",
        JsonObject().apply {
          addProperty("path", "~")
          addProperty("items", "test")
        }
      )
    }
    val failStatus = ConfigurationComponent.fromJson(failJson)
    assertTrue(failStatus is Status.Fail)

    val successJson = JsonObject().apply {
      add(
        "default",
        JsonObject().apply {
          addProperty("path", "~")
          add("items", JsonArray())
        }
      )
    }
    val successStatus = ConfigurationComponent.fromJson(successJson)
    assertTrue(successStatus is Status.Success)
  }

  @Test
  fun `fromJson converts JsonObject to ConfigurationComponent`() {
    val json = createJson("default", "~", "Documents", "Pictures")
    val configurationComponent = ConfigurationComponent.fromJson(json)
    val expectedComponent = ConfigurationComponent("default", "~", "Documents", "Pictures")
    if (configurationComponent is Status.Success) {
      assertEquals(expectedComponent, configurationComponent.value[0])
    } else {
      fail("Could not parse JSON")
    }
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
    if (configurationComponents is Status.Success) {
      assertEquals(expectedComponents, configurationComponents.value)
    } else {
      fail("Could not parse JSON")
    }
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