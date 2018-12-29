package com.gis.featurecamera.data.entity

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult

data class CaptureSessionData(val event: CameraCaptureSessionEvent,
                              val session: CameraCaptureSession,
                              val request: CaptureRequest,
                              val result: TotalCaptureResult)