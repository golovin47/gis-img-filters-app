package com.gis.featurechooseimage.presentation.ui.chooseimagescreen

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import java.util.concurrent.TimeUnit

class ChooseImageFragment : Fragment(), BaseView<ChooseImageState> {

  private val GALLERY_PHOTO_REQUEST = 0x098
  private val CAMERA_PERMISSIONS_REQUEST = 0x097
  private val GALLERY_PERMISSIONS_REQUEST = 0x096
  private val EXTRA_DIALOG_PERMISSIONS_REQUEST = 0x095

  private lateinit var permissionAlertDialog: AlertDialog

  private val imageEventsPublisher = PublishSubject.create<ChooseImageIntent>()
  private lateinit var viewSubscriptions: Disposable
  private var binding: FragmentChooseImageBinding? = null

  private val vmChooseImage: ChooseImageViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleStates()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    initBinding(inflater, container)
    initPermissionsAlertDialog()
    initIntents()

    return binding!!.root
  }

  override fun onDestroyView() {
    binding = null
    viewSubscriptions.dispose()
    super.onDestroyView()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      GALLERY_PHOTO_REQUEST ->
        if (resultCode == Activity.RESULT_OK) processGalleryPhoto(data!!.data!!)
        else photoCancelled()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == CAMERA_PERMISSIONS_REQUEST)
      if (grantResults.size == 3)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
          grantResults[1] == PackageManager.PERMISSION_GRANTED &&
          grantResults[2] == PackageManager.PERMISSION_GRANTED)
          imageEventsPublisher.onNext(OpenCamera)
        else imageEventsPublisher.onNext(ShowExtraPermissionsDialog)

    if (requestCode == GALLERY_PERMISSIONS_REQUEST)
      if (grantResults.size == 2)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
          grantResults[1] == PackageManager.PERMISSION_GRANTED)
          imageEventsPublisher.onNext(OpenGallery)
        else imageEventsPublisher.onNext(ShowExtraPermissionsDialog)
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_image, container, false)
  }

  private fun initPermissionsAlertDialog() {
    permissionAlertDialog = AlertDialog.Builder(context!!).apply {
      setTitle(R.string.permissions_dialog_title)
      setMessage(R.string.permissions_dialog_message)
      setCancelable(false)
      setPositiveButton(R.string.go_to_settings) { _: DialogInterface, _: Int ->
        imageEventsPublisher.onNext(DismissExtraPermissionsDialog)
        imageEventsPublisher.onNext(GoToAppSettings)
      }
      setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int ->
        imageEventsPublisher.onNext(DismissExtraPermissionsDialog)
      }
    }.create()
  }

  override fun initIntents() {
    viewSubscriptions = Observable.merge(listOf(

      RxView.clicks(binding!!.btnCamera)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .map {
          if (cameraPermissionsGranted()) OpenCamera
          else RequestPermissionsForCamera
        },

      RxView.clicks(binding!!.btnGallery)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .map {
          if (galleryPermissionsGranted()) OpenGallery
          else RequestPermissionsForGallery
        },

      imageEventsPublisher
    )
    )
      .subscribe(vmChooseImage.viewIntentsConsumer())
  }

  override fun handleStates() {
    vmChooseImage.stateReceived().observe(this, Observer { state -> render(state) })
  }

  private fun showExtraPermissionDialog() {
    permissionAlertDialog.show()
  }

  private fun dismissExtraPermissionsDialog() {
    permissionAlertDialog.dismiss()
  }

  private fun openGallery() {
    startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, GALLERY_PHOTO_REQUEST)
  }

  private fun processGalleryPhoto(uri: Uri) {
    imageEventsPublisher.onNext(GalleryImageChosen(uri))
  }

  private fun cameraPermissionsGranted(): Boolean {
    return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
      == PackageManager.PERMISSION_GRANTED) &&
      (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED) &&
      (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED)
  }

  private fun galleryPermissionsGranted(): Boolean {
    return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
      == PackageManager.PERMISSION_GRANTED) &&
      (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED)
  }

  private fun requestCameraPermissions() {
    requestPermissions(
      arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE),
      CAMERA_PERMISSIONS_REQUEST)
  }

  private fun requestGalleryPermissions() {
    requestPermissions(
      arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE),
      GALLERY_PERMISSIONS_REQUEST)
  }

  private fun goToAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
      Uri.parse("package:" + context!!.packageName))
    startActivityForResult(intent, EXTRA_DIALOG_PERMISSIONS_REQUEST)
  }

  private fun photoCancelled() {
    imageEventsPublisher.onNext(ImageCancelled)
  }

  override fun render(state: ChooseImageState) {
    if (state.openGallery) openGallery()

    if (state.requestPermissionsForCamera) requestCameraPermissions()
    if (state.requestPermissionsForGallery) requestGalleryPermissions()

    if (state.showExtraPermissionsDialog) showExtraPermissionDialog()
    else dismissExtraPermissionsDialog()

    if (state.goToAppSettings) goToAppSettings()

    if (state.error != null)
      Snackbar.make(view!!, state.error.message!!, Snackbar.LENGTH_SHORT).show()
  }
}