package newjob;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rqcquotes.R;

import java.util.HashMap;
import java.util.Set;

public class editPrice extends Fragment {
    //Globals of retrieved Trade title and cost from parent
    private String title;
    private Double cost;

    public editPrice() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //get my arguments
            title = getArguments().getString("keyFromMap");
            cost = getArguments().getDouble("valFromMap");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_price, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Set Trade title to UI
        TextView titleText = (TextView)view.findViewById(R.id.titleText);
        titleText.setText(title);
        //Set cost before editting
        EditText editCost = (EditText)view.findViewById(R.id.editTextCost);
        String localCost = cost.toString();
        editCost.setText(localCost);

        Button saveBtnEditTask = (Button)view.findViewById(R.id.saveBtnEditTask);
        saveBtnEditTask.setOnClickListener(View -> {
            //Create local HashMap before passing to room HashMap
            HashMap<String, Object>tempMap = new HashMap<>();
            String tempString = editCost.getText().toString();

            //Add task to complete room list
            tempMap.put(title, tempString);
            Property.getInstance().setRoomTasks(tempMap);

            Log.d("Test", "editPrice, edited hashMap cost and added to property: " + Property.getInstance().getRoomTasks());

            //Return data to parent fragment
            Bundle result = new Bundle();
            result.putString("returnName", title);
            result.putString("returnCost", tempString);
            getParentFragmentManager().setFragmentResult("editPriceReturn", result);
        });

        Button cancelBtnEditTask = (Button)view.findViewById(R.id.cancelBtnEditTask);
        cancelBtnEditTask.setOnClickListener(View -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
    }

}