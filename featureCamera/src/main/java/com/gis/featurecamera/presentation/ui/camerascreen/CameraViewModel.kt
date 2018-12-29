package com.gis.featurecamera.presentation.ui.camerascreen

import com.gis.featurecamera.domain.entity.CameraControllerEvent
import com.gis.featurecamera.domain.entity.CameraControllerEvent.*
import com.gis.featurecamera.domain.interactors.ObserveCameraEventsUseCase
import com.gis.featurecamera.domain.interactors.PrepareCameraControllerUseCase
import com.gis.featurecamera.domain.interactors.SwitchCameraUseCase
import com.gis.featurecamera.domain.interactors.TakePhotoUseCase
import com.gis.featurecamera.presentation.ui.camerascreen.CameraIntent.PrepareCamera
import com.gis.featurecamera.presentation.ui.camerascreen.CameraStateChange.Error
import com.gis.featurecamera.presentation.ui.camerascreen.CameraStateChange.Idle
import com.gis.utils.BaseViewModel
import io.reactivex.Observable

class CameraViewModel(
  private var goBack: (() -> Unit)?,
  private var goToTakenPhotoScreen: ((String) -> Unit)?,
  private val prepareCameraControllerUseCase: PrepareCameraControllerUseCase,
  private val observeCameraEventsUseCase: ObserveCameraEventsUseCase,
  private val takePhotoUseCase: TakePhotoUseCase,
  private val switchCameraUseCase: SwitchCameraUseCase) : BaseViewModel<CameraState>() {

  override fun initState(): CameraState = CameraState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(listOf(

      intentStream.ofType(PrepareCamera::class.java)
        .switchMap { event ->
          prepareCameraControllerUseCase.execute(event.orientation, event.textureView, event.lifecycle)
            .andThen(observeCameraEventsUseCase.execute())
            .map { cameraEvent -> mapCameraEvent(cameraEvent) }
            .doOnNext { stateChange -> navigateIfNeed(stateChange) }
            .doOnError { goBack!!.invoke() }
            .onErrorReturn { e: Throwable -> Error(e) }
        },

      intentStream.ofType(CameraIntent.TakePhoto::class.java)
        .switchMap { event ->
          takePhotoUseCase.execute()
            .andThen(Observable.just(Idle))
            .cast(CameraStateChange::class.java)
            .doOnError { goBack!!.invoke() }
            .onErrorReturn { e: Throwable -> Error(e) }
        },

      intentStream.ofType(CameraIntent.SwitchCamera::class.java)
        .switchMap { event ->
          switchCameraUseCase.execute()
            .andThen(Observable.just(Idle))
            .cast(CameraStateChange::class.java)
            .doOnError { goBack!!.invoke() }
            .onErrorReturn { e: Throwable -> Error(e) }
        }
    ))

  private fun mapCameraEvent(event: CameraControllerEvent): CameraStateChange =
    when (event) {
      is CameraSwitched -> CameraStateChange.CameraSwitched(event.facing)
      is FocusStarted -> CameraStateChange.FocusStarted
      is FocusFinished -> CameraStateChange.FocusFinished
      is PhotoTaken -> CameraStateChange.PhotoTaken(event.photoUrl)
      is CameraAccessControllerException -> Error(Throwable("Camera access error"))
      is CameraOpenControllerException -> Error(event.error)
      is Exception -> Error(event.error)
    }

  private fun navigateIfNeed(stateChange: CameraStateChange) {
    if (stateChange is CameraStateChange.PhotoTaken)
      goToTakenPhotoScreen!!.invoke(stateChange.imagePath)
    if (stateChange is Error)
      goBack!!.invoke()
  }

  override fun reduceState(previousState: CameraState, stateChange: Any): CameraState =
    when (stateChange) {
      is Idle -> previousState.copy(error = null)

      is CameraStateChange.PhotoTaken -> previousState.copy(error = null)

      is CameraStateChange.CameraSwitched -> previousState.copy(
        cameraFacing = stateChange.cameraFacing,
        error = null)

      is CameraStateChange.FocusStarted -> previousState.copy(
        focusState = FocusState.FocusStarted,
        error = null)

      is CameraStateChange.FocusFinished -> previousState.copy(
        focusState = FocusState.FocusFinished,
        error = null)

      is Error -> previousState.copy(error = stateChange.error)

      else -> previousState
    }

  override fun onCleared() {
    goBack = null
    goToTakenPhotoScreen = null
    super.onCleared()
  }
}