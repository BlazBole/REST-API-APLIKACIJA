package com.example.myapiapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapiapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var toggle : ActionBarDrawerToggle
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val drawerLayout = binding.drawerLayout
        val navView = binding.navView

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.drawable.baseline_menu_24, R.drawable.baseline_arrow_back_24)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navHeaderView = navView.getHeaderView(0)
        // Pridobitev TextView-ja z ID-jem twUserName znotraj nav-headerja
        val twUserName = navHeaderView.findViewById<TextView>(R.id.twUserName)

        // Tukaj lahko nastavite uporabniško ime na pridobljen TextView
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("USERNAME","")

        navHeaderView.findViewById<ImageView>(R.id.editProfileIcon).setOnClickListener(){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

// Preverite, ali je uporabnik prijavljen
        if (username.isNullOrEmpty()) {
            // Če uporabnik ni prijavljen, nastavite besedilo na "gost"
            twUserName.text = "gost"
        } else {
            // Če je uporabnik prijavljen, nastavite besedilo na uporabniško ime
            twUserName.text = username

            val navMenu = navView.menu
            // Nastavite vidnost elementa menija nav_logOut na true
            navMenu.findItem(R.id.nav_logOut)?.isVisible = true
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_settings -> {
                    Toast.makeText(applicationContext, "Settings", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_login -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_register -> {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_share -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_rate -> {
                    Toast.makeText(applicationContext, "Rate", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logOut -> {
                    val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.remove("USERNAME")
                    editor.apply()
                    twUserName.text = "gost"
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}