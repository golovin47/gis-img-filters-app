package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gis.featureapplyfilter.R
import com.gis.featureapplyfilter.databinding.FragmentApplyFilterBinding
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterIntent.ChooseFilter
import com.gis.utils.BaseView
import com.gis.utils.domain.PhotoFilter
import com.gis.utils.domain.PhotoFilterCallback
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ApplyFilterFragment : Fragment(), BaseView<ApplyFilterState> {

  private lateinit var currentState: ApplyFilterState
  private val eventsPublisher = PublishSubject.create<ApplyFilterIntent>()
  private var viewSubscription: Disposable? = null

  private var photoFilter: PhotoFilter? = null
  private var binding: FragmentApplyFilterBinding? = null

  private val vmApplyFilter: ApplyFilterViewModel by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleStates()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    initBinding(inflater, container)
    initRecyclerView()
    initPhotoFilter()
    initIntents()
    showPhoto(bitmapFromFilePath(arguments!!.getString("imagePath")!!))

    return binding!!.root
  }

  override fun onResume() {
    super.onResume()
    binding!!.glsvImg.onResume()
  }

  override fun onPause() {
    super.onPause()
    binding!!.glsvImg.onPause()
  }

  override fun onDestroyView() {
    binding = null
    photoFilter = null
    viewSubscription?.dispose()
    super.onDestroyView()
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply_filter, container, false)
  }

  private fun initRecyclerView() {
    binding!!.rvFilters.apply {
      layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
      adapter = FiltersAdapter(eventsPublisher)
    }
  }

  private fun initPhotoFilter() {
    photoFilter = get {
      parametersOf(binding!!.glsvImg, object : PhotoFilterCallback {
        override fun filterApplied(bitmap: Bitmap) {
          eventsPublisher.onNext(ApplyFilterIntent.FilterApplied(bitmap))
        }
      })
    }
  }

  private fun showPhoto(bitmap: Bitmap) {
    photoFilter!!.applyFilter(bitmap, "")
  }

  private fun bitmapFromFilePath(path: String): Bitmap =
    BitmapFactory.decodeFile(path, BitmapFactory.Options())

  override fun initIntents() {
    viewSubscription = Observable.merge(listOf(
      eventsPublisher
        .ofType(ChooseFilter::class.java)
        .doOnNext { event ->
          if (event.name == "None")
            showPhoto(bitmapFromFilePath(arguments!!.getString("imagePath")!!))
          else
            photoFilter!!.applyFilter(currentState.currentBitmap!!, event.name)

        },

      eventsPublisher
        .ofType(ApplyFilterIntent.FilterApplied::class.java)
    ))
      .subscribe(vmApplyFilter.viewIntentsConsumer())
  }

  override fun handleStates() {
    vmApplyFilter.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: ApplyFilterState) {
    currentState = state

    if (state.loading) binding!!.applyFilterRoot?.transitionToEnd()
    else binding!!.applyFilterRoot?.transitionToStart()

    (binding!!.rvFilters.adapter as FiltersAdapter).submitList(state.filters)
  }
}