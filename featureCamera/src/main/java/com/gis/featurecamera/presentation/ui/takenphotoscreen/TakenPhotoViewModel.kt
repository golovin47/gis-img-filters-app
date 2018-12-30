package com.gis.featurecamera.presentation.ui.takenphotoscreen

import com.gis.featurecamera.presentation.ui.takenphotoscreen.TakenPhotoIntent.AcceptPhoto
import com.gis.featurecamera.presentation.ui.takenphotoscreen.TakenPhotoIntent.CancelPhoto
import com.gis.utils.BaseViewModel
import io.reactivex.Observable

class TakenPhotoViewModel(
  private var goBack: (() -> Unit)?,
  private var acceptPhotoAndNavigate: ((String) -> Unit)?) : BaseViewModel<TakenPhotoState>() {

  override fun initState(): TakenPhotoState = TakenPhotoState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(listOf(

      intentStream.ofType(TakenPhotoIntent.ShowActions::class.java)
        .map { TakenPhotoStateChange.ShowActions },

      intentStream.ofType(TakenPhotoIntent.HideActions::class.java)
        .map { TakenPhotoStateChange.HideActions },

      intentStream.ofType(AcceptPhoto::class.java)
        .doOnNext { event -> acceptPhotoAndNavigate!!.invoke(event.imagePath) },

      intentStream.ofType(CancelPhoto::class.java)
        .doOnNext { goBack!!.invoke() }
    ))

  override fun reduceState(previousState: TakenPhotoState, stateChange: Any): TakenPhotoState =
    when (stateChange) {

      is TakenPhotoStateChange.ShowActions -> previousState.copy(showActions = true)

      is TakenPhotoStateChange.HideActions -> previousState.copy(showActions = false)

      else -> previousState
    }

  override fun onCleared() {
    goBack = null
    acceptPhotoAndNavigate = null
    super.onCleared()
  }
}