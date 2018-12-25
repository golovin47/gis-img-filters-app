package com.gis.utils.di

import android.opengl.GLSurfaceView
import com.gis.utils.data.GlideImageLoader
import com.gis.utils.data.PhotoFilterImpl
import com.gis.utils.domain.ImageLoader
import com.gis.utils.domain.PhotoFilter
import com.gis.utils.domain.PhotoFilterCallback
import org.koin.dsl.module.module

val utilsModule = module {

  single<ImageLoader> { GlideImageLoader }

  factory<PhotoFilter> { (view: GLSurfaceView, callBack: PhotoFilterCallback) -> PhotoFilterImpl(view, callBack) }
}