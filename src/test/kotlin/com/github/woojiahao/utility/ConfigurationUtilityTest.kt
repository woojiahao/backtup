package com.github.woojiahao.utility

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import kotlin.test.Test
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
}