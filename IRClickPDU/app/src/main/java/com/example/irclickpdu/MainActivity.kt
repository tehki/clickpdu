package com.example.irclickpdu

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val powerButton = findViewById<ImageView>(R.id.powerButton)
        powerButton.setOnClickListener {
            powerClick("BOOM")
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun sendNECIRSignal(context: Context, pattern: IntArray) {
        // Define the frequency of the NEC protocol
        val NEC_FREQUENCY = 38000

        // Check if the device supports ConsumerIrManager
        val irManager = context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

        if (irManager != null && irManager.hasIrEmitter()) {
            // Transmit the IR signal with the NEC protocol
            irManager.transmit(NEC_FREQUENCY, pattern)
            Toast.makeText(this, "PEW PEW", Toast.LENGTH_SHORT).show()
        } else {
            // Device does not support IR emitter or ConsumerIrManager
            // Handle this case accordingly
            Toast.makeText(this, "TRANSMITTER IS NOT AVAILABLE", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun powerClick(code: String) {
        // Show a toast message with the input string
        Toast.makeText(this, code, Toast.LENGTH_SHORT).show()
        sendNECIRSignal(this, intArrayOf(0, 0))
    }

}