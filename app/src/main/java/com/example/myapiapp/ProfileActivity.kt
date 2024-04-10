package com.example.myapiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapiapp.Constants.BASE_URL
import com.example.myapiapp.databinding.ActivityLoginBinding
import com.example.myapiapp.databinding.ActivityMainBinding
import com.example.myapiapp.databinding.ActivityProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding

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
                    val user = response.body()

                    // Preverite, ali je uporabnik pravilno pridobljen iz odgovora API klica
                    if (user != null) {
                        // Uporabite pridobljene podatke za izpolnitev polj
                        binding.twProfileUserName.text = user.userName
                        binding.twProfileEmail.text = user.email
                        binding.twEditProfileUserName.text = user.userName
                        binding.twEditProfilePhone.text = user.phone
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
            // Pridobite izbrano sliko iz podatkovne aktivnosti
            val imageUri = data?.data
            // Nastavite izbrano sliko profila na ImageView
            binding.ivEditProfileImage.setImageURI(imageUri)
        }
    }

}
