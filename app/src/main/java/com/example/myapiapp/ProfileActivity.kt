package com.example.myapiapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapiapp.Constants.BASE_URL
import com.example.myapiapp.databinding.ActivityLoginBinding
import com.example.myapiapp.databinding.ActivityMainBinding
import com.example.myapiapp.databinding.ActivityProfileBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import androidx.appcompat.widget.Toolbar


class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    private var user: ContactItem? = null
    private var imageUri: Uri? = null
    private val MAX_IMAGE_SIZE = 1024

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("USERNAME", "")

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ContactsService::class.java)
        val call = service.getUserByUsername(username ?: "")

        call.enqueue(object : Callback<ContactItem> {
            override fun onResponse(call: Call<ContactItem>, response: Response<ContactItem>) {
                if (response.isSuccessful) {
                    user = response.body()

                    if (user != null) {
                        binding.twProfileUserName.text = user!!.userName
                        binding.twProfileEmail.text = user!!.email
                        binding.twEditProfileUserName.text = user!!.userName
                        binding.twEditProfilePhone.text = user!!.phone

                        val decodedByteArray = Base64.decode(user!!.image, Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
                        binding.ivEditProfileImage.setImageBitmap(decodedBitmap)
                        binding.imageView.setImageBitmap(decodedBitmap)

                    } else {
                        Toast.makeText(applicationContext, "Uporabnik ni bil najden.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Napaka pri pridobivanju podatkov o uporabniku.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ContactItem>, t: Throwable) {
                Toast.makeText(applicationContext, "Napaka pri izvedbi API klica.", Toast.LENGTH_SHORT).show()
            }
        })

        binding.ivEditProfileImage.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.iwAddToInventory.setOnClickListener(){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    ProfileActivity.CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                IntentIntegrator(this).initiateScan()
            }
        }

        binding.iwMyInventory.setOnClickListener(){
            val intent = Intent(this@ProfileActivity, UserInputActivity::class.java)
            startActivity(intent)
        }

        binding.iwLogOut.setOnClickListener(){
            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.remove("USERNAME")
            editor.apply()
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.ivBack.setOnClickListener(){
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == IntentIntegrator.REQUEST_CODE){
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == IntentIntegrator.REQUEST_CODE) {
                val result: IntentResult? =
                    IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null && result.contents != null) {
                    val barcodeContent = result.contents
                    val intent = Intent(this@ProfileActivity, InputActivity::class.java)
                    intent.putExtra("barcodeContent", barcodeContent) // Dodajanje podatkov v intent
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "Skeniranje preklicano ali ni bilo mogoče prebrati QR kode", Toast.LENGTH_SHORT).show()
                }
            }
        }

        else if(requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK){
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
                val originalImageUri = data?.data
                if (originalImageUri != null) {
                    val resizedBitmap = resizeBitmap(originalImageUri, MAX_IMAGE_SIZE)
                    if (resizedBitmap != null) {
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                        val byteArray = byteArrayOutputStream.toByteArray()
                        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                        if (user != null) {

                            val id = user?.userId ?: -1

                            val userName = binding.twEditProfileUserName.text.toString()
                            val phone = binding.twEditProfilePhone.text.toString()

                            val updateUserRequest = ContactItem(id, userName, "", "", phone, base64String)

                            val contactsService = retrofit.create(ContactsService::class.java)

                            val context = applicationContext
                            val call = contactsService.updateUser(id, updateUserRequest)
                            call.enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        val decodedByteArray = Base64.decode(base64String, Base64.DEFAULT)
                                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

                                        binding.imageView.setImageBitmap(decodedBitmap)
                                        binding.ivEditProfileImage.setImageBitmap(decodedBitmap)

                                        Toast.makeText(applicationContext, "Profil uspešno spremenjen", Toast.LENGTH_SHORT).show()

                                        val intent = Intent(context, MainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(applicationContext, "Napaka pri urejanju profila", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                }
                            })
                        } else {
                            Toast.makeText(applicationContext, "Napaka: Uporabnik ni bil pravilno pridobljen.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Napaka pri zmanjševanju velikosti slike", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Napaka pri pridobivanju slike iz galerije", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resizeBitmap(uri: Uri, maxSize: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
            var width = options.outWidth
            var height = options.outHeight
            var scale = 1

            while (width > maxSize || height > maxSize) {
                scale *= 2
                width /= 2
                height /= 2
            }

            val resizedOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, resizedOptions)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
