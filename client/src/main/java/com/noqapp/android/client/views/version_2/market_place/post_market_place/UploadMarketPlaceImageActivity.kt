package com.noqapp.android.client.views.version_2.market_place.post_market_place

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.noqapp.android.client.databinding.ActivityUploadMarketPlaceImageBinding
import com.noqapp.android.client.utils.Constants
import com.noqapp.android.client.utils.Constants.REQUEST_ID_MULTIPLE_PERMISSIONS
import com.noqapp.android.client.views.activities.BaseActivity
import com.noqapp.android.client.views.version_2.market_place.MarketplacePropertyRentalViewModel
import com.squareup.okhttp.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*


class UploadMarketPlaceImageActivity : BaseActivity() {

    private lateinit var activityUploadMarketPlaceImageBinding: ActivityUploadMarketPlaceImageBinding
    private lateinit var marketPlaceId: String
    private val marketplacePropertyRentalViewModel: MarketplacePropertyRentalViewModel by viewModels()
    private var selectedImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUploadMarketPlaceImageBinding = ActivityUploadMarketPlaceImageBinding.inflate(
            LayoutInflater.from(this)
        )
        setContentView(activityUploadMarketPlaceImageBinding.root)
        intent?.getStringExtra(Constants.MARKET_PLACE_ID)?.let {
            marketPlaceId = it
        }

        setListeners()
    }

    private fun setListeners() {
        activityUploadMarketPlaceImageBinding.ivAddPhoto.setOnClickListener {
            if (checkAndRequestPermissions(this)) {
                selectImage()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT
                )
                    .show()
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "FlagUp Requires Access to Your Storage.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                selectImage()
            }
        }
    }

    fun checkAndRequestPermissions(context: Activity): Boolean {
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
            listPermissionsNeeded
                .add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, listPermissionsNeeded
                    .toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val f = File(Environment.getExternalStorageDirectory(), "temp.jpg")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f))
                startActivityForResult(intent, 1)
            } else if (options[item] == "Choose from Gallery") {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                var f = File(Environment.getExternalStorageDirectory().toString())
                for (temp in f.listFiles()) {
                    if (temp.name == "temp.jpg") {
                        f = temp
                        break
                    }
                }
                try {
                    var bitmap: Bitmap?
                    val bitmapOptions = BitmapFactory.Options()
                    bitmap = BitmapFactory.decodeFile(f.absolutePath, bitmapOptions)
                    bitmap = getResizedBitmap(bitmap, 400)
                    activityUploadMarketPlaceImageBinding.ivHeadImage.setImageBitmap(bitmap)
                    bitmap?.let {
                        bitMapToString(bitmap)
                    }
                    val path = (Environment
                        .getExternalStorageDirectory()
                        .toString() + File.separator
                            + "Phoenix" + File.separator + "default")
                    f.delete()
                    var outFile: OutputStream? = null
                    val file = File(path, System.currentTimeMillis().toString() + ".jpg")
                    try {
                        outFile = FileOutputStream(file)
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 85, outFile)
                        outFile.flush()
                        outFile.close()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == 2) {
                val uri: Uri? = intent?.data
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                uri?.let { selectedImage ->
                    val c = contentResolver.query(selectedImage, filePath, null, null, null)
                    c?.let { cursor ->
                        cursor.moveToFirst()
                        val columnIndex = cursor.getColumnIndex(filePath[0])
                        val picturePath = cursor.getString(columnIndex)
                        cursor.close()
                        var thumbnail = BitmapFactory.decodeFile(picturePath)
                        thumbnail = getResizedBitmap(thumbnail, 400)
                        Log.w(
                            "path of image from gallery......******************.........",
                            picturePath + ""
                        )
                        activityUploadMarketPlaceImageBinding.ivHeadImage.setImageBitmap(thumbnail)
                        bitMapToString(thumbnail)
                    }
                    uploadImage(selectedImage)
                }
            }
        }
    }

    fun bitMapToString(userImage1: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos)
        val b = baos.toByteArray()
        selectedImageUri = Base64.encodeToString(b, Base64.DEFAULT)
        return selectedImageUri
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun uploadImage(imageUri: Uri) {
        getRealPathFromURI(imageUri)?.let { imagePath ->
            getMimeType(imageUri)?.let { type ->
                val file: File = File(imagePath)
                val profileImageFile = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    RequestBody.create(okhttp3.MediaType.parse(type, file)
                )
                val postId: RequestBody = RequestBody.create(okhttp3.MediaType.parse("text/plain"), marketPlaceId)
            }
        }

        marketplacePropertyRentalViewModel.postImagesLiveData()
    }

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri, proj, null, null, null)
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    private fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = this.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase())
        }
        return mimeType
    }
}
