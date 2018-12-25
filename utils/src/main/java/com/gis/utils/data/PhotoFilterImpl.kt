package com.gis.utils.data

import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import com.gis.utils.domain.PhotoFilter
import com.gis.utils.domain.PhotoFilterCallback
import com.mukesh.imageproccessing.OnProcessingCompletionListener
import com.mukesh.imageproccessing.filters.*

typealias PhotoFilterLib = com.mukesh.imageproccessing.PhotoFilter

class PhotoFilterImpl(view: GLSurfaceView, photoFilterCallback: PhotoFilterCallback) : PhotoFilter {

  private val photoFilter: PhotoFilterLib

  init {
    photoFilter = PhotoFilterLib(view, object : OnProcessingCompletionListener {
      override fun onProcessingComplete(bitmap: Bitmap) {
        photoFilterCallback.filterApplied(bitmap)
      }
    })
  }

  override fun applyFilter(bitmap: Bitmap, filterName: String) {
    val filter: Filter =
      when (filterName) {
        "AutoFix" -> AutoFix()
        "Highlight" -> Highlight()
        "Brightness" -> Brightness()
        "Contrast" -> Contrast()
        "Cross Process" -> CrossProcess()
        "Documentary" -> Documentary()
        "Duo Tone" -> DuoTone()
        "Fill Light" -> FillLight()
        "Fisheye" -> FishEye()
        "Flip Horizontally" -> FlipHorizontally()
        "Flip Vertically" -> FlipVertically()
        "Grain" -> Grain()
        "Grayscale" -> Grayscale()
        "Lomoish" -> Lomoish()
        "Negative" -> Negative()
        "Posterize" -> Posterize()
        "Rotate" -> Rotate()
        "Saturate" -> Saturate()
        "Sepia" -> Sepia()
        "Sharpen" -> Sharpen()
        "Temperature" -> Temperature()
        "Tint" -> Tint()
        "Vignette" -> Vignette()
        else -> None()
      }

    photoFilter.applyEffect(bitmap, filter)
  }
}