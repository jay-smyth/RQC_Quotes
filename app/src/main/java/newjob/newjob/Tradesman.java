package newjob;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class Tradesman {
    //Firebase essentials
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("trades");
    //Class attributes
    private String title;
    private String taskTitle;
    private double taskCost;
    private String partsTitle;
    private double partsCost;
    public ArrayList<String> trades = new ArrayList<>();

    //Constructors
    public Tradesman(String name){
        setTitle(name);
    }
    public Tradesman() {}

    //Loads data from db and add all trades titles to a variable, data is loaded async so I need callbacks or I get null at the Array when I need it
    public void dbTradesToArray(FirebaseCallBack firebaseCallBack){
        ArrayList<String> list = new ArrayList<>();
                tradesFile.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("Test","Tradesman, we got success");
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Log.d("Test","Tradesman, from db with for loop: " + doc.getId());
                        list.add(doc.getId());
                    }
                    Log.d("Test", "Tradesman, from db as list: " + list.toString());
                    firebaseCallBack.onCallBack(list);
                }
            }
        });
        Log.d("Test","Tradesman, after the onComplete function: " +  list.toString());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTasks(String taskTitle, Integer taskCost) {
        this.taskTitle = taskTitle; this.taskCost = taskCost;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public double getTaskCost(){return taskCost;}

    public void setParts(String partTitle, Integer partsCost) {
        this.partsTitle = partsTitle;this.partsCost = partsCost;
    }

    public String getPartsTitle() {
        return partsTitle;
    }

    public double getPartsCost(){return partsCost;}

    //create each dummy Tradesman and set details to database
    public void addTradeNames(String tTitle){
        Map<String, Object> data = new HashMap<>();
        if(tTitle.contains("Electrician")){
            createSpark(data);
        } else if (tTitle.contains("Joiner")){
            createJoiner(data);
        } else if (tTitle.contains("Decorator")){
            createDecor(data);
        }
        tradesFile.document(tTitle).set(data);
    }

    public void createSpark(Map<String,Object> d) {
        d.put("Wire Plugs", 50);
        d.put("Wire Light", 50);
        d.put("Install earth spike", 220);
        d.put("Equipotential bonding", 50);
    }

    public void createJoiner(Map<String,Object> d){
        d.put("Fit and skim door", 65);
        d.put("Install architrave", 180);
        d.put("Raised exterior decking installation", 2450);
        d.put("New seating area", 450);
    }

    public void createDecor(Map<String,Object> d){
        d.put("Paint room walls", 120);
        d.put("Paint room ceiling", 100);
        d.put("Paint single wall", 65);
        d.put("Gloss and finish woodwork", 265);
    }

    //write any new Tradesman objects and update existing ones with new tasks
    public void writeTradeToDB(String title, HashMap<String, Object>taskList){
        tradesFile.document(title).set(taskList, SetOptions.merge());
    }

}
