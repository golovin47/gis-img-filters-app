package com.gis.featurechooseimage.presentation.ui.chooseimagescreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gis.featurechooseimage.R
import com.gis.featurechooseimage.databinding.FragmentChooseImageBinding
import com.gis.featurechooseimage.presentation.ui.chooseimagescreen.ChooseImageIntent.*
import com.gis.utils.BaseView
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ChooseImageFragment : Fragment(), BaseView<ChooseImageState> {

  private val CAMERA_PHOTO_REQUEST = 0x099
  private val GALLERY_PHOTO_REQUEST = 0x098
  private val GALLERY_PERMISSION_REQUEST = 0x97

  private val imageEventsPublisher = PublishSubject.create<ChooseImageIntent>()
  private lateinit var viewSubscriptions: Disposable
  private lateinit var currentState: ChooseImageState
  private var binding: FragmentChooseImageBinding? = null

  private val vmChooseImage: ChooseImageViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleStates()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    initBinding(inflater, container)
    initIntents()

    return binding!!.root
  }

  override fun onDestroyView() {
    binding = null
    viewSubscriptions.dispose()
    super.onDestroyView()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == CAMERA_PHOTO_REQUEST) {
      if (resultCode == Activity.RESULT_OK)
        processCameraPhoto()
      else photoCancelled()
    }

    if (requestCode == GALLERY_PHOTO_REQUEST) {
      if (resultCode == Activity.RESULT_OK)
        processGalleryPhoto(data!!.data!!)
      else photoCancelled()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == GALLERY_PERMISSION_REQUEST &&
      grantResults.size == 2 &&
      grantResults[0] == PackageManager.PERMISSION_GRANTED &&
      grantResults[1] == PackageManager.PERMISSION_GRANTED)
      openGallery()
    else imageEventsPublisher.onNext(ImageCancelled)
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_image, container, false)
  }

  override fun initIntents() {
    viewSubscriptions = Observable.merge(listOf(

      RxView.clicks(binding!!.btnCamera)
        .skip(500, TimeUnit.MILLISECONDS)
        .doOnNext {
          openCamera()
        }
        .map { ChooseImage },

      RxView.clicks(binding!!.btnGallery)
        .skip(500, TimeUnit.MILLISECONDS)
        .doOnNext {
          if (galleryPermissionGranted())
            openGallery()
          else requestGalleryPermission()
        }
        .map { ChooseImage },

      imageEventsPublisher
    )
    )
      .subscribe(vmChooseImage.viewIntentsConsumer())
  }

  override fun handleStates() {
    vmChooseImage.stateReceived().observe(this, Observer { state -> render(state) })
  }

  private fun openCamera() {
    val photoFile = createFileForCameraPhoto()
    val photoUri = FileProvider.getUriForFile(context!!, "com.gis.imgfiltersapp.provider", photoFile)
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
      putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
    }
    startActivityForResult(intent, CAMERA_PHOTO_REQUEST)
  }

  private fun createFileForCameraPhoto(): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "IMG_" + timestamp + "_"
    val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(fileName, ".jpg", storageDir)
    imageEventsPublisher.onNext(ImagePathCreated(file.absolutePath))
    return file
  }

  private fun processCameraPhoto() {
    imageEventsPublisher.onNext(ImageChosen(currentState.imagePath))
  }

  private fun openGallery() {
    startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, GALLERY_PHOTO_REQUEST)
  }

  private fun processGalleryPhoto(uri: Uri) {
    imageEventsPublisher.onNext(ImageChosen(getPathFromUri(uri)))
  }

  private fun galleryPermissionGranted(): Boolean {
    return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
      == PackageManager.PERMISSION_GRANTED) &&
      (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED)
  }

  private fun requestGalleryPermission() {
    requestPermissions(
      arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
      GALLERY_PERMISSION_REQUEST)
  }

  private fun getPathFromUri(uri: Uri): String {
    var resultPath = ""
    val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context!!.contentResolver.query(uri, proj, null, null, null)
    if (cursor != null && cursor.moveToFirst()) {
      val colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
      resultPath = cursor.getString(colIndex)
    }
    cursor?.close()

    return resultPath
  }

  private fun photoCancelled() {
    imageEventsPublisher.onNext(ImageCancelled)
  }

  override fun render(state: ChooseImageState) {
    currentState = state

    binding!!.loading = state.loading

    if (state.loading) binding!!.chooseImageRoot.transitionToEnd()
    else binding!!.chooseImageRoot.transitionToStart()

    if (state.error != null)
      Snackbar.make(view!!, state.error.message!!, Snackbar.LENGTH_SHORT).show()
  }
}