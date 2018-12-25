package com.gis.imgfiltersapp.application

import android.app.Application
import com.gis.featurechooseimage.presentation.di.chooseImageModule
import com.gis.navigation.di.navigationModule
import com.gis.utils.di.utilsModule
import org.koin.android.ext.android.startKoin

class ImgFiltersApp : Application() {

  override fun onCreate() {
    super.onCreate()

    startKoin(this, listOf(chooseImageModule, navigationModule, utilsModule))
  }
}