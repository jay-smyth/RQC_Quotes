package newjob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rqcquotes.LaunchPad;
import com.example.rqcquotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class finalDetails extends AppCompatActivity {
    //Firebase Globals
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("quotes");

    //Input variables
    private String custName;
    private String custEmail;
    private String custPhone;
    private String propOne;
    private String propTwo;
    private String propTown;
    private String propPost;

    //Layout Variables
    EditText custNameET;
    EditText custEmailET;
    EditText custPhoneET;
    EditText propOneET;
    EditText propTwoET;
    EditText propTownET;
    EditText propPostET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_details);
        //Edittexts instantiation
        custNameET = findViewById(R.id.custNameET);
        custEmailET = findViewById(R.id.custEmailET);
        custPhoneET = findViewById(R.id.custPhoneET);
        propOneET = findViewById(R.id.propAddrLineOne);
        propTwoET = findViewById(R.id.propAddrLineTwo);
        propTownET = findViewById(R.id.propAddrTown);
        propPostET = findViewById(R.id.propAddrPost);

        //find and fire button
        Button saveBtn = (Button)findViewById(R.id.saveJRDBtn);
        saveBtn.setOnClickListener(View -> {
            //gather every fields inputs
            custName = custNameET.getText().toString();
            custEmail = custEmailET.getText().toString();
            custPhone = custPhoneET.getText().toString();
            propOne = propOneET.getText().toString();
            propTwo = propTwoET.getText().toString();
            propTown = propTownET.getText().toString();
            propPost = propPostET.getText().toString();
            //validate all input fields
            if(valCust(custName,custEmail,custPhone) && valProp(propOne,propTwo,propTown,propPost)){
                Log.d("Test", "finalDetails, all validation passed");
                //create and map Object data
                HashMap<String, Object>custDetails = new HashMap<>();
                custDetails.put("Name", custName);
                custDetails.put("Email", custEmail);
                custDetails.put("Phone No:", custPhone);
                //create and map Object data
                HashMap<String, Object>propDetails = new HashMap<>();
                propDetails.put("AddrLine1", propOne);
                propDetails.put("AddrLine2", propTwo);
                propDetails.put("Town", propTown);
                propDetails.put("Postcode", propPost);
                propDetails.put("quoteRef", Property.getInstance().checkDB());
                //Test object maps
                Log.d("Test", "finalDetails, all validation passed, customer and address: " + custDetails + "  " + propDetails);
                //create master Hashmap of all data
                HashMap<String,Object>finalCountDown = new HashMap<>();
                finalCountDown.put("CustomerInformation", custDetails);
                finalCountDown.put("PropertyInformation", propDetails);
                finalCountDown.put("PropertyTasks", Property.getInstance().getRoomObjectFromResult());
                //Every quote and all details at time of creation become unique
                tradesFile.document().set(finalCountDown);
                //Return to start and clear Property final result HashMap
                Property.getInstance().clearRoomResObj();
                startActivity(new Intent(finalDetails.this, LaunchPad.class));
                Toast.makeText(finalDetails.this,"Success!, Your quote can be viewed online", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Button cancelBtn = (Button)findViewById(R.id.cancelJRDBtn);
        cancelBtn.setOnClickListener(View -> {

        });
    }
    //validate the customer input data, check for empty then test for basic string, no specials
    public boolean valCust(String cName, String cEmail, String cPhone){
        if(!cName.isEmpty() && !cEmail.isEmpty() && !cPhone.isEmpty()){
            Log.d("Test", "finalDetails, valCust for broad empty");
            if(!basicString(cName)){
                custNameET.setError("Invalid text entry");
            } else if(!valEmail(cEmail)){
                custNameET.setError("Invalid Email");
            } else if(!valPhone(cPhone)){
                custNameET.setError("Invalid phone number");
            } else {
                return true;
            }
        } else {
            if(cName.isEmpty()){
                custNameET.setError("Field required");
            } else if (cEmail.isEmpty()){
                custEmailET.setError("Field required");
            } else {
                custPhoneET.setError("Field required");
            }
        }
        return false;
    }
    //validate the property input data, check for empty then test for basic string, no specials
    public boolean valProp(String pOne, String pTwo, String pTown, String pPost){
        if(!pOne.isEmpty() && !pTwo.isEmpty() && !pTown.isEmpty() && !pPost.isEmpty()){
            Log.d("Test", "finalDetails, valProp for broad empty");
            if(!basicString(pOne)){
                custNameET.setError("Invalid text entry");
            } else if(!basicString(pTwo)){
                custNameET.setError("Invalid text entry");
            } else if(!basicString(pTown)){
                custNameET.setError("Invalid text entry");
            } else if(!basicString(pPost)){
                custNameET.setError("Invalid text entry");
            } else {
                return true;
            }
        } else {
            if(pOne.isEmpty()){
                propOneET.setError("Field required");
            } else if (pTwo.isEmpty()){
                propTwoET.setError("Field required");
            } else if(pTown.isEmpty()){
                propTownET.setError("Field required");
            } else {
                propPostET.setError("Field required");
            }
        }
        return false;
    }
    //test every input for a basic string, no specials
    public boolean basicString (String text){
        Pattern pattern;
        Matcher matcher;
        final String pass_Pattern = "^[0-9A-Za-z -]+$";
        pattern = Pattern.compile(pass_Pattern);
        matcher = pattern.matcher(text);
        return matcher.matches();
    }
    //test for valid email structure
    public boolean valEmail(final String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    //Test for a standard UK mobile number
    public boolean valPhone (String number){
        Pattern pattern;
        Matcher matcher;
        final String pass_Pattern = "^(07[\\d]{8,12}|447[\\d]{7,11})$";

        pattern = Pattern.compile(pass_Pattern);
        matcher = pattern.matcher(number);
        return matcher.matches();
    }

}