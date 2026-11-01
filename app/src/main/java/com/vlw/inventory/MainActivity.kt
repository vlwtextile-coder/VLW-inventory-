package com.vlw.inventory

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val adapter = SareeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<RecyclerView>(R.id.list)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        findViewById<MaterialButton>(R.id.scanBtn).setOnClickListener {
            startActivity(Intent(this, SellActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.addBtn).setOnClickListener {
            startActivity(Intent(this, ProductActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val items = AppDb.get(this@MainActivity).dao().all()
            adapter.items = items
            adapter.notifyDataSetChanged()
            findViewById<TextView>(R.id.inStockCount).text = items.count { it.stock > 0 }.toString()
            findViewById<TextView>(R.id.outStockCount).text = items.count { it.stock <= 0 }.toString()
        }
    }
}

class SareeAdapter : RecyclerView.Adapter<SareeAdapter.VH>() {
    var items: List<Saree> = emptyList()

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.nameText)
        val price: TextView = v.findViewById(R.id.priceText)
        val stock: TextView = v.findViewById(R.id.stockText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_saree, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val s = items[position]
        h.name.text = s.name
        h.price.text = "Piece \u20B9" + s.piecePrice.toInt() + "  \u00B7  Set \u20B9" + s.setPrice.toInt()
        when {
            s.stock <= 0 -> {
                h.stock.text = "Out"
                h.stock.setTextColor(Color.parseColor("#B3392F"))
            }
            s.stock <= 3 -> {
                h.stock.text = s.stock.toString() + " left"
                h.stock.setTextColor(Color.parseColor("#C98A1B"))
            }
            else -> {
                h.stock.text = s.stock.toString()
                h.stock.setTextColor(Color.parseColor("#1F5C3E"))
            }
        }
    }

    override fun getItemCount() = items.size
}
