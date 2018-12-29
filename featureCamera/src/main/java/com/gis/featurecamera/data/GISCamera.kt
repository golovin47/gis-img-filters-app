package com.gis.featurecamera.data

import android.annotation.SuppressLint
import android.hardware.camera2.*
import android.view.Surface
import com.gis.featurecamera.data.entity.CameraCaptureSessionEvent
import com.gis.featurecamera.data.entity.CameraCaptureSessionEvent.OnCompleted
import com.gis.featurecamera.data.entity.CameraCaptureStateEvent
import com.gis.featurecamera.data.entity.CameraCaptureStateEvent.*
import com.gis.featurecamera.data.entity.CameraStateEvent
import com.gis.featurecamera.data.entity.CameraStateEvent.*
import com.gis.featurecamera.data.entity.CaptureSessionData
import com.gis.featurecamera.data.exception.CameraCaptureFailedException
import com.gis.featurecamera.data.exception.CameraOpenException
import com.gis.featurecamera.data.exception.CreateCaptureSessionException
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

object GISCamera {

  @SuppressLint("MissingPermission")
  fun openCamera(cameraId: String, cameraManager: CameraManager): Observable<CameraStateEvent> =
    Observable.create { emitter ->
      cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
          emitter.onNext(CameraOpened(camera))
        }

        override fun onClosed(camera: CameraDevice) {
          emitter.onNext(CameraClosed(camera))
        }

        override fun onDisconnected(camera: CameraDevice) {
          emitter.onNext(CameraDisconnected(camera))
        }

        override fun onError(camera: CameraDevice, error: Int) {
          emitter.onError(exceptionFromErrorCode(error))
        }

      }, null)
    }

  fun createCaptureSession(camera: CameraDevice, surfaces: List<Surface>): Observable<CameraCaptureStateEvent> =
    Observable.create { emitter ->
      camera.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {

        override fun onConfigured(session: CameraCaptureSession) {
          emitter.onNext(OnConfigured(session))
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
          emitter.onError(CreateCaptureSessionException(session))
        }

        override fun onReady(session: CameraCaptureSession) {
          emitter.onNext(OnReady(session))
        }

        override fun onActive(session: CameraCaptureSession) {
          emitter.onNext(OnActive(session))
        }

        override fun onClosed(session: CameraCaptureSession) {
          emitter.onNext(OnClosed(session))
          emitter.onComplete()
        }

        override fun onSurfacePrepared(session: CameraCaptureSession, surface: Surface) {
          emitter.onNext(OnSurfacePrepared(session))
        }
      }, null)
    }

  fun fromSetRepeatingRequest(captureSession: CameraCaptureSession, request: CaptureRequest): Observable<CaptureSessionData> =
    Observable.create { emitter ->
      captureSession.setRepeatingRequest(request, createCaptureCallback(emitter), null)
    }

  fun fromCapture(captureSession: CameraCaptureSession, request: CaptureRequest): Observable<CaptureSessionData> =
    Observable.create { emitter ->
      captureSession.capture(request, createCaptureCallback(emitter), null)
    }

  private fun createCaptureCallback(emitter: ObservableEmitter<CaptureSessionData>) =
    object : CameraCaptureSession.CaptureCallback() {

      override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
        emitter.onNext(CaptureSessionData(OnCompleted, session, request, result))
      }

      override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
        emitter.onError(CameraCaptureFailedException(failure))
      }
    }

  private fun exceptionFromErrorCode(errorCode: Int): CameraOpenException =
    CameraOpenException(
      when (errorCode) {
        CameraDevice.StateCallback.ERROR_CAMERA_DEVICE -> "Error camera device"
        CameraDevice.StateCallback.ERROR_CAMERA_DISABLED -> "Error camera disabled"
        CameraDevice.StateCallback.ERROR_CAMERA_IN_USE -> "Error camera in use"
        CameraDevice.StateCallback.ERROR_CAMERA_SERVICE -> "Error camera service"
        CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE -> "Error max cameras in use"
        else -> ""
      })
}