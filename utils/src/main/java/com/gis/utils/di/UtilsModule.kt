package com.gis.utils.di

import com.gis.utils.bitmapFromFilePath
import com.gis.utils.data.GlideImageLoader
import com.gis.utils.data.PhotoFilterManagerImpl
import com.gis.utils.domain.ImageLoader
import com.gis.utils.domain.PhotoFilterManager
import com.gis.utils.domain.interactors.ApplyFilterUseCase
import com.gis.utils.domain.interactors.GetThumbnailsUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

val utilsModule = module {

  single<ImageLoader> { GlideImageLoader }

  factory<PhotoFilterManager> { PhotoFilterManagerImpl(androidApplication()) }

  factory("bitmapFromImagePath") { { name: String -> bitmapFromFilePath(name) } }

  factory { GetThumbnailsUseCase(get()) }

  factory { ApplyFilterUseCase(get()) }
}