package com.github.woojiahao.utility

import com.github.woojiahao.utility.BACKUP_FILE_NAME
import com.github.woojiahao.utility.hasBackupFile
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfigurationUtilityTest {
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