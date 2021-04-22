package newjob;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rqcquotes.MyViewModel;
import com.example.rqcquotes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class newTaskFragment extends Fragment{

    private MyViewModel viewModel;
    ArrayList<String> tradeTitlesViewModel = new ArrayList<>();
    private HashMap<String, Object> newTask= new HashMap<String, Object>();
    ArrayAdapter<String> arrayAdapter;
    //UI globals
    EditText newTaskTitle;
    EditText newTaskCost;
    ListView listView;
    int pos;

    //
    Tradesman tradesGuy = new Tradesman();

    public newTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Viewmodel gets updated database on lifecycle start
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        viewModel.getList().observe(getViewLifecycleOwner(), tradesVMArrayList -> {
            tradeTitlesViewModel.addAll(tradesVMArrayList);
            Log.d("Test", "tradeForTask, VM test- arrayList: " + tradeTitlesViewModel);
            arrayAdapter.notifyDataSetChanged();
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Test var on console
        Log.d("Test", "newTaskFragment, trade title array at displayTrades = " + tradeTitlesViewModel);

        //Both class object references find UI elements in layout file
        newTaskTitle = (EditText)requireActivity().findViewById(R.id.newTaskEditText);
        newTaskCost = (EditText)view.findViewById(R.id.newTaskPriceEditText);
        //view becomes a standard
        listView = view.findViewById(R.id.tradeListview);
        //Basic array adapter of Tradesman Titles
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_singlechoice, tradeTitlesViewModel);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Click gets Trade title checkbox and set true
                Log.d("Test", "newTaskFragment, listview item: " + tradeTitlesViewModel.get(position));
                view.setSelected(true);

                pos = position;
            }
        });


        Button cancel = (Button)view.findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(View ->{
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });


        //Find button click and test edit text boxes input values
        Button submit = (Button)requireActivity().findViewById(R.id.saveBtn);
        submit.setOnClickListener(View ->{
            //test for empty text inputs
            if(newTaskTitle != null && newTaskCost != null) {
                String i = newTaskTitle.getText().toString();
                String j = newTaskCost.getText().toString();
                //Test which is empty, report or validate
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
                        //Pass both editText values and TradeTitle list position
                        addToRoom(i, j, pos);
                    } else {
                        Toast.makeText(getContext(), "Please enter a numbers and decimals only", Toast.LENGTH_SHORT).show();
                        newTaskCost.setError("Numbers and decimals only");
                    }
                }

            } else if (newTaskTitle == null){
                Toast.makeText(getContext(), "Please enter a task title", Toast.LENGTH_SHORT).show();
                newTaskTitle.setError("Task title required");
            } else {
                Toast.makeText(getContext(), "Please enter a task cost", Toast.LENGTH_SHORT).show();
                newTaskCost.setError("Task cost required");
            }


        });
    }
    //validate string double return true or false
    public Boolean valDouble(String valD){
        final String digit_Pattern = "^[1-9]\\d*(\\.\\d+)?$";
        Pattern p = Pattern.compile(digit_Pattern);
        Matcher matcher = p.matcher(valD);
        Log.d("Test", "newTaskFragment, " + matcher.matches());
        return matcher.matches();
    }

    //Task title, cost and Trade title,
    public void addToRoom(String title, String cost, int position){
        Log.d("Test", "newTaskFragment, parameters are: " + title + "  " + cost + "  " + position);
        double conCost = Double.parseDouble(cost);
        //Add to local temp HashMap
        newTask.put(title, conCost);
        //Get trade title and pass to Tradesman Class function writeTradeToDB
        String tradeName = tradeTitlesViewModel.get(position);
        Log.d("Test", "newTaskFragment - addToRoom, trade name" + tradeName);
        tradesGuy.writeTradeToDB(tradeName, newTask);
        //Console test variables and pass to Property class
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