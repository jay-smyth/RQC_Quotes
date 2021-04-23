 package newjob;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.rqcquotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

 public class jobRoomDetails extends AppCompatActivity implements addTaskDialog.taskDialogListener {
    //UI Globals
    ArrayList<String> tradeTitles = new ArrayList<>();
    final ArrayList<String> tradeTitlesViewModel = new ArrayList<>();
    private myAdapter adapter;
    //Fragment globals
    FragmentTransaction ftOne;
    newTaskFragment nt;
    tradeForTask tradeForTaskFrag;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_room_details);
        //Set room title in Action bar
        jobRoomDetails.this.setTitle(Property.getInstance().getRoomName());

        //Retrieve array of trade names from previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tradeTitles = bundle.getStringArrayList("TradeTitleArray");
            Log.d("Test", "jobRoomDetails, tradeTitles received from bundle" + tradeTitles);
        }

        //Display tasks that have been created for this room
        ListView taskList = findViewById(R.id.listView);
        adapter = new myAdapter(Property.getInstance().getRoomTasks());
        taskList.setAdapter(adapter);

        //Add new task button, query db and retrieve trades to assign task to.
        Button addTaskBtn = findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(view -> {
            ftOne = getSupportFragmentManager().beginTransaction();
            nt = new newTaskFragment();
            Bundle args = new Bundle();
            nt.setArguments(args);
            ftOne.replace(R.id.tradesmanList, nt);
            ftOne.commit();
        });

        //Add by trade button to open a fragment and allow the selection of trade specific tasks pulled from db
        Button addByBtn = findViewById(R.id.addTaskByTradeBtn);
        //Open top-level fragment to allow user to select which trade tasks to add to the room
        addByBtn.setOnClickListener(view -> {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            tradeForTaskFrag = new tradeForTask();
            if(tradeTitlesViewModel.isEmpty()){
                //Send data to tradeForTask
                Log.d("Data from: ", tradeTitlesViewModel.toString());
            } else {
                tradeForTaskFrag.addNames(tradeTitlesViewModel);
            }
            ft.replace(R.id.tradesmanList, tradeForTaskFrag);
            ft.commit();
        });

        //Save and cancel activity buttons
        Button saveBtn = findViewById(R.id.saveJRDBtn);
        Button cancelBtn = findViewById(R.id.cancelJRDBtn);

        saveBtn.setOnClickListener(View ->{
            if(!Property.getInstance().getRoomTasks().isEmpty()) {
                Bundle resultFinal = new Bundle();
                //Set room name
                String roomName = Property.getInstance().getRoomCount() + ": " + Property.getInstance().getRoomName();
                //Get room tasks HashMap and nest in Room Object
                HashMap<String, Object> roomTasks = new HashMap<>(Property.getInstance().getRoomTasks());
                HashMap<String, Object> room = new HashMap<>();
                room.put(roomName, roomTasks);
                //Pass room HashMap to Property class total room HashMap
                Property.getInstance().setRoomObjectFromResult(room);
                //Update room count
                Property.getInstance().setRoomCount(Property.getInstance().getRoomCount());

                Log.d("Test", Property.getInstance().getRoomObjectFromResult() + " : " + Property.getInstance().getRoomTasks());
                //Intent result launcher for return to NewJobStart.java
                Intent returnedResultIntent = new Intent(getBaseContext(), NewJobStart.class);
                returnedResultIntent.putExtra("addRoomResult", "Success");
                startActivity(returnedResultIntent, resultFinal);
            } else {
                //Test for save button press and an empty room
                Intent returnedResultIntent = new Intent(getBaseContext(), NewJobStart.class);
                returnedResultIntent.putExtra("addRoomReturn", "Cancelled");
                startActivity(returnedResultIntent);
            }
        });

        cancelBtn.setOnClickListener(View ->{
            //TODO Create cancel result to start new room again
        });

        //Return from tradeForTask success
        getSupportFragmentManager().setFragmentResultListener("resultKeyTFT", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString("returnedFromTradeForTask");
                // Do something with the result
                Log.d("Test", "jobRoomDetails, returned result from editPrice fragment" + result + Property.getInstance().getRoomTasks());
                //Removes child fragment
                getSupportFragmentManager().beginTransaction().remove(tradeForTaskFrag).commit();

                //Display tasks that have been created for this room
                ListView taskList = findViewById(R.id.listView);
                adapter = new myAdapter(Property.getInstance().getRoomTasks());
                taskList.setAdapter(adapter);
            }
        });

        //Return from newTaskFrag success
        getSupportFragmentManager().setFragmentResultListener("resultFromNewTaskFrag", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString("newTaskFragResult");
                // Do something with the result#
                Log.d("Test", "jobRoomDetails, returned result from newTaskFrag fragment" + result + Property.getInstance().getRoomTasks());
                //Removes child fragment
                getSupportFragmentManager().beginTransaction().remove(nt).commit();

                //Display tasks that have been created for this room
                ListView taskList = findViewById(R.id.listView);
                adapter = new myAdapter(Property.getInstance().getRoomTasks());
                taskList.setAdapter(adapter);
            }
        });
    }


    /*
     * onDialogPositiveClick, onDialogNegativeClick and onDialogListSelect are interfaces with the addTaskDialog class,
     * the appropriate data is sent back to this parent class for manipulation
     */
    public void onDialogPositiveClick(String taskName, String taskCost){
        Log.d("Test", "newTradeFragment returned value from AlertDialog is: " + taskName + taskCost);
    }

    public void onDialogNegativeClick(DialogFragment dialogFragment){

    }

}

