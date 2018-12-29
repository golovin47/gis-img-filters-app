package com.gis.featurechooseimage.presentation.ui.chooseimagescreen

import android.net.Uri
import com.gis.featurechooseimage.presentation.ui.chooseimagescreen.ChooseImageIntent.GalleryImageChosen
import com.gis.featurechooseimage.presentation.ui.chooseimagescreen.ChooseImageIntent.ImageCancelled
import com.gis.featurechooseimage.presentation.ui.chooseimagescreen.ChooseImageStateChange.*
import com.gis.utils.BaseViewModel
import com.gis.utils.domain.entity.FileUriAndPath
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChooseImageViewModel(
  private var getPathFromUri: ((Uri) -> Observable<String>)?,
  private var getUriAndFilePath: (() -> Observable<FileUriAndPath>)?,
  private var goToCamera: (() -> Unit)?,
  private var goToApplyFilter: ((String) -> Unit)?) : BaseViewModel<ChooseImageState>() {

  override fun initState(): ChooseImageState = ChooseImageState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(listOf(

      intentStream.ofType(ChooseImageIntent.OpenCamera::class.java)
        .switchMap { event ->
          getUriAndFilePath!!.invoke()
            .doOnNext { goToCamera!!.invoke() }
            .map { Idle }
            .cast(ChooseImageStateChange::class.java)
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        },

      intentStream.ofType(ChooseImageIntent.OpenGallery::class.java)
        .flatMap { Observable.just(ChooseImageStateChange.OpenGallery, Idle) },

      intentStream.ofType(ChooseImageIntent.RequestPermissionsForCamera::class.java)
        .flatMap { Observable.just(ChooseImageStateChange.RequestPermissionsForCamera, Idle) },

      intentStream.ofType(ChooseImageIntent.RequestPermissionsForGallery::class.java)
        .flatMap { Observable.just(ChooseImageStateChange.RequestPermissionsForGallery, Idle) },

      intentStream.ofType(ChooseImageIntent.ShowExtraPermissionsDialog::class.java)
        .map { ChooseImageStateChange.ShowExtraPermissionsDialog },

      intentStream.ofType(ChooseImageIntent.DismissExtraPermissionsDialog::class.java)
        .map { ChooseImageStateChange.DismissExtraPermissionsDialog },

      intentStream.ofType(ChooseImageIntent.GoToAppSettings::class.java)
        .flatMap { Observable.just(ChooseImageStateChange.GoToAppSettings, Idle) },

      intentStream.ofType(ImageCancelled::class.java)
        .map { Idle },

      intentStream.ofType(GalleryImageChosen::class.java)
        .switchMap { event ->
          getPathFromUri!!.invoke(event.uri)
            .doOnNext { goToApplyFilter?.invoke(it) }
            .map { Idle }
            .cast(ChooseImageStateChange::class.java)
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        }
    ))

  private fun handleError(e: Throwable): Observable<ChooseImageStateChange> =
    Observable.just(Error(e), HideError)

  override fun reduceState(previousState: ChooseImageState, stateChange: Any): ChooseImageState =
    when (stateChange) {
      is Idle -> previousState.copy(
        openGallery = false,
        requestPermissionsForCamera = false,
        requestPermissionsForGallery = false,
        showExtraPermissionsDialog = false,
        goToAppSettings = false,
        error = null)

      is ChooseImageStateChange.OpenGallery -> previousState.copy(openGallery = true)

      is ChooseImageStateChange.RequestPermissionsForCamera -> previousState.copy(
        requestPermissionsForCamera = true,
        requestPermissionsForGallery = false)

      is ChooseImageStateChange.RequestPermissionsForGallery -> previousState.copy(
        requestPermissionsForGallery = true,
        requestPermissionsForCamera = false)

      is ChooseImageStateChange.ShowExtraPermissionsDialog -> previousState.copy(
        requestPermissionsForCamera = false,
        requestPermissionsForGallery = false,
        showExtraPermissionsDialog = true)

      is ChooseImageStateChange.DismissExtraPermissionsDialog -> previousState.copy(
        showExtraPermissionsDialog = false)

      is ChooseImageStateChange.GoToAppSettings -> previousState.copy(
        goToAppSettings = true)

      is Error -> previousState.copy(error = stateChange.error)

      is HideError -> previousState.copy(error = null)

      else -> previousState
    }

  override fun onCleared() {
    goToCamera = null
    goToApplyFilter = null
    getPathFromUri = null
    getUriAndFilePath = null
    super.onCleared()
  }
}