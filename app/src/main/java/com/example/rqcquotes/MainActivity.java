package com.example.rqcquotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;

import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.setTitle("Welcome to RQC Quotes");

        Button logOutBtn = findViewById(R.id.logOutBtn);
        Button logInBtn = findViewById(R.id.logInSendBtn);
        Button registerBtn = findViewById(R.id.registerBtn);


        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, LaunchPad.class));
            finish();
        } else {
            Toast.makeText(MainActivity.this,"Login Unsuccessful!", Toast.LENGTH_SHORT).show();
        }

    logInBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent (MainActivity.this, LogIn.class));
        }
    });

     registerBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, RegisterUser.class));
        }
    });

     logOutBtn.setOnClickListener(view -> {
         mAuth.signOut();
         Log.d("Not signed out", "UiD = " + mAuth.getUid());
     });

    }

}

