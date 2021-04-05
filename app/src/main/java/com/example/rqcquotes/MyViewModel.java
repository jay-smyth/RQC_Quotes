package com.example.rqcquotes;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import newjob.FirebaseCallBack;
import newjob.Tradesman;

public class MyViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>>tradesVMArrayList;
    private MutableLiveData<HashMap<String, Double>>taskListVM;
    Tradesman tradesman = new Tradesman();

    public LiveData<HashMap<String, Double>> getTasks(){
        if(taskListVM == null){
            taskListVM = new MutableLiveData<HashMap<String,Double>>();
        }
        return taskListVM;
    }

    public void setTasks(HashMap<String,Double> task){
        taskListVM.setValue(task);
        Log.d("Test", "MyViewModel, setTasks to HashMap" + taskListVM);
    }

    public LiveData<ArrayList<String>> getList() {
        if (tradesVMArrayList == null){
            tradesVMArrayList = new MutableLiveData<ArrayList<String>>();
            loadEmIn();
        }
        return tradesVMArrayList;
    }

    private void loadEmIn(){
        //Get the trade titles and load them into an arrayList
        tradesman.dbTradesToArray(new FirebaseCallBack() {
            @Override
            public void onCallBack(ArrayList<String> list) {
                if(list.isEmpty()){
                    Log.d("Test", "MyViewModel, Call back list empty" + list);
                    tradesVMArrayList.setValue(list);
                } else{
                    tradesVMArrayList.setValue(list);
                    Log.d("Test", "MyViewModel, List full" + tradesVMArrayList.toString());
                }
            }
        });
    }

    private void loadTask(){
        
    }

}
