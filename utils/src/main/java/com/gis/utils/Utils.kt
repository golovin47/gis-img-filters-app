package com.gis.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import io.reactivex.Completable
import io.reactivex.Observable
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun bitmapFromFilePath(path: String): Observable<Bitmap> =
  Observable.fromCallable {
    BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inMutable = true })
  }


fun createTempImageFile(bitmap: Bitmap, context: Context): Observable<File> =
  Observable.fromCallable {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "PhotoFilterImg_" + timestamp + "_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(fileName, ".jpg", storageDir)
    val fos = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.flush()
    fos.close()
    return@fromCallable file
  }


fun saveImageToStorage(bitmap: Bitmap, context: Context): Completable =
  createTempImageFile(bitmap, context).concatMapCompletable { file ->
    Completable.fromAction {
      MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, file.name, file.name)
    }
  }
//  Completable. {
//    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//    val fileName = "PhotoFilterImg_" + timestamp + "_"
//    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//    val file = File.createTempFile(fileName, ".jpg", storageDir)
//    val fos = FileOutputStream(file)
//    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//    fos.flush()
//    fos.close()
//
//  }

