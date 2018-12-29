package com.gis.featurecamera.data.entity

import android.hardware.camera2.CameraDevice

sealed class CameraStateEvent {
  class CameraOpened(val camera: CameraDevice) : CameraStateEvent()
  class CameraClosed(val camera: CameraDevice) : CameraStateEvent()
  class CameraDisconnected(val camera: CameraDevice) : CameraStateEvent()
}