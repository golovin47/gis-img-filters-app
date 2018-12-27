package com.gis.navigation.di

import com.gis.navigation.AppNavigator
import org.koin.dsl.module.module

val navigationModule = module {

  single(createOnStart = true) { AppNavigator() }

  factory(name = "fromChooseImageToApplyFilterScreen") {
    { imagePath: String ->
      get<AppNavigator>().goToApplyFilterFromChooseImageScreen(imagePath)
    }
  }

  factory("goToChooseImageFromApplyFilterScreen") {
    { get<AppNavigator>().goToChooseImageFromApplyFilterScreen() }
  }
}