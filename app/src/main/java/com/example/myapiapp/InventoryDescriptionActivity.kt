package com.example.myapiapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapiapp.databinding.ActivityInventoryDescriptionBinding
import com.example.myapiapp.databinding.ActivityMainBinding

class InventoryDescriptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityInventoryDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryDescriptionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.tvStoritev.text = intent.getStringExtra("INVTITLE")

    }
}