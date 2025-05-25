package com.example.acar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the updated "About" screen layout
        setContentView(R.layout.activity_about);

        // Example: If needed, initialize components dynamically (currently not required for static content)
        // initViews();
    }

    /**
     * This method can be used to initialize views dynamically, if interactivity is added in the future.
     */
    private void initViews() {
        // Example: Find views by their IDs and set text dynamically if needed
        // TextView projectTitle = findViewById(R.id.project_title);
        // projectTitle.setText("Dynamic Project Title");
    }
}
