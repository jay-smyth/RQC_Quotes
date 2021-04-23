package newjob;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rqcquotes.MyViewModel;
import com.example.rqcquotes.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class tradeForTask extends Fragment implements FirebaseCallBack {
    //UI Globals
    ArrayList<String> titles = new ArrayList<>();
    ListView list;
    ArrayAdapter<String> adapter;
    //Result from child fragments
    private String returnedRes;
    //Fragment Globals
    private FragmentTransaction ftNewTradeFrag;
    private newTradeFragment trade;
    private FragmentTransaction ftTaskByTrade;
    private tasksByTrade tasksbyTradeFrag;

    private MyViewModel viewModel;
    ArrayList<String> tradeTitlesViewModel = new ArrayList<>();
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

        //Testing view model
        viewModel = new ViewModelProvider(this).get(MyViewModel .class);
        //View model returns up to date TradeTitles list
        viewModel.getList().observe(getViewLifecycleOwner(), tradesVMArrayList -> {
            tradeTitlesViewModel.addAll(tradesVMArrayList);
            Log.d("Test", "tradeForTask, VM test- arrayList: " + tradeTitlesViewModel);
            titles.clear();
            titles.addAll(tradeTitlesViewModel);
            adapter.notifyDataSetChanged();
        });

        //Needed to use view for finding by ID
        View view = inflater.inflate(R.layout.fragment_trade_for_task, container, false);

        //Tradesman titles List with on click functions
        list = (ListView) view.findViewById(R.id.tradeListview);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, titles);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), "Clicked: " + titles.get(i), Toast.LENGTH_SHORT).show();
                    //Take trade type and open fragment for query of objects data and create tasks
                    ftTaskByTrade = getChildFragmentManager().beginTransaction();
                    tasksbyTradeFrag = tasksByTrade.newInstance(titles.get(i));
                    tasksbyTradeFrag.addNames(titles);
                    ftTaskByTrade.replace(R.id.tradeFrag, tasksbyTradeFrag);
                    ftTaskByTrade.commit();
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
        //Returned listener from newTradeFragment
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

        //back button press listener from newTradeFragment
        getChildFragmentManager().setFragmentResultListener("backBtnPress", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                returnedRes = result.getString("taskBackBtn");
                Log.d("Test", "tradeForTask, returned result from newTradeFrag: " + returnedRes);
                if(returnedRes.equals("Back Button Pressed from tasksByTrade")) {
                    removeChildFrag(returnedRes);
                }
            }
        });

        Button addTradesmanBtn = (Button)view.findViewById(R.id.addTradesmanBtn);
        addTradesmanBtn.setOnClickListener(View ->{
            //Launch a new activity for the create new trade type
            ftNewTradeFrag = getChildFragmentManager().beginTransaction();
            trade = new newTradeFragment();
            ftNewTradeFrag.replace(R.id.tradeFrag, trade);
            ftNewTradeFrag.commit();
        });

        return view;
    }

    public void removeChildFrag(String result){
        //Removes child fragments and find back button presses
        switch (result) {
            case "Success from tasksByTrade":
                Log.d("Test", "tradeForTask - removeChildFrag, dismiss tasksByTrade");
                getChildFragmentManager().beginTransaction().remove(tasksbyTradeFrag).commit();
                break;
            case "Success from newTradeFrag":
                Log.d("Test", "tradeForTask - removeChildFrag, dismiss newTradeFrag");
                getChildFragmentManager().beginTransaction().remove(trade).commit();
                break;
            case "Back Button Pressed from tasksByTrade":
                Log.d("Test", "Back Button Pressed from tasksByTrade, passed but not removed frag");
                getChildFragmentManager().beginTransaction().remove(tasksbyTradeFrag).commit();
                break;
        }
    }

    //Set fragment result to parent
    public void passBackToAct(){
        Bundle result = new Bundle();
        result.putString("returnedFromTFT", "Success");
        getParentFragmentManager().setFragmentResult("resultKeyTFT", result);
    }

    @Override
    public void onCallBack(ArrayList<String> list) {

    }
}

