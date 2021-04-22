package com.example.rqcquotes;

import androidx.appcompat.app.AppCompatActivity;
import newjob.FirebaseCallBack;
import newjob.NewJobStart;
import newjob.Tradesman;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class LaunchPad extends AppCompatActivity implements FirebaseCallBack {
    //connect to firebase Auth and Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("trades");
    Tradesman tradesman = new Tradesman();
    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_pad);
        Toast.makeText(LaunchPad.this,"Login Successful!", Toast.LENGTH_SHORT).show();

        //initialise and find buttons
        Button newJobBtn = findViewById(R.id.newJobBtn);
        Button actJobBtn = findViewById(R.id.oldJobs);
        Button viewQuoteBtn = findViewById(R.id.viewQuotes);
        Button viewJobDraftsBtn = findViewById(R.id.viewDraft);
        Button contactsBtn = findViewById(R.id.contactsBtn);


        Log.d("Test", "LaunchPad, tradesArray" + tradesman.trades.toString());

        newJobBtn.setOnClickListener(view ->{
            Intent newJobIntent = new Intent(getBaseContext(), NewJobStart.class);
            newJobIntent.putExtra("User ID", mAuthId);
            newJobIntent.putExtra("TradeTitleArray", tradesman.trades);
            startActivity(newJobIntent);
        });

        actJobBtn.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(LaunchPad.this, LogIn.class));
        });

        /*
         *Test db for content, if empty return false and create dummy data, store locally and with firebase
         */
        tradesman.dbTradesToArray(new FirebaseCallBack() {
            @Override
            public void onCallBack(ArrayList<String> list) {
                if(list.isEmpty()){
                    Log.d("Test", "LaunchPad, Call back list empty" + list);
                    tradesman.trades.addAll(list);
                    addDummy();
                } else{
                    tradesman.trades.addAll(list);
                    Log.d("Test", "LaunchPad, List full" + tradesman.trades);
                }
            }
        });

    }

    //Run through each Tradesman type
    public void addDummy(){
        addToList("Electrician");
        addToList("Joiner");
        addToList("Decorator");
    }

    //Create dummy data with Tradesman Class function addTradeNames
    public void addToList(String t){
        Tradesman tradesman = new Tradesman(t);
        if(t.equals("Electrician")){
            tradesman.addTradeNames(t);
        } else if (t.equals("Joiner")){
            tradesman.addTradeNames(t);
        } else {
            tradesman.addTradeNames(t);
        }
    }
    //Pass to arrayList after async result
    @Override
    public void onCallBack(ArrayList<String> list) {
        arrayList.addAll(tradesman.trades);
        Log.d("Test", "LaunchPad, Callback array" + arrayList);
    }
}