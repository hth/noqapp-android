package com.noqapp.android.client.views.version_2.market_place.householdItem

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.noqapp.android.client.BuildConfig
import com.noqapp.android.client.databinding.FragmentUploadPropertyRentalImagesBinding
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.views.fragments.BaseFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

open class ImageUploadFragment : BaseFragment() {
    open val REQUEST_IMAGE_CAPTURE = 1001
    open val REQUEST_PICK_IMAGE = 1002
    open lateinit var currentPhotoPath: String
    open lateinit var fragmentPostPropertyRentalUpload: FragmentUploadPropertyRentalImagesBinding

    open fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source = ImageDecoder.createSource(
                        requireActivity().contentResolver,
                        photoUri!!
                )
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    open fun pickImageFromGallery() {
        // Create intent for picking a photo from the gallery
        val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
        }
    }

    open fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().managedQuery(contentUri, proj, null, null, null)
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    open fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    open fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    open fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                dispatchTakePictureIntent()
            } else if (options[item] == "Choose from Gallery") {
                pickImageFromGallery()
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    open fun checkAndRequestPermissions(context: Activity): Boolean {
        val wExtStorePermission: Int = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraPermission: Int = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (wExtStorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                    requireActivity(), listPermissionsNeeded
                    .toTypedArray(),
                    Constants.REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                        requireContext(),
                        "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT
                )
                        .show()
            } else if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                        requireContext(),
                        "FlagUp Requires Access to Your Storage.",
                        Toast.LENGTH_SHORT
                ).show()
            } else {
                selectImage()
            }
        }
    }




    open fun showSnackBar(text: String) {
        Snackbar.make(
                fragmentPostPropertyRentalUpload.root, text,
                Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            setPic()
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val photoUri: Uri? = data?.data
            // Load the image located at photoUri into selectedImage
            val selectedImage = loadFromUri(photoUri)
            fragmentPostPropertyRentalUpload.ivHeadImage.setImageBitmap(selectedImage)
            getRealPathFromURI(photoUri)?.let {
                saveImage(it)
            }
        }
    }

    open fun saveImage(imagePath: String) {}

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = fragmentPostPropertyRentalUpload.ivHeadImage.width
        val targetH: Int = fragmentPostPropertyRentalUpload.ivHeadImage.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            fragmentPostPropertyRentalUpload.ivHeadImage.setImageBitmap(bitmap)
            saveImage(currentPhotoPath)
        }
    }
}