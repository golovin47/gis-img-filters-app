package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.graphics.Bitmap
import android.net.Uri

data class ApplyFilterState(
  val showActions: Boolean = true,
  val showImageSaved: Boolean = false,
  val uriToShareImage: Uri? = null,
  val currentBitmap: Bitmap? = null,
  val filters: List<FilterListItem> = emptyList(),
  val error: Throwable? = null)


sealed class ApplyFilterIntent {
  object ShowActions : ApplyFilterIntent()
  object HideActions : ApplyFilterIntent()
  class InitBitmapAndGetThumbnails(val imagePath: String) : ApplyFilterIntent()
  class ChooseFilter(val name: String, val bitmap: Bitmap) : ApplyFilterIntent()
  class ApplyNoFilters(val imagePath: String) : ApplyFilterIntent()
  class SaveImage(val bitmap: Bitmap) : ApplyFilterIntent()
  class ShareImage(val bitmap: Bitmap) : ApplyFilterIntent()
}


sealed class ApplyFilterStateChange {
  object Idle : ApplyFilterStateChange()
  object ActionsShown : ApplyFilterStateChange()
  object ActionsHidden : ApplyFilterStateChange()
  class BitmapAndThumbnailsReceived(val bitmap: Bitmap, val filters: List<FilterListItem>) : ApplyFilterStateChange()
  class FilterApplied(val bitmap: Bitmap) : ApplyFilterStateChange()
  class NoFiltersApplied(val bitmap: Bitmap) : ApplyFilterStateChange()
  object ImageSaved : ApplyFilterStateChange()
  class FileToShareImageReceived(val uri: Uri) : ApplyFilterStateChange()
  class Error(val error: Throwable) : ApplyFilterStateChange()
  object HideError : ApplyFilterStateChange()
}


data class FilterListItem(
  val name: String = "",
  val image: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))


