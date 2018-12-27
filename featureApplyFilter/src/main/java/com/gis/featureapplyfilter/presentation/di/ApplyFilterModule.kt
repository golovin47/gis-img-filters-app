package com.gis.featureapplyfilter.presentation.di

import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val applyFilterModule = module {
  viewModel { ApplyFilterViewModel(
    get("bitmapFromImagePath"),
    get("saveImageToStorage"),
    get("createTempImageFile"),
    get("goToChooseImageFromApplyFilterScreen"),
    get(), get()) }
}