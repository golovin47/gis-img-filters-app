package com.gis.featurecamera.data

import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import com.gis.featurecamera.data.entity.CaptureSessionData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class ConvergeWaitHelper private constructor(
  private val requestTriggerKey: CaptureRequest.Key<Int>,
  private val requestTriggerStartValue: Int,
  private val resultStateKey: CaptureResult.Key<Int>,
  private val resultReadyStates: List<Int>) {

  private val timeoutSeconds = 3


  fun waitForConverge(captureResultParams: CaptureSessionData, builder: CaptureRequest.Builder): Single<CaptureSessionData> {
    val previewRequest = builder.build()

    builder.set<Int>(requestTriggerKey, requestTriggerStartValue)
    val triggerRequest = builder.build()

    val triggerObservable = GISCamera.fromCapture(captureResultParams.session, triggerRequest)
    val previewObservable = GISCamera.fromSetRepeatingRequest(captureResultParams.session, previewRequest)
    val convergeSingle = Observable
      .merge<CaptureSessionData>(previewObservable, triggerObservable)
      .filter { resultParams -> isStateReady(resultParams.result) }
      .first(captureResultParams)

    val timeOutSingle = Single
      .just<CaptureSessionData>(captureResultParams)
      .delay(timeoutSeconds.toLong(), TimeUnit.SECONDS, AndroidSchedulers.mainThread())

    return Single
      .merge<CaptureSessionData>(convergeSingle, timeOutSingle)
      .firstElement()
      .toSingle()
  }

  private fun isStateReady(result: CaptureResult): Boolean {
    val aeState = result.get<Int>(resultStateKey)
    return aeState == null || resultReadyStates.contains(aeState)
  }

  object Factory {

    private val afReadyStates = listOf(
      CaptureResult.CONTROL_AF_STATE_INACTIVE,
      CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED,
      CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED,
      CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED)

    private val aeReadyStates = listOf(
      CaptureResult.CONTROL_AE_STATE_INACTIVE,
      CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED,
      CaptureResult.CONTROL_AE_STATE_CONVERGED,
      CaptureResult.CONTROL_AE_STATE_LOCKED
    )

    fun createAutoFocusConvergeWaiter(): ConvergeWaitHelper {
      return ConvergeWaitHelper(
        CaptureRequest.CONTROL_AF_TRIGGER,
        CameraMetadata.CONTROL_AF_TRIGGER_START,
        CaptureResult.CONTROL_AF_STATE,
        afReadyStates
      )
    }

    fun createAutoExposureConvergeWaiter(): ConvergeWaitHelper {
      return ConvergeWaitHelper(
        CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
        CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START,
        CaptureResult.CONTROL_AE_STATE,
        aeReadyStates
      )
    }
  }
}