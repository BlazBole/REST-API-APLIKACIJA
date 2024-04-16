package com.example.myapiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.Locale

class MyAdapter(private val items: List<InventoryItem>, private val listener: RecyclerViewInterface) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val tvGlava: TextView = itemView.findViewById(R.id.tvGlava)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvDatum: TextView = itemView.findViewById(R.id.tvDatum)
        val tvUlica: TextView = itemView.findViewById(R.id.tvUlica)
        val tvPosta: TextView = itemView.findViewById(R.id.tvPosta)

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

        holder.tvGlava.text = item.inventoryName
        holder.tvEmail.text = "Številka inventarja: ${item.inventoryNumber}"
        holder.tvDatum.text = item.entryDate
        // Dodajte ostale podatke, če jih imate

        // Tukaj lahko nastavite dodatne elemente, kot so slike, če jih imate
    }

    override fun getItemCount(): Int {
        return items.size
    }
}