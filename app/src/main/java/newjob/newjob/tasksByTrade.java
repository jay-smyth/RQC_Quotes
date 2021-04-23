 package newjob;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rqcquotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class tasksByTrade extends Fragment{

    //Add db query for all tasks associated against what is passed to the ArrayList<String> titles.
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("trades");

     // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
     private static final String ARG_PARAM1 = "param1";

     // TODO: Rename and change types of parameters
     private String mParam1;
     ArrayList<String> titles = new ArrayList<>();
     HashMap<String, Object> dbData = new HashMap<>();
     TextView textView;
     Tradesman tradesGuy = new Tradesman();

     Button addTask;
     Button cancel;
     EditText taskTitle;
     EditText cost;

     //returned addTaskDialog strings
     private String taskName;
     private String taskCost;

     FragmentTransaction ft;
     editPrice ed;

    public void addNames(ArrayList<String> t){
        titles.addAll(t);
    }

    public tasksByTrade() {
        // Required empty public constructor
    }

    //Launch call for new instance of Fragment class  TODO can be removed as new Instance is not necessary
    public static tasksByTrade newInstance(String param1) {
        tasksByTrade fragment = new tasksByTrade();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks_by_trade, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        if(!mParam1.contains("Add Task +")) {
            textView = (TextView)view.findViewById(R.id.fragTest);
            textView.setText(mParam1);
        }

        //Display a list of what trades their are in the db if the taskFragment is not called from tradeForTask
        //The jobRoomDetails class can call this and the dialogFragment attached, to its lifecycle.
        Button newTaskBtn = (Button)view.findViewById(R.id.testBtn);
        newTaskBtn.setOnClickListener(View ->{
            showTaskDialog(mParam1);
        });

        //Retrieve Task data from Trade
        getTasks(mParam1);

        if(Property.getInstance().getRoomTasks() != null) {
            Log.d("Test", "tasksByTrade, singleton return: " + Property.getInstance().getRoomTasks());
        }

        //Result listener returned from addTaskDialog
        getChildFragmentManager().setFragmentResultListener("resultKeyAddTaskDialog", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                taskName = result.getString("taskNameResult");
                taskCost = result.getString("taskCostResult");
                Log.d("Test", "tasksByTrade, returned result from addTaskDialog: " + taskName + taskCost);

                //Add to array
                dbData.put(taskName, taskCost);
                Log.d("Test", "newTradeFragment, trade hash map" + dbData);

                //Add to property taskList
                addToRoomTasks(taskName,taskCost);

                //Update ListView
                displayTaskList();

                //Update db
                tradesGuy.writeTradeToDB(mParam1, dbData);

                //pass back to parent after success
                passBackToAct();
            }
        });

        //Result listener returned from editPrice **start here need to change variables
        getChildFragmentManager().setFragmentResultListener("editPriceReturn", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                taskName = result.getString("returnName");
                taskCost = result.getString("returnCost");
                Log.d("Test", "tasksByTrade, returned result from editPrice: " + taskName + taskCost);

                //Add to array
                dbData.put(taskName, taskCost);
                Log.d("Test", "newTradeFragment, trade hash map" + dbData);

                //Add to property taskList
                addToRoomTasks(taskName,taskCost);

                //Update ListView
                displayTaskList();

                //Update db
                tradesGuy.writeTradeToDB(mParam1, dbData);

                //pass back to parent after success
                passBackToAct();
            }
        });
        //Catch back button press and return to parent Fragment
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("Test", "tasksByTrade, back button press test");
                Bundle result = new Bundle();
                result.putString("taskBackBtn", "Back Button Pressed from tasksByTrade");
                getParentFragmentManager().setFragmentResult("backBtnPress", result);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(tasksByTrade.this,callback);

    }

    //Call custom alert dialog and pass
     public void showTaskDialog(String t){
         //Bundle doesn't have a function to accept vectors?
         FragmentTransaction ftOne = getChildFragmentManager().beginTransaction();
         addTaskDialog d = new addTaskDialog();
         //What? Depricaiated in normal Fragments but not AlertDialogs??
         //d.setRetainInstance(true);
         Bundle args = new Bundle();
         args.putString("title",t);
         d.setArguments(args);
         d.show(ftOne, "From taskByTrade");
     }

     /*
      * Get data from the db when queried against the trade title, from query add tasks to list view with a click event.
      * Click event will open alert dialog to alter price
      */
     public void getTasks(String tradeTitle){
         Log.d("Var t: ", tradeTitle);

         tradesFile.document(tradeTitle)
                 .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if(task.isSuccessful()){
                     //var d is the returned data from db, queried against the title of the trade
                     DocumentSnapshot d = task.getResult();

                     if(d.exists()){
                         //pass db data to hashmap dbData
                         dbData.putAll(d.getData());

                         Log.d("Test", "tasksByTrade, returned db data" + dbData.toString());
                        //Creates and populates tasks list after successful query
                         displayTaskList();

                     } else {
                         Log.d("Test", "tasksByTrade, No dbData back");
                     }
                 } else {
                     Log.d("Test","taskByTrade, Exception say: ", task.getException());
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Log.d("Test", "taskByTrade, Failure from db call against trade title" + e.getCause());
             }
         });

     }

     public void displayTaskList(){
         //Listview to display trade tasks
         ListView taskList = requireView().findViewById(R.id.tradeTaskListview);
         myAdapter adapt = new myAdapter(dbData);
         taskList.setAdapter(adapt);
         taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                 Log.d("Test", "tasksByTrade, view: " + view +", position: " + dbData.get(position) + ", item:" + adapt.getItem(position));
                 adapt.getItem(position);
                 String keyFromMap = adapt.getItem(position).getKey();
                 //Convert object to string and then to double
                 Object valueFromMap = adapt.getItem(position).getValue();
                 String objStr = valueFromMap.toString();
                 Double dub = Double.valueOf(objStr).doubleValue();
                 //Send to start edit price fragment
                 passToFragment(keyFromMap,dub);
             }
         });
     }

     //add editted task and new cost to room task list
    public void addToRoomTasks(String name, String cost){
        HashMap<String, Object> temp = new HashMap<>();
        temp.put(name,cost);
        Property.getInstance().setRoomTasks(temp);
    }

    //Return to JobroomDetails.java to repeat process
    public void passBackToAct(){
        Bundle result = new Bundle();
        result.putString("returnedFromTBT", "Success from tasksByTrade");
        getParentFragmentManager().setFragmentResult("resultKeyTFT", result);
    }





    /*
     *Take click event from list and open edit cost fragment and allow the user to change cost or save task selection,
     *pass all values back to jobRoomDetails
     */
    public void passToFragment(String key, Double value){
        ft = getChildFragmentManager().beginTransaction();
        ed = new editPrice();
        Bundle args = new Bundle();
        args.putString("keyFromMap", key);
        args.putDouble("valFromMap", value);
        ed.setArguments(args);
        ft.replace(R.id.editPriceFrag, ed);
        ft.commit();
    }

}

