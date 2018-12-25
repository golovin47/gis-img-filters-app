package com.gis.utils.di

import com.gis.utils.data.GlideImageLoader
import com.gis.utils.domain.ImageLoader
import org.koin.dsl.module.module

val utilsModule = module {

  single<ImageLoader> { GlideImageLoader }
}