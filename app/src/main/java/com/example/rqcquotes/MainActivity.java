package com.example.rqcquotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.setTitle("Welcome to RQC Quotes");
        //Find buttons from Layout file
        Button logOutBtn = findViewById(R.id.logOutBtn);
        Button logInBtn = findViewById(R.id.logInSendBtn);
        Button registerBtn = findViewById(R.id.registerBtn);

        //Verify User returns on an associated account
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, LaunchPad.class));
            finish();
        } else {
            Toast.makeText(MainActivity.this,"Login Unsuccessful!", Toast.LENGTH_SHORT).show();
        }

    //Login button start activity
    logInBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent (MainActivity.this, LogIn.class));
        }
    });

    //Register button start activity
     registerBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, RegisterUser.class));
        }
    });
    //Logout button
     logOutBtn.setOnClickListener(view -> {
         mAuth.signOut();
         Log.d("Not signed out", "UiD = " + mAuth.getUid());
     });

    }

}

