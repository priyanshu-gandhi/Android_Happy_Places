package com.example.happyplaces

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.databinding.ActivityAddPlacesBinding
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.example.happyplaces.ui.theme.DatabaseHelper
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

import java.util.Calendar

class AddPlaces : AppCompatActivity() {

    private lateinit var dbhelper : DatabaseHelper
    private lateinit var itemAdapter: ItemAdapter


    companion object
    {
        const val CAMERA_Permission =1
        const val GALLERY_Permission=2
    }

    private var binding: ActivityAddPlacesBinding?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //Initialize the database
        dbhelper = DatabaseHelper(this)

        binding = ActivityAddPlacesBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddHappyPlaces)


        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title ="ADD HAPPY PLACE"
        }


        binding?.toolbarAddHappyPlaces?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        Log.d("msg","Before")
        //intentFromRecylerView()

        // Dialog asks for specific permission
        binding?.addText?.setOnClickListener {
            Log.d("dia","doa1")
            val pictureDialog = AlertDialog.Builder(this@AddPlaces)

            pictureDialog.setTitle("Select Action")
            val pickerDialogItems = arrayOf(
                "Select photo from Gallery",
                "Capture photo from Camera"
            )

            pictureDialog.setItems(pickerDialogItems)
            { dialog, which ->
                when (which) {
                    0 -> choosePhotofromGallery()
                    1 -> capturePhotofromCamera()
                }
            }
            pictureDialog.show()
        }


        // Date Dialog box
        binding?.icDate?.apply {

            inputType = InputType.TYPE_NULL
            isFocusable = false
            isClickable = true

            setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)


                val datePickerDialog = DatePickerDialog(
                    this@AddPlaces,
                    { _, selected_year, selected_month, selected_date ->
                        val selected_Date = "$selected_date/${selected_month + 1}/$selected_year"
                        binding?.icDate?.setText(selected_Date)

                    }, year, month, day
                )
                datePickerDialog.show()
            }


        }

        //save data to the database
        binding?.addSubmit?.setOnClickListener {


                Log.d("SaveButton", "Save button clicked")

                //val imageUri = binding?.addImage?.tag?.toString()
                val title = binding?.icTitle?.text?.toString()
                val location = binding?.icLocation?.text?.toString()
                val description = binding?.icDesc?.text?.toString()

               /// Log.d("image", "$imageUri")

                if (title?.isNullOrEmpty()!! || location?.isNullOrEmpty()!! || description?.isNullOrEmpty()!!) {
                    Toast.makeText(this, "Enter data in all the fields", Toast.LENGTH_LONG).show()
                } else {

                    val result = dbhelper.insertUser(title, location, description)
                    Log.d("SaveButton", "Insert result: $result")

                    if (result == -1L) {
                        Toast.makeText(this, "Failed to add data", Toast.LENGTH_SHORT).show()
                        Log.d("check", "fail")
                    } else {
                        Toast.makeText(this, "Data Inserted Successfully!!", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("check", "pass")
                    }
                    itemAdapter.notifyItemInserted(result.toInt())
                }

            finish()
        }

    }

    private fun choosePhotofromGallery()
    {
             if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
             {
                 openGallery()
             }
             else
             {
                 ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.READ_EXTERNAL_STORAGE),
                     GALLERY_Permission)
             }
    }


    private fun capturePhotofromCamera()
    {
           if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
           {
               openCamera()
           }
        else
           {
               ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                   CAMERA_Permission)
           }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode)
        {
            GALLERY_Permission->
            {
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    openGallery()
                }
                else
                {
                    Toast.makeText(this,"Gallery Permission is denied",Toast.LENGTH_LONG).show()
                }
            }


            CAMERA_Permission->
            {
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    openCamera()
                }
                else
                {
                    //Toast.makeText(this,"Camera Permission is denied",Toast.LENGTH_LONG).show()
                    //Settings dialog
                    showSystemDialog()
                }
            }
        }
    }

    // Taking the image from intents and set it on image view
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK)
        {
            when(requestCode)
            {
                GALLERY_Permission->
                {
                     val imageUri =data?.data
                     if(imageUri !=null) {
                         binding?.addImage?.setImageURI(imageUri)
                       //  binding?.addImage?.tag = imageUri.toString()
                         saveImageFromUri(imageUri)
                     }
                }
                CAMERA_Permission->
                {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding?.addImage?.setImageBitmap(imageBitmap)
                    //val savedUri =
                        saveCameraImagetoStorage(imageBitmap)
                    //binding?.addImage?.tag = savedUri.toString()

                }
            }
        }

    }

// Creating intents for gallery and Camera
    fun openGallery()
    {
        val galleryintent =Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryintent, GALLERY_Permission)
    }

    fun openCamera()
    {
        val cameraintent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraintent, CAMERA_Permission)
    }

    fun showSystemDialog()
    {
         AlertDialog.Builder(this)
             .setTitle("System Settings")
             .setMessage("Please enable permission from the settings")
             .setPositiveButton("Go to Settings") {_, _,->
                 val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                 val uri= Uri.fromParts("package","com.example.happyplaces", null)
                 intent.data =uri
                 startActivity(intent)
             }
             .setNegativeButton("Cancel")
             {which,_,->
                 which.dismiss()
             }
             .create()
             .show()

    }


    // saving image of camera to storage
    private fun saveCameraImagetoStorage(bitmap: Bitmap)
    {
        val filename= "IMG_${System.currentTimeMillis()}.jpg"
        var fos :OutputStream? =null
        var imageUri: Uri?= null

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val resolver= contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES)

            }

            val imageUri =resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos= imageUri?.let { resolver.openOutputStream(it) }
        }
        else
        {
            val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
            val image= File(imagesDir, filename)
            fos=FileOutputStream(image)
            imageUri= Uri.fromFile(image)
        }

        fos?.use{
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,it)
            Toast.makeText(this, "Image Saved to External Storage",Toast.LENGTH_LONG).show()
        }
       // return imageUri
    }



    // saving image to uri
    private fun saveImageFromUri(imageUri: Uri){
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var newImageUri : Uri?= null


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    android.os.Environment.DIRECTORY_PICTURES
                )
            }

             newImageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = newImageUri?.let { resolver.openOutputStream(it) }

            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                fos?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Toast.makeText(this, "Image saved from gallery to storage", Toast.LENGTH_SHORT).show()

        }
       // return newImageUri
    }


    private fun intentFromRecylerView()
    {
        val mode= intent.getStringExtra("mode")
        val happyPlace = intent.getParcelableExtra<HappyPlacesModel>("happyPlaceObject")
        Log.d("msg","Before2")



            if (supportActionBar != null) {
                binding?.toolbarAddHappyPlaces?.title = happyPlace?.getTitle()
            }


        binding?.toolbarAddHappyPlaces?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            //itemAdapter?.notifyDataSetChanged()
        }



            Log.d("msg","Before3")

            binding?.icTitle?.setText(happyPlace?.getTitle())
            binding?.icLocation?.setText(happyPlace?.getLocation())
            binding?.icDesc?.setText(happyPlace?.getDesc())

            binding?.addSubmit?.text= "UPDATE"


    }










    override fun onDestroy() {
        super.onDestroy()
        binding =null
    }
}