package com.example.rqcquotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity {
    private static final String TAG = "RegisterUser";
    //Firebase Auth Global
    private FirebaseAuth mAuth;
    private EditText name;
    private EditText email;
    private EditText passOne;
    private EditText passTwo;
    private TextView emailMsg;
    private TextView passOneMsg;
    private TextView passTwoMsg;
    private String txtName;
    private String txtEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        RegisterUser.this.setTitle("Register User");
        //initialise firebase auth
        mAuth = FirebaseAuth.getInstance();

        //Initialise view global variables
        Button regBtn = findViewById(R.id.registerBtn);
        Button canBtn = findViewById(R.id.canBtn);
        name = findViewById(R.id.etPersonName);
        email = findViewById(R.id.etEmailAddress);
        passOne = findViewById(R.id.etPassword1);
        passTwo = findViewById(R.id.etPassword2);
        emailMsg = findViewById(R.id.emailWarning);
        passOneMsg = findViewById(R.id.passOneWarning);
        passTwoMsg = findViewById(R.id.passTwoWarning);

        //Lambda expression for onClickListener
        regBtn.setOnClickListener(view -> {
            //Get inputs text
            txtName = name.getText().toString();
            txtEmail = email.getText().toString();
            String txtPass_1 = passOne.getText().toString();
            String txtPass_2 = passTwo.getText().toString();
            //Pass input values
            regUser(txtName, txtEmail, txtPass_1, txtPass_2);
        });

        canBtn.setOnClickListener(view ->{
            startActivity(new Intent(RegisterUser.this, MainActivity.class));
            finish();
        });
    }


    //Register user if valForm returns true
    private void regUser(String name, String email, String passOne, String passTwo){
        if(valForm(name, email, passOne, passTwo)){
            mAuth.createUserWithEmailAndPassword(email,passOne)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                //Sign in for the win!!
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d(TAG, "Created User: Success!" + user);
                                addUID();
                                startActivity(new Intent(RegisterUser.this, LaunchPad.class));
                                finish();
                            } else {
                                //Create user fail :\
                                Log.w(TAG, "Create User: Fail!");
                                Toast.makeText(RegisterUser.this,"Account creation failure!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error creating user: ", e);
                            Toast.makeText(RegisterUser.this,"Account creation failure!" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    public boolean valForm(String name, String email, String passOne, String passTwo){
        //test that fields have input
        if(!name.isEmpty() || !email.isEmpty() || !passOne.isEmpty() || !passTwo.isEmpty()){

            if(valEmail(email)){

                if(valPassword(passOne)){
                    Log.v("","" + valPassword(passOne));
                    Toast.makeText(RegisterUser.this,"Passwords valid", Toast.LENGTH_SHORT).show();
                    if(passOne.equals(passTwo)){
                        return true;
                    } else {
                        passTwoMsg.setText(R.string.passMatchError);
                        Toast.makeText(RegisterUser.this,"Password match error", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    passOneMsg.setText(R.string.passError);
                    Toast.makeText(RegisterUser.this,"Password contains Error", Toast.LENGTH_SHORT).show();
                }

            } else{
                emailMsg.setText(R.string.emailError);
            }

        } else {
            //determine what field is empty
            if(name.isEmpty()){
                Toast.makeText(RegisterUser.this,"Name required!", Toast.LENGTH_SHORT).show();
            } else if(email.isEmpty()){
                Toast.makeText(RegisterUser.this,"Email required!", Toast.LENGTH_SHORT).show();
            } else if (passOne.isEmpty()){
                Toast.makeText(RegisterUser.this,"Empty password field!", Toast.LENGTH_SHORT).show();
            } else if (passTwo.isEmpty()){
                Toast.makeText(RegisterUser.this,"Empty password field!", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    public boolean valEmail(final String email){
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return true;
        } else {
            return false;
        }

    }

    public boolean valPassword(final String password){
        Pattern pattern;
        Matcher matcher;
        final String pass_Pattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?([^\\w\\s]|[_])).{8,}$";
        pattern = Pattern.compile(pass_Pattern);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
    //Add User ID to Database
    private void addUID(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("Name", txtName);
        data.put("Email", mAuth.getCurrentUser().getEmail());
        data.put("Telephone", "");

        db.collection("users").document(mAuth.getUid())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }
}