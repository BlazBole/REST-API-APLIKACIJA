package com.example.myapiapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapiapp.databinding.ActivityLoginBinding
import com.example.myapiapp.databinding.ActivityRegisterBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setRequiredField(binding.etEmail)
        setRequiredField(binding.etPassword)

        var fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        var bottom_down = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        binding.topLinearLayout.animation = bottom_down

        var handler = Handler()
        var runnable = Runnable{
            //set fade in animation
            binding.cardView.animation = fade_in
            binding.cardView2.animation = fade_in
            binding.textView.animation = fade_in
            binding.twToRegister.animation = fade_in
            binding.registerLayout.animation = fade_in

        }

        handler.postDelayed(runnable,1000)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val loginRequest = ContactItem(null, "", email, password, "") // Ustvarite LoginRequest objekt
                loginUser(loginRequest)
            } else {
                Toast.makeText(this, "Izpolnite obvezna vnosna polja", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun loginUser(loginRequest: ContactItem) {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ContactsService::class.java)

        val call = retrofitBuilder.loginUser(loginRequest)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string()
                    // Uporabnik se je uspešno prijavil, obdelajte odgovor
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("USER_NAME", loginRequest.email.toString())
                    startActivity(intent)
                    finish()
                } else {
                    // Prišlo je do napake pri prijavi uporabnika
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(applicationContext, errorBody, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Prišlo je do napake pri izvajanju zahtevka
                Log.e("LoginActivity", "Error: ${t.message}")
                Toast.makeText(applicationContext, "Napaka pri izvajanju zahtevka", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setRequiredField(editText: EditText) {
        val hint = editText.hint ?: return
        val builder = SpannableStringBuilder(hint)
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val redColorSpan = ForegroundColorSpan(primaryColor)
        builder.setSpan(redColorSpan, hint.length - 1, hint.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        editText.hint = builder
    }
}