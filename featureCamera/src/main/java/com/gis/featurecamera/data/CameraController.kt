package com.gis.featurecamera.data

import android.content.Context
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraAccessException
import android.media.ImageReader
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.gis.featurecamera.data.entity.CameraCaptureStateEvent.OnClosed
import com.gis.featurecamera.data.entity.CameraCaptureStateEvent.OnConfigured
import com.gis.featurecamera.data.entity.CameraControllerEvent
import com.gis.featurecamera.data.entity.CameraControllerEvent.*
import com.gis.featurecamera.data.entity.CameraParams
import com.gis.featurecamera.data.entity.CameraStateEvent.CameraClosed
import com.gis.featurecamera.data.entity.CameraStateEvent.CameraOpened
import com.gis.featurecamera.data.entity.CaptureSessionData
import com.gis.featurecamera.data.exception.CameraOpenException
import com.gis.featurecamera.presentation.ui.customview.AutoFitTextureView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

class CameraController(
  context: Context,
  private val createTempFileForPhoto: () -> Observable<File>) : LifecycleObserver {

  private val orientation = Configuration.ORIENTATION_PORTRAIT
  private lateinit var cameraParams: CameraParams
  private lateinit var surface: Surface
  private var imageReader: ImageReader? = null

  private val autoFocusConvergeWaiter = ConvergeWaitHelper.Factory.createAutoFocusConvergeWaiter()
  private val autoExposureConvergeWaiter = ConvergeWaitHelper.Factory.createAutoExposureConvergeWaiter()

  private lateinit var textureView: AutoFitTextureView

  private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
  private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

  private val cameraManagerEventsPublisher = PublishSubject.create<CameraControllerEvent>()
  private val onPausePublisher = PublishSubject.create<Any>()
  private val takePhotoClickPublisher = PublishSubject.create<Any>()
  private val switchCameraClickPublisher = PublishSubject.create<Any>()
  private val surfaceTextureAvailablePublisher = PublishSubject.create<SurfaceTexture>()
  private val subscriptions = CompositeDisposable()

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun onResume() {
    subscribe()

    // When the screen is turned off and turned back on, the SurfaceTexture is already
    // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
    // a camera and start preview from here (otherwise, we wait until the surface is ready in
    // the SurfaceTextureListener).
    if (textureView.isAvailable)
      surfaceTextureAvailablePublisher.onNext(textureView.surfaceTexture)
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  fun onPause() {
    onPausePublisher.onNext(this)
  }

  fun prepareCameraController(textureView: AutoFitTextureView,
                              lifecycle: Lifecycle) {
    this.textureView = textureView
    lifecycle.addObserver(this)

    initCameraParamsAndAspectRatio()
    initSurfaceTextureListener()

    // For some reasons onSurfaceSizeChanged is not always called, this is a workaround
    initSurfaceSizeChangedBackup()
  }

  private fun initCameraParamsAndAspectRatio() {
    lateinit var cameraId: String
    try {
      cameraId = GISCameraHelper.chooseDefaultCamera(cameraManager)

      if (cameraId.isEmpty()) {
        cameraManagerEventsPublisher.onNext(Exception(IllegalStateException("Can't find any camera")))
        return
      }

      cameraParams = getCameraParams(cameraId)
      setTextureAspectRatio(cameraParams)
    } catch (e: CameraAccessException) {
      cameraManagerEventsPublisher.onNext(Exception(IllegalStateException("Can't find any camera")))
      return
    }
  }

  private fun initSurfaceTextureListener() {
    textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {

      override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        surfaceTextureAvailablePublisher.onNext(surface)
      }

      override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        surfaceTextureAvailablePublisher.onNext(surface)
      }

      override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

      }

      override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
    }
  }

  private fun initSurfaceSizeChangedBackup() {
    textureView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
      if (textureView.isAvailable) {
        surfaceTextureAvailablePublisher.onNext(textureView.surfaceTexture)
      }
    }
  }

  fun observeEvents() = cameraManagerEventsPublisher

  private fun subscribe() {
    subscriptions.clear()

    // open camera

    val cameraDeviceObservable = surfaceTextureAvailablePublisher
      .firstElement()
      .doAfterSuccess { setupSurface(it) }
      .doAfterSuccess { initImageReader() }
      .toObservable()
      .flatMap { GISCamera.openCamera(cameraParams.cameraId, cameraManager) }
      .share()

    val openCameraObservable = cameraDeviceObservable
      .filter { event -> event is CameraOpened }
      .map { event -> (event as CameraOpened).camera }
      .share()

    val closeCameraObservable = cameraDeviceObservable
      .filter { event -> event is CameraClosed }
      .map { event -> (event as CameraClosed).camera }
      .share()

    // create capture session

    val createCaptureSessionObservable = openCameraObservable
      .flatMap { cameraDevice ->
        GISCamera.createCaptureSession(cameraDevice, listOf(surface, imageReader!!.surface))
      }
      .share()

    val captureSessionConfiguredObservable = createCaptureSessionObservable
      .filter { event -> event is OnConfigured }
      .map { event -> (event as OnConfigured).session }
      .share()

    val captureSessionClosedObservable = createCaptureSessionObservable
      .filter { event -> event is OnClosed }
      .map { event -> (event as OnClosed).session }
      .share()

    // start preview

    val previewObservable = captureSessionConfiguredObservable
      .flatMap { cameraCaptureSession ->
        val previewBuilder = createPreviewBuilder(cameraCaptureSession, surface)
        GISCamera.fromSetRepeatingRequest(cameraCaptureSession, previewBuilder.build())
      }
      .share()

    // react to shutter button

    subscriptions.add(
      Observable.combineLatest(
        previewObservable, takePhotoClickPublisher, BiFunction<CaptureSessionData, Any, CaptureSessionData> { captureSessionData, o -> captureSessionData })
        .firstElement().toObservable()
        .doOnNext { cameraManagerEventsPublisher.onNext(FocusStarted) }
        .flatMap { waitForAf(it) }
        .flatMap { waitForAe(it) }
        .doOnNext { cameraManagerEventsPublisher.onNext(FocusFinished) }
        .flatMap { captureSessionData -> captureStillPicture(captureSessionData.session) }
        .subscribe({ }, { onError(it) })
    )

    // react to switch camera button

    subscriptions.add(
      Observable.combineLatest(
        previewObservable, switchCameraClickPublisher, BiFunction<CaptureSessionData, Any, CaptureSessionData> { captureSessionData, o -> captureSessionData })
        .firstElement().toObservable()
        .doOnNext { captureSessionData -> captureSessionData.session.close() }
        .flatMap { captureSessionClosedObservable }
        .doOnNext { cameraCaptureSession -> cameraCaptureSession.device.close() }
        .flatMap { closeCameraObservable }
        .doOnNext { closeImageReader() }
        .subscribe({ switchCameraInternal() }, { onError(it) })
    )

    // react to onPause event

    subscriptions.add(Observable.combineLatest(previewObservable, onPausePublisher, BiFunction<CaptureSessionData, Any, CaptureSessionData> { captureSessionData, o -> captureSessionData })
      .firstElement().toObservable()
      .doOnNext { captureSessionData ->
        captureSessionData.session.stopRepeating()
        captureSessionData.session.abortCaptures()
        captureSessionData.session.close()
      }
      .flatMap { captureSessionClosedObservable }
      .doOnNext { cameraCaptureSession -> cameraCaptureSession.device.close() }
      .flatMap { closeCameraObservable }
      .doOnNext { closeImageReader() }
      .subscribe({ unsubscribe() }, { onError(it) })
    )
  }

  private fun unsubscribe() {
    subscriptions.clear()
  }

  private fun onError(error: Throwable) {
    unsubscribe()

    when (error) {
      is CameraAccessException -> cameraManagerEventsPublisher.onNext(CameraAccessControllerException)
      is CameraOpenException -> cameraManagerEventsPublisher.onNext(CameraOpenControllerException(error))
      else -> cameraManagerEventsPublisher.onNext(Exception(error))
    }
  }

  private fun setupSurface(surfaceTexture: SurfaceTexture) {
    surfaceTexture.setDefaultBufferSize(cameraParams.previewSize.width, cameraParams.previewSize.height)
    surface = Surface(surfaceTexture)
  }

  private fun switchCameraInternal() {
    try {
      unsubscribe()
      val cameraId = GISCameraHelper.switchCamera(cameraManager, cameraParams.cameraId)
      cameraParams = getCameraParams(cameraId)
      setTextureAspectRatio(cameraParams)
      subscribe()
      // waiting for textureView to be measured
    } catch (e: CameraAccessException) {
      onError(e)
    }
  }

  private fun initImageReader() {
    val sizeForImageReader = GISCameraHelper.getStillImageSize(cameraParams.characteristics, cameraParams.previewSize)
    imageReader = ImageReader.newInstance(sizeForImageReader.width, sizeForImageReader.height, ImageFormat.JPEG, 1)
    subscriptions.add(
      createTempFileForPhoto().flatMap { file ->
        GISImageSaver.createOnImageAvailableObservable(imageReader!!)
          .observeOn(Schedulers.io())
          .flatMap { imageReader -> GISImageSaver.save(imageReader.acquireLatestImage(), file).toObservable() }
          .observeOn(AndroidSchedulers.mainThread())
      }.subscribe { file -> cameraManagerEventsPublisher.onNext(PhotoTaken(file.absolutePath, getLensFacingPhotoType())) }
    )
  }

  private fun getCameraParams(cameraId: String): CameraParams {
    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
    val previewSize = GISCameraHelper.getPreviewSize(characteristics)
    return CameraParams(cameraId, characteristics, previewSize)
  }

  private fun getLensFacingPhotoType(): Int =
    cameraParams.characteristics.get(CameraCharacteristics.LENS_FACING)!!

  private fun contains(modes: IntArray?, mode: Int): Boolean {
    if (modes == null) {
      return false
    }
    for (i in modes) {
      if (i == mode) {
        return true
      }
    }
    return false
  }

  private fun waitForAf(captureResultParams: CaptureSessionData): Observable<CaptureSessionData> {
    return Observable
      .fromCallable { createPreviewBuilder(captureResultParams.session, surface) }
      .flatMap<CaptureSessionData> { previewBuilder ->
        autoFocusConvergeWaiter
          .waitForConverge(captureResultParams, previewBuilder)
          .toObservable()
      }
  }

  private fun waitForAe(captureResultParams: CaptureSessionData): Observable<CaptureSessionData> {
    return Observable
      .fromCallable { createPreviewBuilder(captureResultParams.session, surface) }
      .flatMap { previewBuilder ->
        autoExposureConvergeWaiter
          .waitForConverge(captureResultParams, previewBuilder)
          .toObservable()
      }
  }

  private fun captureStillPicture(cameraCaptureSession: CameraCaptureSession): Observable<CaptureSessionData> {
    return Observable.fromCallable { createStillPictureBuilder(cameraCaptureSession.device) }
      .flatMap { builder -> GISCamera.fromCapture(cameraCaptureSession, builder.build()) }
  }

  @Throws(CameraAccessException::class)
  private fun createStillPictureBuilder(cameraDevice: CameraDevice): CaptureRequest.Builder {
    val builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
      set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_STILL_CAPTURE)
      set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE)
      addTarget(imageReader!!.surface)
    }
    setup3Auto(builder)

    val rotation = windowManager.defaultDisplay.rotation
    builder.set(CaptureRequest.JPEG_ORIENTATION, GISCameraHelper.getJpegOrientation(cameraParams.characteristics, rotation))
    return builder
  }

  @Throws(CameraAccessException::class)
  internal fun createPreviewBuilder(captureSession: CameraCaptureSession, previewSurface: Surface): CaptureRequest.Builder {
    val builder = captureSession.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
    builder.addTarget(previewSurface)
    setup3Auto(builder)
    return builder
  }

  private fun setup3Auto(builder: CaptureRequest.Builder) {
    // Enable auto-magical 3A run by camera device
    builder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)

    val minFocusDist = cameraParams.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)

    // If MINIMUM_FOCUS_DISTANCE is 0, lens is fixed-focus and we need to skip the AF run.
    val noAFRun = minFocusDist == null || minFocusDist == 0f

    if (!noAFRun) {
      // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
      val afModes = cameraParams.characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
      if (contains(afModes, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)) {
        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
      } else {
        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
      }
    }

    // If there is an auto-magical flash control mode available, use it, otherwise default to
    // the "on" mode, which is guaranteed to always be available.
    val aeModes = cameraParams.characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)
    if (contains(aeModes, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)) {
      builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
    } else {
      builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
    }

    // If there is an auto-magical white balance control mode available, use it.
    val awbModes = cameraParams.characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)
    if (contains(awbModes, CaptureRequest.CONTROL_AWB_MODE_AUTO)) {
      // Allow AWB to run auto-magically if this device supports this
      builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO)
    }
  }

  private fun setTextureAspectRatio(cameraParams: CameraParams) {
    if (orientation == Configuration.ORIENTATION_LANDSCAPE)
      textureView.setAspectRatio(cameraParams.previewSize.width, cameraParams.previewSize.height)
    else
      textureView.setAspectRatio(cameraParams.previewSize.height, cameraParams.previewSize.width)
  }

  private fun closeImageReader() {
    if (imageReader != null) {
      imageReader?.close()
      imageReader = null
    }
  }
}