package com.gis.navigation.di

import com.gis.navigation.AppNavigator
import org.koin.dsl.module.module

val navigationModule = module {

  single(createOnStart = true) { AppNavigator() }

  factory(name = "goToApplyFilterScreenFromChooseImage") {
    { imagePath: String ->
      get<AppNavigator>().goToApplyFilterFromChooseImageScreen(imagePath)
    }
  }

  factory(name = "goToCameraFromChooseImageScreen") {
    { get<AppNavigator>().goToCameraFromChooseImageScreen() }
  }

  factory(name = "goToTakenPhotoFromCameraScreen") {
    { imagePath: String ->
      get<AppNavigator>().goToTakenPhotoFromCameraScreen(imagePath)
    }
  }

  factory(name = "goToApplyFilterScreenFromTakenPhoto") {
    { imagePath: String ->
      get<AppNavigator>().goToApplyFilterFromTakenPictureScreen(imagePath)
    }
  }

  factory("goBack") {
    { get<AppNavigator>().goBack() }
  }
}