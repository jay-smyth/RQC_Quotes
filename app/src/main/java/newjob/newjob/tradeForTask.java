package newjob;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rqcquotes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

public class tradeForTask extends Fragment implements FirebaseCallBack {

    ArrayList<String> titles = new ArrayList<>();
    ListView list;
    ArrayAdapter<String> adapter;

    private String taskName;
    private String taskCost;
    private String returnedRes;

    private FragmentTransaction ftNewTradeFrag;
    private newTradeFragment trade;
    private FragmentTransaction ftTaskByTrade;
    private tasksByTrade tasksbyTradeFrag;

    //Default constructor
    public tradeForTask(){
    }

    public void addNames(ArrayList<String> t){
        //Test data was brought from the parent activity, jobRoomDetails.j, if not 
        if(t.size() > 0){
            titles = new ArrayList<>(t);
        } else {
            Log.d("No data", "Shit!!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trade_for_task, container, false);
        list = (ListView) view.findViewById(R.id.tradeListview);

        titles.add("Add new Trade +");

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, titles);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), "Clicked: " + titles.get(i), Toast.LENGTH_SHORT).show();
                if(titles.get(i).equals("Add new Trade +")){
                    //Launch a new activity for the create new trade type
                    ftNewTradeFrag = getChildFragmentManager().beginTransaction();
                    trade = new newTradeFragment();
                    ftNewTradeFrag.replace(R.id.tradeFrag, trade);
                    ftNewTradeFrag.commit();
                } else {
                    //Take trade type and open fragment for query of objects data and create tasks
                    ftTaskByTrade = getChildFragmentManager().beginTransaction();
                    tasksbyTradeFrag = tasksByTrade.newInstance(titles.get(i));
                    tasksbyTradeFrag.addNames(titles);
                    ftTaskByTrade.replace(R.id.tradeFrag, tasksbyTradeFrag);
                    ftTaskByTrade.commit();
                }
            }
        });

        //Returned listener from taskByTrade
        getChildFragmentManager().setFragmentResultListener("resultKeyTFT", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                returnedRes = result.getString("returnedFromTBT");
                Log.d("Test", "tradeForTask, returned result from tasksByTrade: " + returnedRes);
                if(returnedRes.equals("Success from tasksByTrade")) {
                    removeChildFrag(returnedRes);
                    passBackToAct();
                }
            }
        });

        getChildFragmentManager().setFragmentResultListener("resultFromNewTradeFrag", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                returnedRes = result.getString("newTradeFragResult");
                Log.d("Test", "tradeForTask, returned result from newTradeFrag: " + returnedRes);
                if(returnedRes.equals("Success from newTradeFrag")) {
                    removeChildFrag(returnedRes);
                    passBackToAct();
                }
            }
        });

        return view;
    }

    public void removeChildFrag(String result){
        //Removes child fragments
        if(result.equals("Success from tasksByTrade")) {
            Log.d("Test", "tradeForTask - removeChildFrag, dismiss tasksByTrade");
            getChildFragmentManager().beginTransaction().remove(tasksbyTradeFrag).commit();
        } else {
            Log.d("Test", "tradeForTask - removeChildFrag, dismiss newTradeFrag");
            getChildFragmentManager().beginTransaction().remove(trade).commit();
        }
    }

    //
    public void passBackToAct(){
        Bundle result = new Bundle();
        result.putString("returnedFromTFT", "Success");
        getParentFragmentManager().setFragmentResult("resultKeyTFT", result);
    }



    @Override
    public void onCallBack(ArrayList<String> list) {

    }
}

