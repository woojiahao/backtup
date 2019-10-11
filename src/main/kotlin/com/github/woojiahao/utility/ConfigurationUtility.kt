package com.github.woojiahao.utility

import java.io.File

const val BACKUP_FILE_NAME = ".backup.json"

fun hasBackupFile(root: String = "~") = File(path(root, BACKUP_FILE_NAME)).exists()

fun createBackupFile(root: String = "~") = File(path(root, BACKUP_FILE_NAME)).createNewFile()
