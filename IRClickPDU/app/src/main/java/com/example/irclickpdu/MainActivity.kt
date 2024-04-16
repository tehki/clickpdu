package com.example.irclickpdu

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.os.VibrationEffect
import android.os.Vibrator

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

    // Define the frequency of the NEC protocol
    val NEC_FREQUENCY = 38000
    // Define the bit lengths for NEC protocol
    val NEC_MARK_DURATION = 9000 // Duration of the mark (carrier on) for each bit in microseconds
    val NEC_SPACE_DURATION = 4500 // Duration of the space (carrier off) for logical 0 in microseconds
    val NEC_SPACE_DURATION_LONG = 22500 // Duration of the space (carrier off) for logical 1 in microseconds

    private fun createNECPattern(systemCode: Int, dataCode: Int): IntArray {
        // Convert system code and data code to binary strings
        val systemCodeBinary = String.format("%16s", Integer.toBinaryString(systemCode)).replace(' ', '0')
        val dataCodeBinary = String.format("%8s", Integer.toBinaryString(dataCode)).replace(' ', '0')

        // Combine system code and data code to form the NEC signal
        val signalBinary = "0000" + systemCodeBinary + dataCodeBinary

        // Convert the binary signal to an IntArray of mark and space durations
        val pattern = IntArray(signalBinary.length * 2)
        for (i in signalBinary.indices) {
            val index = i * 2
            pattern[index] = NEC_MARK_DURATION
            pattern[index + 1] = if (signalBinary[i] == '1') NEC_SPACE_DURATION_LONG else NEC_SPACE_DURATION
        }
        return pattern
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun sendNECIRSignal(context: Context, pattern: IntArray) {

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
    private fun vibrateShort(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Get the Vibrator service
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

            // Check if the device supports vibration
            if (vibrator?.hasVibrator() == true) {
                // Vibrate for a short duration
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        100,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun powerClick(code: String) {
        // Show a toast message with the input string
        Toast.makeText(this, code, Toast.LENGTH_SHORT).show()
        vibrateShort(this)

        val systemCode = 0x00FF
        val dataCode = 0x04
        val pattern = createNECPattern(systemCode, dataCode)
        sendNECIRSignal(this, pattern)
    }


}