package newjob;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rqcquotes.MyViewModel;
import com.example.rqcquotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/*
 *This class is part of the fragment_new_trade.xml, in this class it will take user data and update the db with a new trade and its first task
 * if applicable.  Zero arguments have been passed but 3 should be returned, a String with a trade title, and a hashmap of a new task and the task cost.
 */
public class newTradeFragment extends Fragment implements addTaskDialog.taskDialogListener {
    //connect to firebase Auth and Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("trades");
    private Tradesman newTradeGuy = new Tradesman();


    // default vars and args
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //my vars
    public String tradeTitle;
    public String taskTitle;
    public String taskCost;

    public HashMap<String, Object> taskList = new HashMap<>();

    int i = 0;

    myAdapter adapt;

    public newTradeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment newTradeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static newTradeFragment newInstance(String param1, String param2) {
        newTradeFragment fragment = new newTradeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //Fragment bug means that to findById requires an object specified viewGroup on which to find UI elements
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_new_trade, container, false);
        //Initialise UI components in here with View class object
        Button saveBtn = view.findViewById(R.id.saveBtn);
        Button canBtn = view.findViewById(R.id.cancelBtn);
        Button addNewTask = view.findViewById(R.id.addTaskBtn);
        EditText newTradeEditText = view.findViewById(R.id.newTradeEditText);

        //Empty arraylist to satisfy parameters for addTaskDialog
        ArrayList<String> t = new ArrayList<>();

        addNewTask.setOnClickListener(View -> {
            tradeTitle = newTradeEditText.getText().toString();
            Log.d("Test, newTradeFrag", "tradeTitle = " + tradeTitle);

            if(!tradeTitle.isEmpty()) {
                //create and inflate addTaskDialog
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                addTaskDialog taskDialog = new addTaskDialog();
                taskDialog.show(ft, "From newTradeFragment");
            } else if (i == 0){
                newTradeEditText.setError(getString(R.string.tradeTitle_err));
                i++;
            } else {
                Toast.makeText(getContext(), "Trade title required", Toast.LENGTH_SHORT).show();
            }
        });

        saveBtn.setOnClickListener(View ->{
            if(!newTradeEditText.getText().toString().isEmpty() && !taskList.isEmpty()) {
                newTradeGuy.writeTradeToDB(tradeTitle, taskList);
                //Write entire list to jobDetails and write updater function at jobRoomDetials Listview
                Property.getInstance().setRoomTasks(taskList);
            }
            dismissFrag();
        });

        getChildFragmentManager().setFragmentResultListener("resultKeyAddTaskDialog", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                taskTitle = result.getString("taskNameResult");
                taskCost = result.getString("taskCostResult");
                Log.d("Test", "newTradeFragment, returned result from alertDialog: " + taskTitle + taskCost);
                //Add to array
                taskList.put(taskTitle, taskCost);
                Log.d("Test", "newTradeFragment, trade hash map" + taskList);
                ListView list = (ListView)view.findViewById(R.id.newTasksListView);
                adapt = new myAdapter(taskList);
                list.setAdapter(adapt);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    public void dismissFrag(){
        Bundle result = new Bundle();
        result.putString("newTradeFragResult", "Success from newTradeFrag");
        getParentFragmentManager().setFragmentResult("resultFromNewTradeFrag", result);
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