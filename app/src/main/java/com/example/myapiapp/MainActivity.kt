package com.example.myapiapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapiapp.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import android.Manifest
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log

class MainActivity : AppCompatActivity(), RecyclerViewInterface {

    lateinit var binding: ActivityMainBinding
    lateinit var toggle : ActionBarDrawerToggle
    private var user: ContactItem? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var inventoryList: List<InventoryItem>

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        fetchInventoryFromApi()

        val drawerLayout = binding.drawerLayout
        val navView = binding.navView

        val navHeaderView = navView.getHeaderView(0)
        val twUserName = navHeaderView.findViewById<TextView>(R.id.twUserName)
        val profileImage = navHeaderView.findViewById<ImageView>(R.id.profilePic)

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("USERNAME","")

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ContactsService::class.java)
        val call = service.getUserByUsername(username ?: "")

        call.enqueue(object : Callback<ContactItem> {
            override fun onResponse(call: Call<ContactItem>, response: Response<ContactItem>) {
                if (response.isSuccessful) {
                    user = response.body() // Shrani uporabnika kot razredno spremenljivko

                    // Preverite, ali je uporabnik pravilno pridobljen iz odgovora API klica
                    if (user != null) {
                        // Uporabite pridobljene podatke za izpolnitev polj
                        val decodedByteArray = Base64.decode(user!!.image, Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
                        profileImage.setImageBitmap(decodedBitmap)
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

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.drawable.baseline_menu_24, R.drawable.baseline_arrow_back_24)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        navHeaderView.findViewById<ImageView>(R.id.editProfileIcon).setOnClickListener(){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        if (username.isNullOrEmpty()) {
            twUserName.text = "gost"
        } else {
            twUserName.text = username

            val navMenu = navView.menu
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
                R.id.nav_myInventory -> {
                    val intent = Intent(this, UserInputActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_addToInventory -> {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                    } else {
                        IntentIntegrator(this).initiateScan()
                    }
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

        binding.btnAddScan.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                IntentIntegrator(this).initiateScan()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result: IntentResult? =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null && result.contents != null) {
                //TODO
                val barcodeContent = result.contents
                val intent = Intent(this@MainActivity, InputActivity::class.java)
                intent.putExtra("barcodeContent", barcodeContent) // Dodajanje podatkov v intent
                startActivity(intent)

            } else {
                Toast.makeText(this, "Skeniranje preklicano ali ni bilo mogoče prebrati QR kode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
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
            // Izbris itema iz seznama in obvestimo adapter
            inventoryList = inventoryList.filterIndexed { index, _ -> index != position }
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Ne") { dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.create().show()
    }

    fun fetchInventoryFromApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ContactsService::class.java)
        val call = service.getInventory()

        call.enqueue(object : Callback<List<InventoryItem>> {
            override fun onResponse(call: Call<List<InventoryItem>>, response: Response<List<InventoryItem>>) {
                if (response.isSuccessful) {
                    val inventoryList = response.body() ?: emptyList()
                    setupRecyclerView(inventoryList)
                } else {
                    // Napaka pri pridobivanju inventarja
                    Toast.makeText(applicationContext, "Napaka pri pridobivanju inventarja", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<InventoryItem>>, t: Throwable) {
                // Napaka pri klicanju REST API-ja
                Log.e("MainActivity", "Error: ${t.message}")
                Toast.makeText(applicationContext, "Napaka pri izvajanju zahtevka", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(inventoryList: List<InventoryItem>) {
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(inventoryList, this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}