package com.gis.utils.domain.interactors

import android.graphics.Bitmap
import com.gis.utils.domain.PhotoFilterManager

class GetThumbnailsUseCase(private val filterManager: PhotoFilterManager) {

  fun execute(bitmap: Bitmap) = filterManager.getFilterThumbnails(bitmap)
}


class ApplyFilterUseCase(private val filterManager: PhotoFilterManager) {

  fun execute(bitmap: Bitmap, filterName: String) = filterManager.applyFilter(bitmap, filterName)
}