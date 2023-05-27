package com.example.myuserstory.ui.addnewuserstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myuserstory.R
import com.example.myuserstory.databinding.ActivityAddNewUserStoryBinding
import com.example.myuserstory.helper.ViewModelFactory
import com.example.myuserstory.ui.camera.CameraActivity
import com.example.myuserstory.ui.main.MainActivity
import com.example.myuserstory.ui.main.MainViewModel
import com.example.myuserstory.ui.signin.SignInViewModel
import com.example.myuserstory.utils.getAddressName
import com.example.myuserstory.utils.reduceFileImage
import com.example.myuserstory.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File


class AddNewUserStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewUserStoryBinding

    private val factory = ViewModelFactory.getInstance(this)
    private lateinit var mainViewModel: MainViewModel
    private lateinit var signInViewModel: SignInViewModel

    private var getFile: File? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat = 0.0
    private var lon = 0.0

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewUserStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_title)

        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        signInViewModel = ViewModelProvider(this, factory)[SignInViewModel::class.java]


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnAddCamera.setOnClickListener { startCameraX() }
        binding.btnAddGallery.setOnClickListener { startGallery() }
        binding.btnAddLocation.setOnClickListener {
            lat = mainViewModel.coordLat.value.toString().toDouble()
            lon = mainViewModel.coordLon.value.toString().toDouble()
            val location = getAddressName(this@AddNewUserStoryActivity, lat, lon).toString()
            binding.tvAddLocation.text = location
        }
        binding.buttonAdd.setOnClickListener {
            if (getFile != null) {
                val description = binding.edAddDescription.text.toString()
                if (description.isNotEmpty()) {
                    val file = reduceFileImage(getFile as File)
                    signInViewModel.getUser().observe(this) { user ->
                        mainViewModel.addNewStory(user.token, file, description, lat.toString(), lon.toString())
                        mainViewModel.repository.remoteData.isLoading.observe(this) {
                            showLoading(it)
                            if (!it) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(this@AddNewUserStoryActivity, getString(R.string.hint_description), Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this@AddNewUserStoryActivity, getString(R.string.hint_image), Toast.LENGTH_SHORT).show()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getMyLastLocation()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.addProgressBar.visibility = View.VISIBLE
        }
        else {
            binding.addProgressBar.visibility = View.GONE
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        val pickPhoto = getString(R.string.pick_photo)
        val chooser = Intent.createChooser(intent, pickPhoto)
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage, this@AddNewUserStoryActivity)
            getFile = myFile

            binding.ivAddImg.setImageURI(selectedImage)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    postCoordinate(location.latitude, location.longitude)
                }
                else {
                    postCoordinate(0.0, 0.0)
                }
            }
        }
        else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun postCoordinate(lat: Double, lon: Double) {
        mainViewModel.coordLat.postValue(lat)
        mainViewModel.coordLon.postValue(lon)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(CameraActivity.PICTURE) as File
            val isBackCamera = it.data?.getBooleanExtra(CameraActivity.IS_BACK_CAMERA, false) as Boolean

            getFile = myFile

//            Jika gambar perlu dirotasi
//            val result = rotateBitmap(
//                BitmapFactory.decodeFile(getFile?.path),
//                isBackCamera
//            )
//            binding.ivAddImg.setImageBitmap(result)

            myFile.let { file ->
                val result = BitmapFactory.decodeFile(file.path)
                binding.ivAddImg.setImageBitmap(result)
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}