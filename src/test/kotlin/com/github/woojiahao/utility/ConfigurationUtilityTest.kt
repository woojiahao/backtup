package com.github.woojiahao.utility

import com.github.woojiahao.extensions.read
import com.github.woojiahao.models.ConfigurationComponent
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfigurationUtilityTest {
  @get:Rule
  val temporaryFolder = TemporaryFolder()

  private val temporaryFolderPath
    get() = temporaryFolder.root.absolutePath

  @Test
  fun `hasBackupFile returns false if backup file does not exist in provided directory`() {
    assertFalse(hasBackupFile(temporaryFolderPath))
  }

  @Test
  fun `hasBackupFile returns true if backup file exists in provided directory`() {
    temporaryFolder.newFile(BACKUP_FILE_NAME)
    assertTrue(hasBackupFile(temporaryFolderPath))
  }

  @Test
  fun `createBackupFile adds backup json to provided directory and returns true`() {
    val fileCreatedSuccessfully = createBackupFile(temporaryFolderPath)
    assertTrue(fileCreatedSuccessfully)
    assertTrue(hasBackupFile(temporaryFolderPath))
  }

  @Test
  fun `createBackupFile called twice returns false`() {
    val fileCreatedSuccessfully = createBackupFile(temporaryFolderPath)
    assertTrue(fileCreatedSuccessfully)
    val createFileAgain = createBackupFile(temporaryFolderPath)
    assertFalse(createFileAgain)
  }

  @Test
  fun `loadDefaultBackupComponent loads backup file with default backup component`() {
    val fileCreatedSuccessfully = createBackupFile(temporaryFolderPath)
    if (fileCreatedSuccessfully) {
      loadDefaultBackupComponent(temporaryFolderPath)
      val json = Gson().read<JsonObject>(File(path(temporaryFolderPath, BACKUP_FILE_NAME)).readText())
      val defaultComponent = ConfigurationComponent.DEFAULT
      val configurationComponent = ConfigurationComponent.fromJson(json)[0]
      assertEquals(defaultComponent, configurationComponent)
    }
  }

  @Test
  fun `validateBackupFileStructure returns false if file structure is invalid`() {
//    val isFileStructureValid = validateBackupFileStructure(temporaryFolderPath)
//    assertTrue(isFileStructureValid)
  }
}