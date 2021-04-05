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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link editPrice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class editPrice extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String title;
    Double cost;

    public editPrice() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment editPrice.
     */
    // TODO: Rename and change types and number of parameters
    public static editPrice newInstance(String param1, String param2) {
        editPrice fragment = new editPrice();
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

        TextView titleText = (TextView)view.findViewById(R.id.titleText);
        titleText.setText(title);

        EditText editCost = (EditText)view.findViewById(R.id.editTextCost);
        String localCost = cost.toString();
        editCost.setText(localCost);

        Button saveBtnEditTask = (Button)view.findViewById(R.id.saveBtnEditTask);
        saveBtnEditTask.setOnClickListener(View -> {
            HashMap<String, Object>tempMap = new HashMap<>();
            String tempString = editCost.getText().toString();
            Double tempDouble = Double.parseDouble(tempString);

            //Add task to property
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