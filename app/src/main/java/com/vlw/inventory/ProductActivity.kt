package com.vlw.inventory

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            findViewById<TextInputEditText>(R.id.barcodeInput).setText(result.contents)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        findViewById<MaterialButton>(R.id.scanBarcodeBtn).setOnClickListener {
            val options = ScanOptions()
            options.setBeepEnabled(true)
            options.setOrientationLocked(false)
            scanLauncher.launch(options)
        }

        findViewById<MaterialButton>(R.id.saveBtn).setOnClickListener {
            val name = findViewById<TextInputEditText>(R.id.nameInput).text.toString().trim()
            val code = findViewById<TextInputEditText>(R.id.barcodeInput).text.toString().trim()
            val cost = findViewById<TextInputEditText>(R.id.costInput).text.toString().toDoubleOrNull() ?: 0.0
            val piece = findViewById<TextInputEditText>(R.id.pieceInput).text.toString().toDoubleOrNull() ?: 0.0
            val set = findViewById<TextInputEditText>(R.id.setInput).text.toString().toDoubleOrNull() ?: 0.0
            val perSet = findViewById<TextInputEditText>(R.id.perSetInput).text.toString().toIntOrNull() ?: 1
            val stock = findViewById<TextInputEditText>(R.id.stockInput).text.toString().toIntOrNull() ?: 0

            if (name.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "Name and barcode needed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                AppDb.get(this@ProductActivity).dao().save(
                    Saree(
                        barcode = code,
                        name = name,
                        costPrice = cost,
                        piecePrice = piece,
                        setPrice = set,
                        piecesPerSet = perSet,
                        stock = stock
                    )
                )
                Toast.makeText(this@ProductActivity, "Saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
