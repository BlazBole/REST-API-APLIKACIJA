package com.example.myapiapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapiapp.databinding.ActivityInputBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class InputActivity : AppCompatActivity() {
    lateinit var binding: ActivityInputBinding
    var userId: Int? = null
    lateinit var formattedDate: String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        setRequiredField(binding.etInvNumber)
        setRequiredField(binding.etInvTitle)
        setRequiredField(binding.etLocation)

        val currentDate = LocalDate.now()

        // Pretvorba trenutnega datuma v želeni format
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        formattedDate = currentDate.format(formatter)

        // Nastavitev vrednosti EditText z današnjim datumom
        binding.etInputDate.text = Editable.Factory.getInstance().newEditable(formattedDate)

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("USERNAME","")

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ContactsService::class.java)

        // Pokličite API za pridobitev uporabnika
        val call = retrofitBuilder.getUserByUsername(username!!)
        call.enqueue(object : Callback<ContactItem> {
            override fun onResponse(call: Call<ContactItem>, response: Response<ContactItem>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    userId = user?.userId // Nastavite userId na ID uporabnika
                } else {
                    // Napaka pri pridobivanju uporabnika
                    Toast.makeText(applicationContext, "Napaka pri pridobivanju uporabnika", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ContactItem>, t: Throwable) {
                // Napaka pri klicanju REST API-ja
                Log.e("InputActivity", "Error: ${t.message}")
                Toast.makeText(applicationContext, "Napaka pri izvajanju zahtevka", Toast.LENGTH_SHORT).show()
            }
        })


        var fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        var bottom_down = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        binding.topLinearLayout.animation = bottom_down

        var handler = Handler()
        var runnable = Runnable{
            //set fade in animation
            binding.cardView.animation = fade_in
            binding.cardView2.animation = fade_in
            binding.textView.animation = fade_in
            binding.infoLayout.animation = fade_in
        }

        handler.postDelayed(runnable,1000)

        val barcodeContent = intent.getStringExtra("barcodeContent")
        binding.etInvNumber.text = Editable.Factory.getInstance().newEditable(barcodeContent)


        binding.btnAddInventory.setOnClickListener {
            val inventoryNumber = binding.etInvNumber.text.toString().trim()
            val inventoryName = binding.etInvTitle.text.toString().trim()
            val locationRoom = binding.etLocation.text.toString().trim()

            if (inventoryNumber.isEmpty() || inventoryName.isEmpty() || locationRoom.isEmpty()) {
                // Ena ali več polj je praznih, prikažite opozorilo uporabniku
                Toast.makeText(this, "Izpolni vsa vnosna polja", Toast.LENGTH_SHORT).show()
            } else {
                binding.etInputDate.text = Editable.Factory.getInstance().newEditable(formattedDate)
                val addToInventoryRequest = InventoryItem(
                    null, inventoryNumber, inventoryName, formattedDate, locationRoom, userId!!
                )
                addToInventory(addToInventoryRequest)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }


        binding.ivBack.setOnClickListener(){
            onBackPressed()
        }
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }

    // Prikaže navigacijsko vrstico telefona (domov, nazaj, zavihki)
    private fun showSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }

    // Ob dogodku, ko uporabnik potegne navzgor iz dna zaslona, prikažemo navigacijsko vrstico
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            showSystemUI()
        }
        return true
    }

    fun addToInventory(addToInventoryRequest: InventoryItem) {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ContactsService::class.java)

        val call = retrofitBuilder.addToInventory(addToInventoryRequest)

        call.enqueue(object : Callback<InventoryItem> {
            override fun onResponse(call: Call<InventoryItem>, response: Response<InventoryItem>) {
                if (response.isSuccessful) {
                    // Inventar je bil uspešno dodan
                    val inventoryItem = response.body()
                    Toast.makeText(applicationContext, "Inventar je bil uspešno dodan", Toast.LENGTH_SHORT).show()
                    // Lahko naredite kaj v primeru uspeha
                } else {
                    // Napaka pri dodajanju inventarja


                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(applicationContext, "Napaka pri dodajanju inventarja: $errorBody", Toast.LENGTH_SHORT).show()
                    // Lahko naredite kaj v primeru napake
                }
            }

            override fun onFailure(call: Call<InventoryItem>, t: Throwable) {
                // Napaka pri klicanju REST API-ja
                Log.e("InputActivity", "Error: ${t.message}")
                Toast.makeText(applicationContext, "Napaka pri izvajanju zahtevka", Toast.LENGTH_SHORT).show()
                // Lahko naredite kaj v primeru napake
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

