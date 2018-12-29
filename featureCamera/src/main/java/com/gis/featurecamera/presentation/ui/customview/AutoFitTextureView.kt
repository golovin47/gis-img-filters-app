package com.gis.featurecamera.presentation.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

class AutoFitTextureView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr) {

  private var ratioWidth = 0
  private var ratioHeight = 0

  fun setAspectRatio(width: Int, height: Int) {
    if (width < 0 || height < 0) throw IllegalArgumentException("Size cannot be negative.")

    ratioWidth = width
    ratioHeight = height
    requestLayout()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)
    if (ratioWidth == 0 || ratioHeight == 0)
      setMeasuredDimension(width, height)
    else
      if (width < height * ratioWidth / ratioHeight)
        setMeasuredDimension(width, width * ratioHeight / ratioWidth)
      else
        setMeasuredDimension(height * ratioWidth / ratioHeight, height)
  }
}