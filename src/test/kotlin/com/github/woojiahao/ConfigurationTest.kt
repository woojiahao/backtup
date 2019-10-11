package com.github.woojiahao

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfigurationTest {
  @get:Rule
  val temporaryFolder = TemporaryFolder()

  @Test
  fun `hasBackupFile returns false if backup file does not exist in provided directory`() {
    assertFalse(hasBackupFile(temporaryFolder.root.absolutePath))
  }

  @Test
  fun `hasBackupFile returns true if backup file exists in provided directory`() {
    temporaryFolder.newFile(BACKUP_FILE_NAME)
    assertTrue(hasBackupFile(temporaryFolder.root.absolutePath))
  }
}