package com.github.woojiahao.models

import com.github.woojiahao.utility.replaceWithRootPath

class Configuration(val components: List<ConfigurationComponent>) {
  val componentNames = components.map { it.name }

  fun hasComponent(component: String) = componentNames.contains(component)

  fun matchComponent(component: String) = components.first { it.name == component }

  fun files(component: String) =
    with(matchComponent(component)) {
      items.map { "$path/$it".replaceWithRootPath() }
    }
}