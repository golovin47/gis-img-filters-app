package com.gis.featurecamera.data.entity

import android.hardware.camera2.CameraCaptureSession

sealed class CameraCaptureStateEvent {
  class OnConfigured(val session: CameraCaptureSession) : CameraCaptureStateEvent()
  class OnReady(val session: CameraCaptureSession) : CameraCaptureStateEvent()
  class OnActive(val session: CameraCaptureSession) : CameraCaptureStateEvent()
  class OnClosed(val session: CameraCaptureSession) : CameraCaptureStateEvent()
  class OnSurfacePrepared(val session: CameraCaptureSession) : CameraCaptureStateEvent()
}