package com.gis.featurecamera.data.entity

import com.gis.featurecamera.data.exception.CameraOpenException

sealed class CameraControllerEvent {
  object FocusStarted : CameraControllerEvent()
  object FocusFinished : CameraControllerEvent()
  class PhotoTaken(val photoUrl: String, val photoSourceType: Int) : CameraControllerEvent()
  object CameraAccessControllerException : CameraControllerEvent()
  class CameraOpenControllerException(error: CameraOpenException) : CameraControllerEvent()
  class Exception(error: Throwable) : CameraControllerEvent()
}