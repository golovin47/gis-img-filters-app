package com.gis.featurecamera.domain

import android.view.TextureView
import androidx.lifecycle.Lifecycle
import com.gis.featurecamera.domain.entity.CameraControllerEvent
import io.reactivex.Completable
import io.reactivex.Observable

interface CameraController {

  fun prepareCameraController(textureView: TextureView, lifecycle: Lifecycle): Completable

  fun observeCameraEvents(): Observable<CameraControllerEvent>

  fun takePhoto(): Completable

  fun switchCamera(): Completable
}