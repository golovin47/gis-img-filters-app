package com.gis.featurecamera.presentation.di

import com.gis.featurecamera.data.CameraControllerImpl
import com.gis.featurecamera.domain.CameraController
import com.gis.featurecamera.domain.interactors.ObserveCameraEventsUseCase
import com.gis.featurecamera.domain.interactors.PrepareCameraControllerUseCase
import com.gis.featurecamera.domain.interactors.SwitchCameraUseCase
import com.gis.featurecamera.domain.interactors.TakePhotoUseCase
import com.gis.featurecamera.presentation.ui.camerascreen.CameraViewModel
import com.gis.featurecamera.presentation.ui.takenphotoscreen.TakenPhotoViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val cameraModule = module {
  single<CameraController> {
    CameraControllerImpl(androidApplication(), get("createTempFileForPhoto"))
  }

  factory { PrepareCameraControllerUseCase(get()) }
  factory { ObserveCameraEventsUseCase(get()) }
  factory { TakePhotoUseCase(get()) }
  factory { SwitchCameraUseCase(get()) }

  viewModel {
    CameraViewModel(
      get("goBack"),
      get("goToTakenPhotoFromCameraScreen"),
      get(), get(), get(), get())
  }

  viewModel {
    TakenPhotoViewModel(
      get("goBack"),
      get("goToApplyFilterScreenFromTakenPhoto")
    )
  }
}