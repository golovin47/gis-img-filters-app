package com.gis.featurechooseimage.presentation.di

import com.gis.featurechooseimage.presentation.ui.chooseimagescreen.ChooseImageViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val chooseImageModule = module {
  viewModel {
    ChooseImageViewModel(
      get("getPathFromUri"),
      get("getUriAndFilePath"),
      get("fromChooseImageToApplyFilterScreen"))
  }
}