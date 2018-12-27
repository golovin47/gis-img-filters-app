package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.graphics.Bitmap
import android.net.Uri
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterIntent.*
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterStateChange.*
import com.gis.utils.BaseViewModel
import com.gis.utils.domain.entity.PhotoFilterThumbnail
import com.gis.utils.domain.interactors.ApplyFilterUseCase
import com.gis.utils.domain.interactors.GetThumbnailsUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ApplyFilterViewModel(
  private var bitmapFromImagePath: ((String) -> Observable<Bitmap>)?,
  private var saveImageToStorage: ((Bitmap) -> Completable)?,
  private var createTempImageFileAndGetUri: ((Bitmap) -> Observable<Uri>)?,
  private var goBack: (() -> Unit)?,
  private val getThumbnailsUseCase: GetThumbnailsUseCase,
  private val applyFilterUseCase: ApplyFilterUseCase)
  : BaseViewModel<ApplyFilterState>() {

  override fun initState(): ApplyFilterState = ApplyFilterState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(listOf(

      intentStream.ofType(HideActions::class.java)
        .map { ActionsHidden },

      intentStream.ofType(ShowActions::class.java)
        .map { ActionsShown },

      intentStream.ofType(InitBitmapAndGetThumbnails::class.java)
        .switchMap { event ->
          bitmapFromImagePath!!.invoke(event.imagePath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { bitmap ->
              getThumbnailsUseCase.execute(bitmap)
                .map { items -> BitmapAndThumbnailsReceived(bitmap, items.map { it.toPresentation() }) }
                .cast(ApplyFilterStateChange::class.java)
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
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        },

      intentStream.ofType(ApplyNoFilters::class.java)
        .switchMap { event ->
          bitmapFromImagePath!!.invoke(event.imagePath)
            .map { bitmap -> NoFiltersApplied(bitmap) }
            .cast(ApplyFilterStateChange::class.java)
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        },

      intentStream.ofType(SaveImage::class.java)
        .switchMap { event ->
          saveImageToStorage!!.invoke(event.bitmap)
            .andThen(
              Observable.just(ImageSaved, Idle)
                .doOnNext { if (it is Idle) goBack!!.invoke() })
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        },

      intentStream.ofType(ShareImage::class.java)
        .switchMap { event ->
          createTempImageFileAndGetUri!!.invoke(event.bitmap)
            .flatMap { uri -> Observable.just(FileToShareImageReceived(uri), Idle) }
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        }
    ))

  private fun handleError(e: Throwable): Observable<ApplyFilterStateChange> =
    Observable.just(Error(e), HideError)

  override fun reduceState(previousState: ApplyFilterState, stateChange: Any): ApplyFilterState =
    when (stateChange) {

      is Idle -> previousState.copy(showImageSaved = false, uriToShareImage = null, error = null)

      is ActionsShown -> previousState.copy(showActions = true)

      is ActionsHidden -> previousState.copy(showActions = false)

      is BitmapAndThumbnailsReceived -> previousState.copy(
        currentBitmap = previousState.currentBitmap ?: stateChange.bitmap,
        filters = stateChange.filters.toMutableList().apply {
          add(0, FilterListItem("No Filters"))
        })

      is NoFiltersApplied -> previousState.copy(currentBitmap = stateChange.bitmap)

      is FilterApplied -> previousState.copy(currentBitmap = stateChange.bitmap)

      is ImageSaved -> previousState.copy(showImageSaved = true)

      is FileToShareImageReceived -> previousState.copy(uriToShareImage = stateChange.uri)

      is Error -> previousState.copy(error = stateChange.error)

      is HideError -> previousState.copy(error = null)

      else -> previousState
    }

  override fun onCleared() {
    bitmapFromImagePath = null
    saveImageToStorage = null
    createTempImageFileAndGetUri = null
    goBack = null
    super.onCleared()
  }
}


private fun PhotoFilterThumbnail.toPresentation() =
  FilterListItem(name = name, image = image)