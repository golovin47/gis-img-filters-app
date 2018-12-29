package com.gis.featurecamera.presentation.ui.camerascreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gis.featurecamera.R
import com.gis.featurecamera.data.CameraController
import com.gis.featurecamera.databinding.FragmentCameraBinding
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.get

class CameraFragment : Fragment() {

  private var viewSubscription: Disposable? = null
  private var binding: FragmentCameraBinding? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    initBinding(inflater, container)
    prepareCameraController()

    return binding!!.root
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)
  }

  private fun prepareCameraController() {
    val cameraController: CameraController = get()
    cameraController.prepareCameraController(binding!!.textureView, lifecycle)
  }

  override fun onDestroyView() {
    binding = null
    viewSubscription?.dispose()
    super.onDestroyView()
  }
}