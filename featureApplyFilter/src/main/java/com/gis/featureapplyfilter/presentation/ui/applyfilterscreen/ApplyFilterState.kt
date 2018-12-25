package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.graphics.Bitmap

data class ApplyFilterState(
  val loading: Boolean = false,
  val currentBitmap: Bitmap? = null,
  val filters: List<FilterListItem> = getAllFilters(),
  val error: Throwable? = null) {

  private companion object {
    fun getAllFilters() = listOf(
      FilterListItem("None", true),
      FilterListItem("AutoFix", false),
      FilterListItem("Highlight", false),
      FilterListItem("Brightness", false),
      FilterListItem("Contrast", false),
      FilterListItem("Cross Process", false),
      FilterListItem("Documentary", false),
      FilterListItem("Duo Tone", false),
      FilterListItem("Fill Light", false),
      FilterListItem("Fisheye", false),
      FilterListItem("Flip Horizontally", false),
      FilterListItem("Flip Vertically", false),
      FilterListItem("Grain", false),
      FilterListItem("Grayscale", false),
      FilterListItem("Lomoish", false),
      FilterListItem("Negative", false),
      FilterListItem("Posterize", false),
      FilterListItem("Rotate", false),
      FilterListItem("Saturate", false),
      FilterListItem("Sepia", false),
      FilterListItem("Sharpen", false),
      FilterListItem("Temperature", false),
      FilterListItem("Tint", false),
      FilterListItem("Vignette", false)
    )
  }
}


sealed class ApplyFilterIntent {
  class ChooseFilter(val name: String) : ApplyFilterIntent()
  class FilterApplied(val bitmap: Bitmap) : ApplyFilterIntent()
}


sealed class ApplyFilterStateChange {
  class FilterChosen(val name: String) : ApplyFilterStateChange()
  class FilterApplied(val bitmap: Bitmap) : ApplyFilterStateChange()
  class Error(val error: Throwable) : ApplyFilterStateChange()
  object HideError : ApplyFilterStateChange()
}


