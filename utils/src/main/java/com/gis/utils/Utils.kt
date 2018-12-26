package com.gis.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun bitmapFromFilePath(path: String): Bitmap =
  BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inMutable = true })