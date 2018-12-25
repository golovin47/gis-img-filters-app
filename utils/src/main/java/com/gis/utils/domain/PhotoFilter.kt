package com.gis.utils.domain

import android.graphics.Bitmap

interface PhotoFilter {

    fun applyFilter(bitmap: Bitmap, filterName: String)
}

interface PhotoFilterCallback {

    fun filterApplied(bitmap: Bitmap)
}