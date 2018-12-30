package com.gis.featurecamera.presentation.ui.takenphotoscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gis.featurecamera.R
import com.gis.featurecamera.databinding.FragmentTakenPhotoBinding
import com.gis.featurecamera.presentation.ui.takenphotoscreen.TakenPhotoIntent.*
import com.gis.utils.BaseView
import com.gis.utils.domain.ImageLoader
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class TakenPhotoFragment : Fragment(), BaseView<TakenPhotoState> {

  private val imagePath: String by lazy { arguments!!.getString("imagePath") }

  private val imageLoader: ImageLoader by inject()

  private lateinit var currentState: TakenPhotoState
  private var viewSubscriptions: Disposable? = null
  private var binding: FragmentTakenPhotoBinding? = null
  private val vmTakenPhoto: TakenPhotoViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleStates()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    initBinding(inflater, container!!)
    initIntents()
    showPhoto()

    return binding!!.root
  }

  override fun onDestroyView() {
    disposeIntents()
    cleanBinding()
    super.onDestroyView()
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_taken_photo, container, false)
  }

  private fun cleanBinding() {
    binding = null
  }

  override fun initIntents() {
    viewSubscriptions = Observable.merge(listOf(

      RxView.clicks(binding!!.ivImg)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .map {
          if (currentState.showActions) HideActions
          else ShowActions
        },

      RxView.clicks(binding!!.ivDone)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .map { AcceptPhoto(imagePath) },

      RxView.clicks(binding!!.ivCancel)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .map { CancelPhoto }
    ))
      .subscribe(vmTakenPhoto.viewIntentsConsumer())
  }

  private fun disposeIntents() {
    viewSubscriptions?.dispose()
  }

  override fun handleStates() {
    vmTakenPhoto.stateReceived().observe(this, Observer { state -> render(state) })
  }

  private fun showPhoto() {
    imageLoader.loadImg(binding!!.ivImg, imagePath)
  }

  override fun render(state: TakenPhotoState) {
    currentState = state

    if (state.showActions) binding!!.takenPhotoRoot.transitionToStart()
    else binding!!.takenPhotoRoot.transitionToEnd()
  }
}