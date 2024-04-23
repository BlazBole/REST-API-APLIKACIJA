package com.example.myapiapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapiapp.databinding.ActivityInventoryDescriptionBinding
import com.example.myapiapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryDescriptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityInventoryDescriptionBinding
    lateinit var userEmail : String
    private var user: ContactItem? = null // Definicija uporabnika kot razredne spremenljivke
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryDescriptionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        binding.twInvNumber.text = intent.getStringExtra("INVNUMBER")
        binding.twInvNumCorner.text = intent.getStringExtra("INVNUMBER")
        binding.twInvTitle.text = intent.getStringExtra("INVTITLE")
        binding.twInvEntryDate.text = intent.getStringExtra("INVINPUTDATE")
        binding.twInvLocation.text = intent.getStringExtra("INVLOCATION")
        //binding.twProfileUserName.text = intent.getStringExtra("USER")
        userEmail = intent.getStringExtra("USER").toString()
        //binding.twProfileUserName.text = userName

        fatchUserByUsername();

        binding.ivBack.setOnClickListener(){
            onBackPressed()
        }

    }

    fun fatchUserByUsername(){
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ContactsService::class.java)
        val call = service.getUserByEmail(userEmail ?: "")

        call.enqueue(object : Callback<ContactItem> {
            override fun onResponse(call: Call<ContactItem>, response: Response<ContactItem>) {
                if (response.isSuccessful) {
                    user = response.body() // Shrani uporabnika kot razredno spremenljivko
                    // Preverite, ali je uporabnik pravilno pridobljen iz odgovora API klica
                    if (user != null) {
                        // Uporabite pridobljene podatke za izpolnitev polj
                        binding.twProfileUserName.text = user!!.userName
                        binding.twProfileEmail.text = user!!.email
                        binding.twProfilePhone.text = user!!.phone

                        val decodedByteArray = Base64.decode(user!!.image, Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
                        binding.imageView.setImageBitmap(decodedBitmap)
                        binding.imageView.setImageBitmap(decodedBitmap)

                        Log.d("InventoryDescription", "Uporabniško ime: ${user!!.userName}")

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
    }
}