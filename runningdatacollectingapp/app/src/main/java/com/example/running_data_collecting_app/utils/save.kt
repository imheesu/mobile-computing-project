package com.example.running_data_collecting_app.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

fun saveCsv(context: Context, dir: List<String>, fileName: String, content: String) {
    Log.d("debug", "saveCsv runned")
    val directory = createDirectory(context, dir)
    val file = File(directory, "$fileName.csv")
    if (file.exists()) {
        file.delete()
    }
    Log.d("debug", "Trying to save csv")
    try {
        val out = FileOutputStream(file)
        out.write(content.toByteArray())
        out.flush()
        out.close()
        saveCsvToMediaStore(context, file, fileName)
        Log.d("debug", "saved:${file.path}")
    } catch (e: Exception) {
        Log.d("debug", "saveCsv: error")
        e.printStackTrace()
    }
}

fun saveCsvToMediaStore(context: Context, file: File, displayName: String) {
    context.openFileOutput(displayName, Context.MODE_PRIVATE).use { outputStream ->
        file.inputStream().copyTo(outputStream)
        outputStream.flush()
    }

//    val uri =
//        context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
//    context.contentResolver.openOutputStream(uri!!).use { outputStream ->
//        file.inputStream().copyTo(outputStream!!)
//        outputStream.flush()
//    }
}

fun createDirectory(context:Context, dir: List<String>): File {
//    val path = dir.joinToString(separator = "/")
//    val mediaDir = Environment.getExternalStorageDirectory()
//        .toString() + "/Android/media/com.example.running-data-collecting-app/"
//    val dirPath = "$mediaDir$path/"
//    val dirPath = context.filesDir
//    Log.d("debug", "createDirectory: $dirPath")
//    val dir = File(dirPath)
//    if (!dir.exists()) {
//        Log.d("debug", "dir not exist")
//        try {
//            val result = dir.mkdirs()
//            Log.d("debug", "dir.mkdirs(): $result")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
    // create directory in internal storage
//    val file = context.getExternalFilesDir("good_running_posture")
    val directory = File(context.filesDir, dir.joinToString(separator = File.separator))
    if (!directory.exists()) {
        Log.d("debug", "dir not exist")
        try {
            val result = directory.mkdirs()
            Log.d("debug", "dir.mkdirs(): $result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
//    val file = context.getDir(dir.joinToString(separator = File.separator), Context.MODE_PRIVATE)
    return directory
}