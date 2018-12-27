package com.gis.utils.di

import android.graphics.Bitmap
import com.gis.utils.bitmapFromFilePath
import com.gis.utils.createTempImageFile
import com.gis.utils.data.GlideImageLoader
import com.gis.utils.data.PhotoFilterManagerImpl
import com.gis.utils.domain.ImageLoader
import com.gis.utils.domain.PhotoFilterManager
import com.gis.utils.domain.interactors.ApplyFilterUseCase
import com.gis.utils.domain.interactors.GetThumbnailsUseCase
import com.gis.utils.saveImageToStorage
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

val utilsModule = module {

  single<ImageLoader> { GlideImageLoader }

  factory<PhotoFilterManager> { PhotoFilterManagerImpl(androidApplication()) }

  factory("bitmapFromImagePath") { { name: String -> bitmapFromFilePath(name) } }

  factory("saveImageToStorage") { { bitmap: Bitmap -> saveImageToStorage(bitmap, androidApplication()) } }

  factory("createTempImageFile") { { bitmap: Bitmap -> createTempImageFile(bitmap, androidApplication()) } }

  factory { GetThumbnailsUseCase(get()) }

  factory { ApplyFilterUseCase(get()) }
}