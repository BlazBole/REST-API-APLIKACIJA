package com.example.myapiapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapiapp.Constants.BASE_URL
import com.example.myapiapp.databinding.ActivityLoginBinding
import com.example.myapiapp.databinding.ActivityMainBinding
import com.example.myapiapp.databinding.ActivityProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    private var user: ContactItem? = null // Definicija uporabnika kot razredne spremenljivke
    private var imageUri: Uri? = null
    private val MAX_IMAGE_SIZE = 1024

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Pridobitev uporabniškega imena iz SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("USERNAME", "")

        // Uporaba pridobljenega uporabniškega imena za izvedbo API klica getUserByUsername
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ContactsService::class.java)
        val call = service.getUserByUsername(username ?: "")

        call.enqueue(object : Callback<ContactItem> {
            override fun onResponse(call: Call<ContactItem>, response: Response<ContactItem>) {
                if (response.isSuccessful) {
                    user = response.body() // Shrani uporabnika kot razredno spremenljivko

                    // Preverite, ali je uporabnik pravilno pridobljen iz odgovora API klica
                    if (user != null) {
                        // Uporabite pridobljene podatke za izpolnitev polj
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
            // Odprite galerijo za izbiro slike profila
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

                    // Klic REST API in posodobitev profila z Base64 sliko
                    if (user != null) { // Preveri, ali
                        // je uporabnik pridobljen

                        val id = user?.userId ?: -1 // Predpostavimo neko privzeto vrednost, npr. -1

                        val userName = binding.twEditProfileUserName.text.toString()
                        val phone = binding.twEditProfilePhone.text.toString()

                        val updateUserRequest = ContactItem(id, userName, "", "", phone, base64String)

                        val contactsService = retrofit.create(ContactsService::class.java)

                        val context = applicationContext
                        val call = contactsService.updateUser(id, updateUserRequest)
                        call.enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    // Dekodirajte base64 niz v sliko
                                    val decodedByteArray = Base64.decode(base64String, Base64.DEFAULT)
                                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

                                    // Nastavite dekodirano sliko v ImageView
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
                                // Napaka pri komunikaciji s strežnikom
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
