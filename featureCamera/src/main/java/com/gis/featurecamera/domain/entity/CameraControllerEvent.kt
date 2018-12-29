package com.gis.featurecamera.domain.entity

import com.gis.featurecamera.data.exception.CameraOpenException

sealed class CameraControllerEvent {
  object FocusStarted : CameraControllerEvent()
  object FocusFinished : CameraControllerEvent()
  class PhotoTaken(val photoUrl: String) : CameraControllerEvent()
  class CameraSwitched(val facing: CameraFacing) : CameraControllerEvent()
  object CameraAccessControllerException : CameraControllerEvent()
  class CameraOpenControllerException(val error: CameraOpenException) : CameraControllerEvent()
  class Exception(val error: Throwable) : CameraControllerEvent()
}