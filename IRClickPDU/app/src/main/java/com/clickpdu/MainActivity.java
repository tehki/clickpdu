package com.clickpdu;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the button by its ID
        ImageButton myButton = findViewById(R.id.powerButton);

        // Set a click listener for the button
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This code will execute when the button is clicked
                sendCommand("0xFF", "0x10", 2);
            }
        });
    }
    // Helper method to show a toast message
    private void sendCommand(String addr, String cmd, int repeats) {
        // Convert hexadecimal strings to integers
        int address = Integer.decode(addr);
        int command = Integer.decode(cmd);

        // IRNecFactory.create(command,address,repeats);
        IRMessage msg = IRNecFactory.create(command,address,repeats);
        IRController irController = new IRController(getApplicationContext());
        irController.sendMessage(new IRMessageRequest(msg));

        if (irController.isEnabled()) {
            showToast(cmd + " sent to " + addr);
        } else {
            showToast("Transmitter is not available");
        }
    }

    // Helper method to show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}