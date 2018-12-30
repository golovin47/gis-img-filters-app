package com.gis.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.gis.utils.domain.entity.FileUriAndPath
import io.reactivex.Completable
import io.reactivex.Observable
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


fun bitmapFromFilePath(path: String): Observable<Bitmap> =
  Observable.fromCallable {
    var rotate = 0
    val imageFile = File(path)
    val exif = ExifInterface(imageFile.absolutePath)
    val orientation = exif.getAttributeInt(
      ExifInterface.TAG_ORIENTATION,
      ExifInterface.ORIENTATION_NORMAL)

    when (orientation) {
      ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
      ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
      ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
    }
    val matrix = Matrix();
    matrix.postRotate(rotate.toFloat())
    val scaled = BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inMutable = true })
    return@fromCallable Bitmap.createBitmap(scaled, 0, 0, scaled.width, scaled.height, matrix, true)
  }


fun getPathFromUri(uri: Uri, context: Context): Observable<String> =
  Observable.fromCallable {
    var resultPath = ""
    val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, proj, null, null, null)
    if (cursor != null && cursor.moveToFirst()) {
      val colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
      resultPath = cursor.getString(colIndex)
    }
    cursor?.close()

    return@fromCallable resultPath
  }


fun createTempFileForPhoto(context: Context): Observable<File> =
  Observable.fromCallable {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "PhotoFilterImg_" + timestamp + "_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return@fromCallable File.createTempFile(fileName, ".jpg", storageDir)
  }


fun createTempFileForPhotoAndGetUri(context: Context): Observable<Uri> =
  Observable.fromCallable {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "PhotoFilterImg_" + timestamp + "_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(fileName, ".jpg", storageDir)
    FileProvider.getUriForFile(context, "com.gis.imgfiltersapp.provider", file)
  }


fun getUriAndFilePath(context: Context): Observable<FileUriAndPath> =
  Observable.fromCallable {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "PhotoFilterImg_" + timestamp + "_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(fileName, ".jpg", storageDir)
    val uri = FileProvider.getUriForFile(context, "com.gis.imgfiltersapp.provider", file)
    return@fromCallable FileUriAndPath(uri, file.absolutePath)
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


fun createTempImageFileAndGetUri(bitmap: Bitmap, context: Context): Observable<Uri> =
  Observable.fromCallable {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "PhotoFilterImg_" + timestamp + "_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(fileName, ".jpg", storageDir)
    val fos = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.flush()
    fos.close()
    return@fromCallable FileProvider.getUriForFile(context, "com.gis.imgfiltersapp.provider", file)
  }


fun saveImageToStorage(bitmap: Bitmap, context: Context): Completable =
  createTempImageFile(bitmap, context).concatMapCompletable { file ->
    Completable.fromAction {
      MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, file.name, file.name)
    }
  }

