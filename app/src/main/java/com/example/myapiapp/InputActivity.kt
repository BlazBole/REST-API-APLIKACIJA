package com.example.myapiapp

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapiapp.databinding.ActivityInputBinding
import com.example.myapiapp.databinding.ActivityLoginBinding
import android.view.MotionEvent
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

class InputActivity : AppCompatActivity() {
    lateinit var binding: ActivityInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        //hideSystemUI();


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

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val editableText = Editable.Factory.getInstance().newEditable(formattedDate)
        binding.etInputDate.text = editableText
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

}

