package com.github.woojiahao

import com.github.woojiahao.utility.path
import java.io.File

const val BACKUP_FILE_NAME = ".backup.json"

fun hasBackupFile(root: String = "~") = File(path(root, BACKUP_FILE_NAME)).exists()
