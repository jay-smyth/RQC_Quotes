package com.example.rqcquotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LogIn extends AppCompatActivity {
    //Create globals
    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passField;
    private TextView errText;
    private String TAG = "Testing:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        LogIn.this.setTitle("Log In");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.emailField);
        passField = findViewById(R.id.passField);
        errText = findViewById(R.id.textInput_error);

        Button logInSendBtn = findViewById(R.id.logInSendBtn);
        Button canBtn = findViewById(R.id.cancelBtn);


        logInSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtEmail = emailField.getText().toString();
                String txtPass = passField.getText().toString();

                if(!txtEmail.isEmpty() && !txtPass.isEmpty()) {
                    //Validate email address before sending
                    if(Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
                        logIn(txtEmail, txtPass);
                    } else {
                        emailField.setError("Invalid Email");
                    }

                } else if(txtEmail.isEmpty()){

                    emailField.setError("Email required");

                } else if(txtPass.isEmpty()){

                    passField.setError("Password required");

                }
            }
        });

        //Cancel button to return user to main_activity
        canBtn.setOnClickListener((view -> {
            startActivity(new Intent(LogIn.this, MainActivity.class));
        }));

    }

    private void logIn(String email, String pass){
        mAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LogIn.this,"Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LogIn.this, LaunchPad.class));
                finish();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error writing document", e);

                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    //errText.setText(((FirebaseAuthInvalidCredentialsException) e).getErrorCode());  **Redundant but possibly useful function later on
                    errText.setText("Invalid Email or Password");
                } else if(e instanceof FirebaseAuthInvalidUserException){
                    errText.setText("Invalid Email or Password");
                }

            }
        });
    }


}