package com.gis.featurecamera.data

import android.media.Image
import android.media.ImageReader
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream

class GISImageSaver {

  companion object {

    fun save(image: Image, file: File): Single<File> =
      Single.fromCallable {
        return@fromCallable FileOutputStream(file).channel.use {
          it.write(image.planes[0].buffer)
          return@use file
        }
      }

    fun createOnImageAvailableObservable(imageReader: ImageReader): Observable<ImageReader> =
      Observable.create { emitter ->
        val listener = ImageReader.OnImageAvailableListener { reader -> emitter.onNext(reader) }
        imageReader.setOnImageAvailableListener(listener, null)
        emitter.setCancellable { imageReader.setOnImageAvailableListener(null, null) }
      }
  }
}