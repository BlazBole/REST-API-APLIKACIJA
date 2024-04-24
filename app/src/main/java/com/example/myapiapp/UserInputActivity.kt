package com.example.myapiapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapiapp.databinding.ActivityUserInputBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserInputActivity : AppCompatActivity(), RecyclerViewInterface {
    lateinit var binding: ActivityUserInputBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapterFilter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var username: String
    private lateinit var inventoryList: List<InventoryItem>

    private var user: ContactItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInputBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.emptyTextView.isVisible = false


        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        username = sharedPrefs.getString("USERNAME","").toString()

        fetchInventoryFromApi()

        binding.ivBack.setOnClickListener(){
            onBackPressed()
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, InventoryDescriptionActivity::class.java)
        val INVNUMBER = binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<TextView>(R.id.tvInNumber)?.text.toString()
        val INVTITLE = binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<TextView>(R.id.tvInTitle)?.text.toString()
        val INVLOCATION = binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<TextView>(R.id.tvInLocation)?.text.toString()
        val INVINPUTDATE = binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<TextView>(R.id.tvInInputDate)?.text.toString()
        val USER = binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<TextView>(R.id.tvUser)?.text.toString()

        // Pass the text as an extra to the intent
        intent.putExtra("INVNUMBER", INVNUMBER)
        intent.putExtra("INVTITLE", INVTITLE)
        intent.putExtra("INVLOCATION", INVLOCATION)
        intent.putExtra("INVINPUTDATE", INVINPUTDATE)
        intent.putExtra("USER", USER)
        startActivity(intent)
    }

    override fun onItemLongClick(position: Int) {
        // Ob dolgem kliku na element RecyclerView prikažemo dialog za potrditev brisanja
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Potrditev brisanja")
        alertDialogBuilder.setMessage("Ste prepričani, da želite izbrisati ta inventar?")
        alertDialogBuilder.setPositiveButton("Da") { dialog, which ->
            // Izvedite brisanje inventarja
            val inventoryItem = adapter.getItem(position) // Pridobite izbrani inventar iz adapterja
            deleteInventory(inventoryItem)
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Ne") { dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.create().show()
    }

    private fun deleteInventory(inventoryItem: InventoryItem) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ContactsService::class.java)
        val call = service.deleteInventory(inventoryItem.id!!)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Uspešno izbrisano
                    Toast.makeText(applicationContext, "Inventar je bil uspešno izbrisan.", Toast.LENGTH_SHORT).show()
                    // Osvežite seznam inventarja
                    fetchInventoryFromApi()
                } else {
                    // Napaka pri brisanju inventarja
                    Toast.makeText(applicationContext, "Napaka pri izbrisu inventarja.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Napaka pri izvedbi API klica
                Toast.makeText(applicationContext, "Napaka pri izvedbi API klica.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun fetchInventoryFromApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ContactsService::class.java)
        val call = service.getUserByUsername(username ?: "")

        call.enqueue(object : Callback<ContactItem> {
            override fun onResponse(call: Call<ContactItem>, response: Response<ContactItem>) {
                if (response.isSuccessful) {
                    val user = response.body() // Shrani uporabnika kot lokalno spremenljivko
                    if (user != null) {
                        // Pridobi inventar samo, če je uporabnik uspešno prejet
                        val callInventory = service.getInventoryByUser(user.userId ?: -1)

                        callInventory.enqueue(object : Callback<List<InventoryItem>> {
                            override fun onResponse(call: Call<List<InventoryItem>>, response: Response<List<InventoryItem>>) {
                                if (response.isSuccessful) {
                                    val inventoryList = response.body() ?: emptyList()
                                    setupRecyclerView(inventoryList)
                                } else {
                                    Toast.makeText(applicationContext, "Napaka pri pridobivanju inventarja", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<List<InventoryItem>>, t: Throwable) {
                                Log.e("MainActivity", "Error: ${t.message}")
                                Toast.makeText(applicationContext, "Napaka pri izvajanju zahtevka", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(applicationContext, "Napaka: Uporabnik ni bil pravilno pridobljen.", Toast.LENGTH_SHORT).show()
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

    private fun setupRecyclerView(inventoryList: List<InventoryItem>) {
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        adapter = MyAdapterFilter(inventoryList, this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        if (inventoryList.isEmpty()) {
            binding.emptyTextView.isVisible = true
        }

    }
}