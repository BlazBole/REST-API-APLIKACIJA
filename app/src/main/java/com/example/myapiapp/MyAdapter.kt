package com.example.myapiapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyAdapter(private val items: List<InventoryItem>, private val listener: RecyclerViewInterface) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val tvNumber: TextView = itemView.findViewById(R.id.tvInNumber)
        val tvTitle: TextView = itemView.findViewById(R.id.tvInTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvInInputDate)
        val tvLocation: TextView = itemView.findViewById(R.id.tvInLocation)
        val tvUser: TextView = itemView.findViewById(R.id.tvUser)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            listener.onItemLongClick(adapterPosition)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvNumber.text = item.inventoryNumber
        holder.tvTitle.text = item.inventoryName
        holder.tvDate.text = item.entryDate
        holder.tvLocation.text = item.locationRoom

        // Dodatek: Pridobitev uporabniškega imena iz API-ja na podlagi UserId
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ContactsService::class.java)
        val call = service.getUsernameByUserId(item.userId)

        call.enqueue(object : Callback<ContactItem> {
            override fun onResponse(call: Call<ContactItem>, response: Response<ContactItem>) {
                if (response.isSuccessful) {
                    val contactItem = response.body()
                    val username = contactItem?.email ?: "Unknown" // V primeru, da ni bilo mogoče pridobiti uporabniškega imena
                    holder.tvUser.text = username
                } else {
                    // Napaka pri pridobivanju uporabniškega imena
                    holder.tvUser.text = "Unknown"
                    Toast.makeText(holder.itemView.context, "Napaka pri pridobivanju uporabniškega imena", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ContactItem>, t: Throwable) {
                // Napaka pri klicanju REST API-ja
                holder.tvUser.text = "Unknown"
                Log.e("MainActivity", "Error: ${t.message}")
                Toast.makeText(holder.itemView.context, "Napaka pri izvajanju zahtevka", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun getItemCount(): Int {
        return items.size
    }
}