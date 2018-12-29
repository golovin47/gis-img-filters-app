package com.gis.featurecamera.presentation.ui.camerascreen

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gis.featurecamera.R
import com.gis.featurecamera.databinding.FragmentCameraBinding
import com.gis.featurecamera.presentation.ui.camerascreen.CameraIntent.*
import com.gis.utils.BaseView
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(), BaseView<CameraState> {

  private var viewSubscription: Disposable? = null
  private var binding: FragmentCameraBinding? = null
  private val vmCamera: CameraViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleStates()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    lockPortraitScreenOrientation()
    initBinding(inflater, container)
    initIntents()

    return binding!!.root
  }

  override fun onDestroyView() {
    cleanBinding()
    disposeIntents()
    unlockScreenOrientation()

    super.onDestroyView()
  }

  private fun lockPortraitScreenOrientation() {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
  }

  private fun unlockScreenOrientation() {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)
  }

  private fun cleanBinding() {
    binding = null
  }

  @SuppressLint("CheckResult")
  override fun initIntents() {
    Observable.merge(listOf(

      Observable.just(PrepareCamera(binding!!.textureView, lifecycle)),

      RxView.clicks(binding!!.btnTakePhoto)
        .throttleFirst(1000, TimeUnit.MILLISECONDS)
        .map { TakePhoto },

      RxView.clicks(binding!!.btnSwitchCamera)
        .throttleFirst(1000, TimeUnit.MILLISECONDS)
        .map { SwitchCamera }
    ))
      .subscribe(vmCamera.viewIntentsConsumer())
  }

  private fun disposeIntents() {
    viewSubscription?.dispose()
  }

  override fun handleStates() {
    vmCamera.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: CameraState) {
    if (state.focusState is FocusState.FocusStarted) {
      binding!!.ivFocus.visibility = View.VISIBLE
      binding!!.ivFocus.animate().scaleX(2f).scaleY(2f).setDuration(300)
    } else {
      binding!!.ivFocus.visibility = View.GONE
    }

    if (state.error != null)
      Snackbar.make(binding!!.root, state.error.message.toString(), Snackbar.LENGTH_SHORT).show()
  }
}