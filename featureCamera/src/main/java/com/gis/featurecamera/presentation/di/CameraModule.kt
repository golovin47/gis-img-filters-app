package com.gis.featurecamera.presentation.di

import com.gis.featurecamera.data.CameraController
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

val cameraModule = module {
  single { CameraController(androidApplication(), get("createTempFileForPhoto")) }
}