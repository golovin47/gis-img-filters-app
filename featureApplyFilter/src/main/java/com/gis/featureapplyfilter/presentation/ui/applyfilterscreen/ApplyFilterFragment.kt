package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gis.featureapplyfilter.R
import com.gis.featureapplyfilter.databinding.FragmentApplyFilterBinding
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterIntent.*
import com.gis.utils.BaseView
import com.gis.utils.domain.ImageLoader
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.concurrent.TimeUnit

class ApplyFilterFragment : Fragment(), BaseView<ApplyFilterState> {

  private val imagePath: String by lazy(LazyThreadSafetyMode.NONE) {
    arguments!!.getString("imagePath")
  }

  private lateinit var currentState: ApplyFilterState
  private val filterClicksPublisher = PublishSubject.create<String>()
  private var viewSubscription: Disposable? = null

  private val imageLoader: ImageLoader by inject()
  private var binding: FragmentApplyFilterBinding? = null

  private val vmApplyFilter: ApplyFilterViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleStates()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    initBinding(inflater, container)
    initRecyclerView()
    initIntents()

    return binding!!.root
  }

  override fun onDestroyView() {
    binding = null
    viewSubscription?.dispose()
    super.onDestroyView()
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply_filter, container, false)
  }

  private fun initRecyclerView() {
    binding!!.rvFilters.apply {
      layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
      adapter = FiltersAdapter(filterClicksPublisher, imageLoader)
      addItemDecoration(DividerItemDecoration(context!!, RecyclerView.HORIZONTAL))
    }
  }

  private fun showPhoto(bitmap: Bitmap) {
    imageLoader.loadBitmap(binding!!.ivImg, bitmap)
  }

  private fun sharePhoto(file: File) {
    val uri = FileProvider.getUriForFile(context!!, "com.gis.imgfiltersapp.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "image/jpg"
      putExtra(Intent.EXTRA_STREAM, uri)
    }
    startActivity(Intent.createChooser(intent, getString(R.string.share_image)))
  }

  override fun initIntents() {
    viewSubscription = Observable.merge(listOf(
      Observable.just(InitBitmapAndGetThumbnails(imagePath)),

      RxView.clicks(binding!!.ivImg)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .map {
          if (currentState.showActions) HideActions
          else ShowActions
        },

      RxView.clicks(binding!!.ivDone)
        .throttleFirst(1000, TimeUnit.MILLISECONDS)
        .map { SaveImage(currentState.currentBitmap!!) },

      RxView.clicks(binding!!.ivShare)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .map { ShareImage(currentState.currentBitmap!!) },

      filterClicksPublisher
        .map { name ->
          if (name == getString(R.string.no_filters)) ApplyNoFilters(imagePath)
          else ChooseFilter(name, currentState.currentBitmap!!)
        }
    ))
      .subscribe(vmApplyFilter.viewIntentsConsumer())
  }

  override fun handleStates() {
    vmApplyFilter.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: ApplyFilterState) {
    currentState = state

    if (state.fileToShareImage != null) sharePhoto(state.fileToShareImage)

    if (state.showImageSaved)
      Toast.makeText(context!!, R.string.image_saved, Toast.LENGTH_SHORT).show()

    if (state.showActions) binding!!.applyFilterRoot.transitionToStart()
    else binding!!.applyFilterRoot.transitionToEnd()

    if (state.currentBitmap != null) showPhoto(state.currentBitmap)

    (binding!!.rvFilters.adapter as FiltersAdapter).submitList(state.filters)

    if (state.error != null)
      Snackbar.make(binding!!.root, state.error.message.toString(), Snackbar.LENGTH_SHORT).show()
  }
}