// Package declaration specifies the namespace of the project
package com.example.acar;

// Import necessary Android and Firebase libraries for UI handling and user authentication
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// MainActivity class handles login, registration, and mode selection for the app
public class MainActivity extends AppCompatActivity {

    // Firebase Authentication instance to manage user sign-in and sign-up
    private FirebaseAuth mAuth;

    // UI Components for different sections of the app
    private LinearLayout loginSection, registerSection, modeSelectionSection;
    private EditText emailLogin, passwordLogin, emailRegister, passwordRegister, confirmPasswordRegister;
    private Button loginButton, switchToRegisterButton, registerButton, switchToLoginButton;
    private Button manualControlButton, autonomousControlButton, aboutButton;
    private TextView titleText, welcomeMessage;

    // onCreate() method initializes the app's UI and Firebase authentication when the activity is created
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Links to the XML layout file

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Link UI components to XML layout using findViewById
        loginSection = findViewById(R.id.loginSection);
        registerSection = findViewById(R.id.registerSection);
        modeSelectionSection = findViewById(R.id.modeSelectionSection);

        titleText = findViewById(R.id.titleText);
        welcomeMessage = findViewById(R.id.welcomeMessage);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        switchToRegisterButton = findViewById(R.id.switchToRegisterButton);

        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPasswordRegister = findViewById(R.id.confirmPasswordRegister);
        registerButton = findViewById(R.id.registerButton);
        switchToLoginButton = findViewById(R.id.switchToLoginButton);

        manualControlButton = findViewById(R.id.manualControlButton);
        autonomousControlButton = findViewById(R.id.autonomousControlButton);
        aboutButton = findViewById(R.id.aboutButton); // About Button

        // Set click listeners for login and registration actions
        loginButton.setOnClickListener(v -> loginUser()); // Triggers loginUser method
        switchToRegisterButton.setOnClickListener(v -> switchToRegister()); // Switches to registration section

        registerButton.setOnClickListener(v -> registerUser()); // Triggers registerUser method
        switchToLoginButton.setOnClickListener(v -> switchToLogin()); // Switches to login section

        // Mode selection button listeners for manual and autonomous control
        manualControlButton.setOnClickListener(v -> switchToManualControl());
        autonomousControlButton.setOnClickListener(v -> switchToAutonomousControl());
        aboutButton.setOnClickListener(v -> openAboutPage()); // Opens About page
    }

    // Method to handle user login
    private void loginUser() {
        String email = emailLogin.getText().toString().trim(); // Get email input
        String password = passwordLogin.getText().toString().trim(); // Get password input

        // Check for empty input fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase sign-in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            switchToModeSelection(); // Redirect to mode selection screen
                            welcomeMessage.setVisibility(View.VISIBLE);
                            titleText.setText("Android Controlled Autonomous Rover");
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to handle user registration
    private void registerUser() {
        String email = emailRegister.getText().toString().trim();
        String password = passwordRegister.getText().toString().trim();
        String confirmPassword = confirmPasswordRegister.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        switchToLogin(); // Redirect to login after successful registration
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Switch to registration UI
    private void switchToRegister() {
        loginSection.setVisibility(View.GONE);
        registerSection.setVisibility(View.VISIBLE);
    }

    // Switch to login UI
    private void switchToLogin() {
        registerSection.setVisibility(View.GONE);
        loginSection.setVisibility(View.VISIBLE);
    }

    // Switch to mode selection screen after login
    private void switchToModeSelection() {
        loginSection.setVisibility(View.GONE);
        modeSelectionSection.setVisibility(View.VISIBLE);
    }

    // Open Manual Control Activity
    private void switchToManualControl() {
        Intent intent = new Intent(MainActivity.this, ManualControlActivity.class);
        startActivity(intent);
    }

    // Open Autonomous Control Activity
    private void switchToAutonomousControl() {
        Intent intent = new Intent(MainActivity.this, AutonomousControlActivity.class);
        startActivity(intent);
    }

    // Open About Activity to display app information
    private void openAboutPage() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }
}
