package com.gis.utils.domain

import android.graphics.Bitmap
import android.widget.ImageView

interface ImageLoader {

  fun loadImg(iv:ImageView, url: String)

  fun loadBitmap(iv:ImageView, bitmap:Bitmap)
}