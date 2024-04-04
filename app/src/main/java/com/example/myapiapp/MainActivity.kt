package com.example.myapiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.myapiapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class MainActivity : AppCompatActivity() {
    private lateinit var txtId: TextView
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getMyData();

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var userName = sharedPrefs.getString("USERNAME", "")
        val twShowUser: TextView = findViewById(R.id.twShowUser)
        twShowUser.text = userName

        if(binding.twShowUser.text != ""){
            binding.btnLogout.isVisible = true;
        }

        binding.btnLogout.setOnClickListener {
            // Odstranjevanje shranjenega uporabniškega imena iz SharedPreferences
            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.remove("USERNAME")
            editor.apply()

            // Počistite vse druge shranjene podatke ali izvedite druge potrebne operacije ob odjavi

            // Po odjavi preusmerite uporabnika na LoginActivity
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // Ta klic bo izbrisal MainActivity iz back stacka, tako da se uporabnik ne more vrniti nazaj na to aktivnost
        }

        binding.btnToMain.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build().create(ContactsService::class.java)

        val retrofitData = retrofitBuilder.getData()

        retrofitData.enqueue(object : Callback<List<ContactItem>?> {
            override fun onResponse(
                call: Call<List<ContactItem>?>,
                response: Response<List<ContactItem>?>
            ) {
                val responseBody = response.body()!!
                val myStringBuilder = StringBuilder()

                for(myData in responseBody){
                    myStringBuilder.append(myData.userName)
                    myStringBuilder.append("\n")
                }

                txtId = findViewById(R.id.txtId)
                txtId.text = myStringBuilder
            }

            override fun onFailure(call: Call<List<ContactItem>?>, t: Throwable) {
                Log.d("MainActivity", "onFailure: "+t.message)
            }
        })
    }


}