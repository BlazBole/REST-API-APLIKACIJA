package com.example.myapiapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://172.27.244.142:5009/"

class MainActivity : AppCompatActivity() {
    private lateinit var txtId: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        geMyData();
    }

    private fun geMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
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
                    myStringBuilder.append(myData.fullname)
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