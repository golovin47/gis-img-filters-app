package com.gis.featureapplyfilter.presentation.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gis.featureapplyfilter.R
import com.gis.featureapplyfilter.databinding.FragmentApplyFilterBinding
import com.gis.utils.domain.ImageLoader
import org.koin.android.ext.android.inject

class ApplyFilterFragment : Fragment() {

  private val imageLoader: ImageLoader by inject()
  private var binding: FragmentApplyFilterBinding? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    initBinding(inflater, container)
    showPhoto(bitmapFromFilePath(arguments!!.getString("imagePath")!!))

    imageLoader.loadImg(binding!!.ivImg, arguments!!.getString("imagePath")!!)

    return binding!!.root
  }

  override fun onDestroyView() {
    binding = null
    super.onDestroyView()
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply_filter, container, false)
  }

  private fun showPhoto(bitmap: Bitmap) {
    imageLoader.loadBitmap(binding!!.ivImg, bitmap)
  }

  private fun bitmapFromFilePath(path: String): Bitmap =
    BitmapFactory.decodeFile(path, BitmapFactory.Options())
}