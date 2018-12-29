package com.gis.featurecamera.domain.interactors

import android.view.TextureView
import androidx.lifecycle.Lifecycle
import com.gis.featurecamera.domain.CameraController

class PrepareCameraControllerUseCase(private val cameraController: CameraController) {
  fun execute(orientation: Int, textureView: TextureView, lifecycle: Lifecycle) =
    cameraController.prepareCameraController(orientation, textureView, lifecycle)
}


class ObserveCameraEventsUseCase(private val cameraController: CameraController) {
  fun execute() = cameraController.observeCameraEvents()
}


class TakePhotoUseCase(private val cameraController: CameraController) {
  fun execute() = cameraController.takePhoto()
}


class SwitchCameraUseCase(private val cameraController: CameraController) {
  fun execute() = cameraController.switchCamera()
}