package com.vlw.inventory

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch

class SellActivity : AppCompatActivity() {

    private var current: Saree? = null

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            lookup(result.contents)
        } else if (current == null) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell)

        findViewById<MaterialButton>(R.id.scanNextBtn).setOnClickListener { startScan() }
        findViewById<MaterialButton>(R.id.sellPieceBtn).setOnClickListener { sell(1) }
        findViewById<MaterialButton>(R.id.sellSetBtn).setOnClickListener {
            val s = current
            if (s != null) sell(s.piecesPerSet)
        }

        startScan()
    }

    private fun startScan() {
        val options = ScanOptions()
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        scanLauncher.launch(options)
    }

    private fun lookup(code: String) {
        lifecycleScope.launch {
            val s = AppDb.get(this@SellActivity).dao().byBarcode(code)
            current = s
            if (s == null) {
                Toast.makeText(this@SellActivity, getString(R.string.not_found), Toast.LENGTH_SHORT).show()
                show(null)
            } else {
                show(s)
            }
        }
    }

    private fun show(s: Saree?) {
        val name = findViewById<TextView>(R.id.sellName)
        val stock = findViewById<TextView>(R.id.sellStock)
        val piece = findViewById<TextView>(R.id.sellPiecePrice)
        val set = findViewById<TextView>(R.id.sellSetPrice)
        if (s == null) {
            name.text = getString(R.string.not_found)
            stock.text = ""
            piece.text = ""
            set.text = ""
        } else {
            name.text = s.name
            stock.text = if (s.stock > 0) s.stock.toString() + " in stock" else "Out of stock"
            piece.text = "\u20B9" + s.piecePrice.toInt() + "  / piece"
            set.text = "\u20B9" + s.setPrice.toInt() + "  / set of " + s.piecesPerSet
        }
    }

    private fun sell(qty: Int) {
        val s = current ?: return
        if (s.stock <= 0) {
            Toast.makeText(this, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val dao = AppDb.get(this@SellActivity).dao()
            dao.reduce(s.id, qty)
            val updated = dao.byBarcode(s.barcode)
            current = updated
            show(updated)
            Toast.makeText(this@SellActivity, "Sold", Toast.LENGTH_SHORT).show()
        }
    }
}
