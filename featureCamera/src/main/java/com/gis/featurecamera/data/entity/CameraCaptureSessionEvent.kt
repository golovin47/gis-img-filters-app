package com.gis.featurecamera.data.entity

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult

sealed class CameraCaptureSessionEvent {
  object OnStarted : CameraCaptureSessionEvent()
  object OnProgressed : CameraCaptureSessionEvent()
  object OnCompleted : CameraCaptureSessionEvent()
  object OnSequenceCompleted : CameraCaptureSessionEvent()
  object OnSequenceAborted : CameraCaptureSessionEvent()
}