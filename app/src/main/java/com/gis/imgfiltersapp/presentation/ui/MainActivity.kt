package com.gis.imgfiltersapp.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.gis.imgfiltersapp.R
import com.gis.imgfiltersapp.databinding.ActivityMainBinding
import com.gis.navigation.AppNavigator
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private var navigator: AppNavigator = get()

  companion object {
    init {
      System.loadLibrary("NativeImageProcessor")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)

    initBinding()
    initNavController()
  }

  private fun initBinding() {
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
  }

  private fun initNavController() {
    val mainNavController =
      (supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment).navController
    navigator.setNavController(mainNavController)
  }
}
