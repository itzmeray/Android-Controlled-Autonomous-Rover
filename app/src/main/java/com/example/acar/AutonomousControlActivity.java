package com.example.acar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class AutonomousControlActivity extends AppCompatActivity {

    // TextViews to display sensor data
    private TextView textViewTempHumidity, textViewGPS, textViewRaindrop, textViewMoisture;

    // Button to return to Manual Control mode
    private Button returnToManualButton;

    // Firebase Database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomous_control);

        // Initialize UI components
        textViewTempHumidity = findViewById(R.id.textView_temp_humidity);
        textViewGPS = findViewById(R.id.textView_gps);
        textViewRaindrop = findViewById(R.id.textView_raindrop);
        textViewMoisture = findViewById(R.id.textView_moisture);
        returnToManualButton = findViewById(R.id.returnToManualButton);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Set listener for returning to Manual Control mode
        returnToManualButton.setOnClickListener(v -> {
            startActivity(new Intent(AutonomousControlActivity.this, ManualControlActivity.class));
            finish(); // Finish current activity to avoid stacking
        });

        // Fetch and update real-time sensor data
        fetchSensorData();

        // Activate autonomous behavior
        startAutonomousBehavior();
    }

    private void fetchSensorData() {
        // Listener for Temperature & Humidity data
        databaseReference.child("sensors/dht22").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double temperature = dataSnapshot.child("temperature").getValue(Double.class);
                    Double humidity = dataSnapshot.child("humidity").getValue(Double.class);
                    String tempHumidityData = "Temperature: " + temperature + "\u00B0C\nHumidity: " + humidity + "%";
                    textViewTempHumidity.setText(tempHumidityData);
                } else {
                    textViewTempHumidity.setText("Data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read DHT11 data", databaseError.toException());
            }
        });

        // Listener for GPS Data
        databaseReference.child("sensors/gps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String gpsData = "Latitude: " + latitude + "\nLongitude: " + longitude;
                    textViewGPS.setText(gpsData);
                } else {
                    textViewGPS.setText("GPS data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read GPS data", databaseError.toException());
            }
        });

        // Listener for Raindrop Sensor Data
        databaseReference.child("sensors/raindrop").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean raindropData = dataSnapshot.getValue(Boolean.class);
                    textViewRaindrop.setText(raindropData != null && raindropData ? "Rain detected" : "No rain detected");
                } else {
                    textViewRaindrop.setText("Data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read raindrop data", databaseError.toException());
            }
        });

        // Listener for Moisture Sensor Data
        databaseReference.child("sensors/moisture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer moistureData = dataSnapshot.getValue(Integer.class);
                    textViewMoisture.setText("Soil Moisture: " + moistureData + "%");
                } else {
                    textViewMoisture.setText("Data not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read moisture data", databaseError.toException());
            }
        });
    }

    private void startAutonomousBehavior() {
        // Display toast indicating Autonomous Mode activation
        Toast.makeText(this, "Autonomous Mode Activated", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
