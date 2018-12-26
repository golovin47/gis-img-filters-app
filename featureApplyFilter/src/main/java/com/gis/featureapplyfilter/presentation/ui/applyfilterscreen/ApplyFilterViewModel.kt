package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.graphics.Bitmap
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterIntent.ChooseFilter
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterIntent.InitBitmapAndGetThumbnails
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterStateChange.*
import com.gis.utils.BaseViewModel
import com.gis.utils.domain.entity.PhotoFilterThumbnail
import com.gis.utils.domain.interactors.ApplyFilterUseCase
import com.gis.utils.domain.interactors.GetThumbnailsUseCase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ApplyFilterViewModel(
  private var bitmapFromImagePath: ((String) -> Bitmap)?,
  private val getThumbnailsUseCase: GetThumbnailsUseCase,
  private val applyFilterUseCase: ApplyFilterUseCase)
  : BaseViewModel<ApplyFilterState>() {

  override fun initState(): ApplyFilterState = ApplyFilterState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(listOf(
      intentStream.ofType(InitBitmapAndGetThumbnails::class.java)
        .switchMap { event ->
          Observable.fromCallable { bitmapFromImagePath?.invoke(event.imagePath) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { bitmap ->
              getThumbnailsUseCase.execute(bitmap)
                .map { items -> BitmapAndThumbnailsReceived(bitmap, items.map { it.toPresentation() }) }
                .cast(ApplyFilterStateChange::class.java)
                .startWith(Loading)
                .onErrorResumeNext { e: Throwable -> handleError(e) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
            }
        },

      intentStream.ofType(ChooseFilter::class.java)
        .switchMap { event ->
          applyFilterUseCase.execute(event.bitmap, event.name)
            .map { bitmap -> FilterApplied(bitmap) }
            .cast(ApplyFilterStateChange::class.java)
            .startWith(Loading)
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        }
    ))

  private fun handleError(e: Throwable): Observable<ApplyFilterStateChange> =
    Observable.just(Error(e), HideError)

  override fun reduceState(previousState: ApplyFilterState, stateChange: Any): ApplyFilterState =
    when (stateChange) {
      is Loading -> previousState.copy(loading = true, error = null)

      is BitmapAndThumbnailsReceived -> previousState.copy(
        loading = false,
        currentBitmap = previousState.currentBitmap ?: stateChange.bitmap,
        filters = stateChange.filters)

      is FilterApplied -> previousState.copy(loading = false, currentBitmap = stateChange.bitmap)

      is Error -> previousState.copy(loading = false, error = stateChange.error)

      is HideError -> previousState.copy(error = null)

      else -> previousState
    }

  override fun onCleared() {
    bitmapFromImagePath = null
    super.onCleared()
  }
}


private fun PhotoFilterThumbnail.toPresentation() =
  FilterListItem(name = name, image = image)