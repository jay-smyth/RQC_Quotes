package newjob;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rqcquotes.MyViewModel;
import com.example.rqcquotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link newTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class newTaskFragment extends Fragment {
    //connect to firebase Auth and Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("trades");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<String> tradeTitles = new ArrayList<>();
    private HashMap<String, Object> newTask= new HashMap<String, Object>();

    EditText newTaskTitle;
    EditText newTaskCost;
    ListView listView; 
    int pos;

    Tradesman tradesGuy = new Tradesman();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public newTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment newTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static newTaskFragment newInstance(String param1, String param2) {
        newTaskFragment fragment = new newTaskFragment();
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
            tradeTitles = getArguments().getStringArrayList("tradeTitleArray");
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Test", "newTaskFragment, trade title array = " + tradeTitles);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("Test", "newTaskFragment, trade title array at displayTrades = " + tradeTitles);

        newTaskTitle = (EditText)requireActivity().findViewById(R.id.newTaskEditText);
        newTaskCost = (EditText)view.findViewById(R.id.newTaskPriceEditText);

        listView = view.findViewById(R.id.tradeListview);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_singlechoice, tradeTitles);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("Test", "newTaskFragment, listview item: " + tradeTitles.get(position));
                view.setSelected(true);
                pos = position;
            }
        });


        Button cancel = (Button)view.findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(View ->{
            //Find issue between requireActivity and getActivity, why is there a null possibility with getActivity?
            //An error exists here that when this associated fragment is returned to a different fragmentManager it gets an error
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });



        Button submit = (Button)requireActivity().findViewById(R.id.saveBtn);
        submit.setOnClickListener(View ->{
            if(newTaskTitle != null && newTaskCost != null) {
                String i = newTaskTitle.getText().toString();
                String j = newTaskCost.getText().toString();

                if(i.isEmpty()){
                    Toast.makeText(getContext(), "Please enter a task title", Toast.LENGTH_SHORT).show();
                    newTaskTitle.setError("Task title required");
                } else if (j.isEmpty()){
                    Toast.makeText(getContext(), "Please enter a task cost", Toast.LENGTH_SHORT).show();
                    newTaskCost.setError("Task cost required");
                } else if (!listView.isItemChecked(pos)){
                    Toast.makeText(getContext(), "Please select a trade to assign this task to", Toast.LENGTH_LONG).show();
                } else {
                    if (valDouble(j)) {
                        Log.d("Test", "newTaskFragment, submit validation Passed!");
                        addToRoom(i, j, pos);
                    } else {
                        Toast.makeText(getContext(), "Please enter a numbers and decimals only", Toast.LENGTH_SHORT).show();
                        newTaskCost.setError("Numbers and decimals only");
                    }
                }

            } else if (newTaskTitle == null){
                Toast.makeText(getContext(), "Please enter a task title", Toast.LENGTH_SHORT).show();
                newTaskTitle.setError("Task title required");
            } else if (newTaskCost == null){
                Toast.makeText(getContext(), "Please enter a task cost", Toast.LENGTH_SHORT).show();
                newTaskCost.setError("Task cost required");
            }


        });
    }

    public Boolean valDouble(String valD){
        final String digit_Pattern = "^[1-9]\\d*(\\.\\d+)?$";
        Pattern p = Pattern.compile(digit_Pattern);
        Matcher matcher = p.matcher(valD);
        Log.d("Test", "newTaskFragment, " + matcher.matches());
        return matcher.matches();
    }

    public void addToRoom(String title, String cost, int position){
        Log.d("Test", "newTaskFragment, parameters are: " + title + "  " + cost + "  " + position);
        double conCost = Double.parseDouble(cost);
        newTask.put(title, conCost);

        String tradeName = tradeTitles.get(position);
        Log.d("Test", "newTaskFragment - addToRoom, trade name" + tradeName);
        tradesGuy.writeTradeToDB(tradeName, newTask);

        Log.d("Test", "newTaskFragment, HashMap within class is: " + newTask);
        Property.getInstance().setRoomTasks(newTask);
        Log.d("Test", "newTaskFragment, singleton test for hashmap" + Property.getInstance().getRoomTasks());

        dismissFrag();

        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public void dismissFrag(){
        Bundle result = new Bundle();
        result.putString("newTaskFragResult", "Success from newTaskFrag");
        getParentFragmentManager().setFragmentResult("resultFromNewTaskFrag", result);
    }
}