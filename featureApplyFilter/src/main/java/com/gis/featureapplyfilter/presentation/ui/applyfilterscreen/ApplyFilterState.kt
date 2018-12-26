package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.graphics.Bitmap

data class ApplyFilterState(
  val showActions: Boolean = true,
  val currentBitmap: Bitmap? = null,
  val filters: List<FilterListItem> = emptyList(),
  val error: Throwable? = null)


sealed class ApplyFilterIntent {
  object ShowActions : ApplyFilterIntent()
  object HideActions : ApplyFilterIntent()
  class InitBitmapAndGetThumbnails(val imagePath: String) : ApplyFilterIntent()
  class ChooseFilter(val name: String, val bitmap: Bitmap) : ApplyFilterIntent()
  class ApplyNoFilters(val imagePath: String) : ApplyFilterIntent()
  object SaveImage : ApplyFilterIntent()
}


sealed class ApplyFilterStateChange {
  object ActionsShown : ApplyFilterStateChange()
  object ActionsHidden : ApplyFilterStateChange()
  class BitmapAndThumbnailsReceived(val bitmap: Bitmap, val filters: List<FilterListItem>) : ApplyFilterStateChange()
  class FilterApplied(val bitmap: Bitmap) : ApplyFilterStateChange()
  class NoFiltersApplied(val bitmap: Bitmap) : ApplyFilterStateChange()
  class Error(val error: Throwable) : ApplyFilterStateChange()
  object HideError : ApplyFilterStateChange()
}


data class FilterListItem(
  val name: String = "",
  val image: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))


