package com.example.myapiapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.myapiapp.databinding.ActivityMainBinding
import com.example.myapiapp.databinding.ActivityRegisterBinding
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setRequiredField(binding.etUsername)
        setRequiredField(binding.etEmail)
        setRequiredField(binding.etPassword)

        //initialize animations

        var fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        var bottom_down = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        //setting the bottom down animation on top layout

        binding.topLinearLayout.animation = bottom_down

        //handler for other layouts
        var handler = Handler()
        var runnable = Runnable{
            //set fade in animation
            binding.cardView.animation = fade_in
            binding.cardView2.animation = fade_in
            binding.textView.animation = fade_in
            binding.twToLogin.animation = fade_in
            binding.registerLayout.animation = fade_in

        }

        handler.postDelayed(runnable,1000)

        binding.etPhone.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val phoneNumberUtil = PhoneNumberUtil.getInstance()
                val userInput = (v as EditText).text.toString()

                try {
                    val phoneNumber = phoneNumberUtil.parse(userInput, "SI")
                    if (phoneNumberUtil.isValidNumber(phoneNumber)) {
                        val formattedPhoneNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
                        binding.etPhone.setText(formattedPhoneNumber)
                    } else {
                        Toast.makeText(this, "Neveljavna oblika telefonske številke", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                }
            }
        }

        binding.btnRegister.setOnClickListener(){
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                addUser(username, email, password, phone, this)
            } else {
                Toast.makeText(this, "Izpolnite obvezna vnosna polja", Toast.LENGTH_SHORT).show()
            }
        }

        binding.twToLogin.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.textView2.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnAnim.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addUser(username: String, email: String, password: String, phone: String, context: Context) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build().create(ContactsService::class.java)

        // Najprej preverimo, ali že obstaja uporabnik z istim e-poštnim naslovom
        val emailCheckCall = retrofitBuilder.getUserByEmail(email)
        val usernameCheckCall = retrofitBuilder.getUserByUsername(username)

        // Asinhrono izvedemo obe preverjanji
        emailCheckCall.enqueue(object : Callback<ContactItem> {
            override fun onResponse(call: Call<ContactItem>, emailResponse: Response<ContactItem>) {
                if (emailResponse.isSuccessful) {
                    // Uporabnik z istim e-poštnim naslovom že obstaja
                    val existingUser = emailResponse.body()
                    Log.d("RegisterActivity", "User with email $email already exists: $existingUser")
                    Toast.makeText(context, "Ta uporabnik že obstaja", Toast.LENGTH_SHORT).show()
                } else {
                    // Preverimo uporabniško ime, če e-poštni naslov ni bil najden
                    usernameCheckCall.enqueue(object : Callback<ContactItem> {
                        override fun onResponse(call: Call<ContactItem>, usernameResponse: Response<ContactItem>) {
                            if (usernameResponse.isSuccessful) {
                                // Uporabnik z istim uporabniškim imenom že obstaja
                                val existingUser = usernameResponse.body()
                                Log.d("RegisterActivity", "User with username $username already exists: $existingUser")
                                Toast.makeText(context, "Uporabniško ime že obstaja", Toast.LENGTH_SHORT).show()
                            } else {
                                // Uporabnik s tem e-poštnim naslovom in uporabniškim imenom ne obstaja, dodajemo novega
                                val newUser = ContactItem(id = null, username, email, password, phone)
                                val addUserCall = retrofitBuilder.addUser(newUser)

                                addUserCall.enqueue(object : Callback<ContactItem> {
                                    override fun onResponse(call: Call<ContactItem>, response: Response<ContactItem>) {
                                        if (response.isSuccessful) {
                                            // Uspešno dodan uporabnik
                                            Log.d("RegisterActivity", "User added successfully")
                                            // Po dodajanju uporabnika zaženemo novo aktivnost
                                            val intent = Intent(context, LoginActivity::class.java)
                                            context.startActivity(intent)
                                        } else {
                                            // Obdelava napake, če POST zahteva ni uspela
                                            Log.d("RegisterActivity", "Failed to add user: ${response.code()}")
                                        }
                                    }

                                    override fun onFailure(call: Call<ContactItem>, t: Throwable) {
                                        // Obdelava napake, če je prišlo do napake med izvajanjem zahteve
                                        Log.d("RegisterActivity", "onFailure: ${t.message}")
                                    }
                                })
                            }
                        }

                        override fun onFailure(call: Call<ContactItem>, t: Throwable) {
                            // Obdelava napake, če je prišlo do napake med izvajanjem zahteve
                            Log.d("RegisterActivity", "onFailure: ${t.message}")
                        }
                    })
                }
            }

            override fun onFailure(call: Call<ContactItem>, t: Throwable) {
                // Obdelava napake, če je prišlo do napake med izvajanjem zahteve
                Log.d("RegisterActivity", "onFailure: ${t.message}")
            }
        })
    }


    private fun setRequiredField(editText: EditText) {
        val hint = editText.hint ?: return // Preverimo, če obstaja napis "hint"
        val builder = SpannableStringBuilder(hint)
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val redColorSpan = ForegroundColorSpan(primaryColor)
        builder.setSpan(redColorSpan, hint.length - 1, hint.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        editText.hint = builder
    }
}