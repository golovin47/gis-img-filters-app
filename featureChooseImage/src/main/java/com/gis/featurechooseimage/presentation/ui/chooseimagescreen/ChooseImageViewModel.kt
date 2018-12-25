package com.gis.featurechooseimage.presentation.ui.chooseimagescreen

import com.gis.featurechooseimage.presentation.ui.chooseimagescreen.ChooseImageIntent.*
import com.gis.featurechooseimage.presentation.ui.chooseimagescreen.ChooseImageStateChange.*
import com.gis.utils.BaseViewModel
import io.reactivex.Observable

class ChooseImageViewModel(var goToApplyFilter: ((String) -> Unit)?) : BaseViewModel<ChooseImageState>() {

  override fun initState(): ChooseImageState = ChooseImageState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(listOf(
      intentStream.ofType(ChooseImage::class.java)
        .map { Loading },

      intentStream.ofType(ImageCancelled::class.java)
        .map { Cancelled },

      intentStream.ofType(ChooseImageIntent.ImagePathCreated::class.java)
        .map { event -> ChooseImageStateChange.ImagePathCreated(event.imagePath) },

      intentStream.ofType(ImageChosen::class.java)
        .doOnNext { event -> goToApplyFilter?.invoke(event.imagePath) }
        .map { Cancelled }
    ))

  override fun reduceState(previousState: ChooseImageState, stateChange: Any): ChooseImageState =
    when (stateChange) {
      is Loading -> previousState.copy(loading = true, error = null)

      is Cancelled -> previousState.copy(loading = false, imagePath = "", error = null)

      is ChooseImageStateChange.ImagePathCreated -> previousState.copy(imagePath = stateChange.imagePath)

      is Error -> previousState.copy(loading = false, error = stateChange.error)

      is HideError -> previousState.copy(error = null)

      else -> previousState
    }

  override fun onCleared() {
    goToApplyFilter = null
    super.onCleared()
  }
}