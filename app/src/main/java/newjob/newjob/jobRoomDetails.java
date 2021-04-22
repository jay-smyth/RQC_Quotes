 package newjob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.rqcquotes.MyViewModel;
import com.example.rqcquotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class jobRoomDetails extends AppCompatActivity implements addTaskDialog.taskDialogListener {
    //connect to firebase Auth and Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("trades");

    //create object of type property to populate from last Intent
    //Property p;

    ArrayList<String> tradeTitles = new ArrayList<>();
    ArrayList<String> tradeTitlesViewModel = new ArrayList<>();

    private myAdapter adapter;

    FragmentTransaction ftOne;
    newTaskFragment nt;
    tradeForTask tradeForTaskFrag;

    private MyViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_room_details);


        /*
         *Retrieve array of trade names from previous activity
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tradeTitles = bundle.getStringArrayList("TradeTitleArray");
            Log.d("Test", "jobRoomDetails, tradeTitles received from bundle" + tradeTitles);
        } else {
            Log.d("Test", "jobRoomDetails, tradeTitles received nothing from bundle" + tradeTitles);
            //MY-TO-DO add recovery option to retrieve data or create new instances of it.
            if(tradeTitles.isEmpty()){
                tradeTitles.addAll(tradeTitlesViewModel);
            }
        }

        //Display tasks that have been created for this room

            ListView taskList = findViewById(R.id.listView);
            adapter = new myAdapter(Property.getInstance().getRoomTasks());
            taskList.setAdapter(adapter);



        /*
         * Add new task button, query db and retrieve trades to assign task to.
         */
        Button addTaskBtn = findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(view -> {
            ArrayList<String> v = new ArrayList<>(tradeTitlesViewModel);

            Log.d("Test trade array", "Trades: " + v.toString());

            ftOne = getSupportFragmentManager().beginTransaction();
            nt = new newTaskFragment();
            Bundle args = new Bundle();
            args.putString("title", "Add new task +");
            args.putStringArrayList("tradeTitleArray", v);
            nt.setArguments(args);
            ftOne.replace(R.id.tradesmanList, nt);
            ftOne.commit();
        });

        /*
        * Add by trade button to open a fragment and allow the selection of trade specific tasks pulled from db
        */
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

                String roomName = Property.getInstance().getRoomCount() + ": " + Property.getInstance().getRoomName();

                HashMap<String, Object> roomTasks = new HashMap<>();
                roomTasks.putAll(Property.getInstance().getRoomTasks());
                HashMap<String, Object> room = new HashMap<>();
                room.put(roomName, roomTasks);

                Property.getInstance().setRoomObjectFromResult(room);

                Property.getInstance().setRoomCount(Property.getInstance().getRoomCount());

                Log.d("Test", Property.getInstance().getRoomObjectFromResult() + " : " + Property.getInstance().getRoomTasks());

                Intent returnedResultIntent = new Intent(getBaseContext(), NewJobStart.class);
                returnedResultIntent.putExtra("addRoomResult", "Success");
                startActivity(returnedResultIntent, resultFinal);
            } else {
                Intent returnedResultIntent = new Intent(getBaseContext(), NewJobStart.class);
                returnedResultIntent.putExtra("addRoomReturn", "Cancelled");
                startActivity(returnedResultIntent);
            }
        });

        cancelBtn.setOnClickListener(View ->{

        });

        //Return from tradeForTask success
        getSupportFragmentManager().setFragmentResultListener("resultKeyTFT", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString("returnedFromTradeForTask");
                // Do something with the result#
                Log.d("Test", "jobRoomDetails, returned result from editPrice fragment" + result + Property.getInstance().getRoomTasks());
                //Removes child fragment
                getSupportFragmentManager().beginTransaction().remove(tradeForTaskFrag).commit();

                //Display tasks that have been created for this room

                ListView taskList = findViewById(R.id.listView);
                adapter = new myAdapter(Property.getInstance().getRoomTasks());
                taskList.setAdapter(adapter);
            }
        });

        //Return from newTaskFrag
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


    public boolean booTrue(){
        return true;
    }
    public boolean booFalse(){return true;}

    private class StableArrayAdapter extends ArrayAdapter<String>{

        HashMap<String, Integer> myMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects){
            super(context, textViewResourceId, objects);
            for(int i = 0; i < objects.size(); ++i){
                myMap.put(objects.get(i), i);
            }
        }
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

