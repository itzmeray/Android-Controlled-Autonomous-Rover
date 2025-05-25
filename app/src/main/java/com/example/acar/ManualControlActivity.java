// ManualControlActivity.java
// This activity handles manual control of the autonomous rover.
// It connects with Firebase to send movement commands and display real-time sensor data.

package com.example.acar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManualControlActivity extends AppCompatActivity {

    // TextViews to display sensor data
    private TextView textViewTempHumidity, textViewGPS, textViewRaindrop, textViewMoisture;

    // Buttons for manual control commands
    private Button buttonForward, buttonBackward, buttonLeft, buttonRight, buttonStop, autonomousControlButton;

    // Reference to the Firebase Realtime Database
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);

        // Initialize UI components
        textViewTempHumidity = findViewById(R.id.textView_temp_humidity);
        textViewGPS = findViewById(R.id.textView_gps);
        textViewRaindrop = findViewById(R.id.textView_raindrop);
        textViewMoisture = findViewById(R.id.textView_moisture);

        buttonForward = findViewById(R.id.button_forward);
        buttonBackward = findViewById(R.id.button_backward);
        buttonLeft = findViewById(R.id.button_left);
        buttonRight = findViewById(R.id.button_right);
        buttonStop = findViewById(R.id.button_stop);
        autonomousControlButton = findViewById(R.id.AutonomousControlButton);

        // Initialize Firebase Database
        initializeFirebase();

        // Set the control mode to manual in Firebase
        setControlMode("manual");

        // Configure button click listeners for manual commands
        setupButtonListeners();

        // Load real-time sensor data from Firebase
        updateSensorData();
    }

    private void initializeFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("Firebase", "Firebase initialized and database reference obtained.");
    }

    private void setControlMode(String mode) {
        databaseReference.child("controlMode").setValue(mode)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Control mode set to: " + mode))
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to set control mode", e);
                    Toast.makeText(ManualControlActivity.this, "Failed to update control mode", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupButtonListeners() {
        buttonForward.setOnClickListener(v -> sendCommand("forward"));
        buttonBackward.setOnClickListener(v -> sendCommand("backward"));
        buttonLeft.setOnClickListener(v -> sendCommand("left"));
        buttonRight.setOnClickListener(v -> sendCommand("right"));
        buttonStop.setOnClickListener(v -> sendCommand("stop"));

        // Switch to autonomous mode
        autonomousControlButton.setOnClickListener(v -> {
            setControlMode("autonomous");
            Intent intent = new Intent(ManualControlActivity.this, AutonomousControlActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void sendCommand(String command) {
        databaseReference.child("commands").setValue(command)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Command sent: " + command))
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to send command", e);
                    Toast.makeText(ManualControlActivity.this, "Failed to send command", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateSensorData() {
        // Temperature and Humidity Data (DHT11)
        databaseReference.child("sensors/dht22").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double temperature = dataSnapshot.child("temperature").getValue(Double.class);
                    Double humidity = dataSnapshot.child("humidity").getValue(Double.class);
                    textViewTempHumidity.setText("Temperature: " + temperature + "°C\nHumidity: " + humidity + "%");
                } else {
                    textViewTempHumidity.setText("Data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read DHT22 data", error.toException());
            }
        });

        // Soil Moisture Data
        databaseReference.child("sensors/moisture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer moisture = snapshot.getValue(Integer.class);
                    textViewMoisture.setText("Soil Moisture: " + moisture + "%");
                } else {
                    textViewMoisture.setText("Data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read moisture data", error.toException());
            }
        });

        // Raindrop Sensor Data
        databaseReference.child("sensors/raindrop").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean raindrop = snapshot.getValue(Boolean.class);
                    textViewRaindrop.setText(raindrop != null && raindrop ? "Rain detected" : "No rain detected");
                } else {
                    textViewRaindrop.setText("Data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read raindrop data", error.toException());
            }
        });

        // GPS Data
        databaseReference.child("sensors/gps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);
                    textViewGPS.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                } else {
                    textViewGPS.setText("GPS data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read GPS data", error.toException());
            }
        });
    }
}