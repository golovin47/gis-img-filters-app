package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.graphics.Bitmap

data class ApplyFilterState(
  val loading: Boolean = false,
  val currentBitmap: Bitmap? = null,
  val filters: List<FilterListItem> = emptyList(),
  val error: Throwable? = null)


sealed class ApplyFilterIntent {
  class InitBitmapAndGetThumbnails(val imagePath: String) : ApplyFilterIntent()
  class ChooseFilter(val name: String, val bitmap: Bitmap) : ApplyFilterIntent()
}


sealed class ApplyFilterStateChange {
  object Loading : ApplyFilterStateChange()
  class BitmapAndThumbnailsReceived(val bitmap: Bitmap, val filters: List<FilterListItem>) : ApplyFilterStateChange()
  class FilterApplied(val bitmap: Bitmap) : ApplyFilterStateChange()
  class Error(val error: Throwable) : ApplyFilterStateChange()
  object HideError : ApplyFilterStateChange()
}


data class FilterListItem(
  val name: String = "",
  val image: Bitmap = Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888))


