package com.example.running_data_collecting_app.utils

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream

fun saveCsv(context: Context, dir: List<String>, fileName: String, content: String) {
    val dirPath = createDirectory(dir)
    val file = File(
        "$dirPath$fileName.csv"
    )
    if (file.exists()) {
        file.delete()
    }
    try {
        val out = FileOutputStream(file)
        out.write(content.toByteArray())
        out.flush()
        out.close()
        saveCsvToMediaStore(context, file, fileName)
        Log.d("debug", "saved:$dirPath$fileName.csv")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun saveCsvToMediaStore(context: Context, file: File, displayName: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
    }

    val uri =
        context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
    context.contentResolver.openOutputStream(uri!!).use { outputStream ->
        file.inputStream().copyTo(outputStream!!)
        outputStream.flush()
    }
}

fun createDirectory(dir: List<String>): String {
    val path = dir.joinToString(separator = "/")
    val mediaDir = Environment.getExternalStorageDirectory()
        .toString() + "/Android/media/com.example.running-data-collecting-app/"
    val dirPath = "$mediaDir$path/"
    Log.d("debug", "createDirectory: $dirPath")
    val dir = File(dirPath)
    if (!dir.exists()) {
        Log.d("debug", "dir not exist")
        try {
            val result = dir.mkdirs()
            Log.d("debug", "dir.mkdirs(): $result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return dirPath
}