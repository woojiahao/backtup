package com.github.woojiahao.utility

import com.github.woojiahao.extensions.read
import com.github.woojiahao.models.Configuration
import com.github.woojiahao.models.ConfigurationComponent
import com.github.woojiahao.models.Status
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

const val BACKUP_FILE_NAME = ".backup.json"

fun hasBackupFile(root: String = "~") = File(path(root, BACKUP_FILE_NAME)).exists()

fun createBackupFile(root: String = "~") = File(path(root, BACKUP_FILE_NAME)).createNewFile()

fun writeConfiguration(configuration: Configuration, root: String = "~") =
  with(File(path(root, BACKUP_FILE_NAME))) {
    writeText(configuration.toJson().toString())
  }

fun loadDefaultBackupComponent(root: String = "~") =
  writeConfiguration(Configuration(ConfigurationComponent.DEFAULT), root)

fun loadConfiguration(root: String = "~"): Status<Configuration> {
  if (!hasBackupFile(root)) {
    createBackupFile(root)
    loadDefaultBackupComponent(root)
  }

  val backupJson = Gson().read<JsonObject>(File(path(root, BACKUP_FILE_NAME)).readText())
  return when(val configuration = ConfigurationComponent.fromJson(backupJson)) {
    is Status.Success -> Status.Success(Configuration(configuration.value))
    is Status.Fail -> Status.Fail(Configuration(configuration.value), *configuration.errorComponents)
  }
}
