package newjob;


import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.HashMap;

import androidx.annotation.NonNull;

public class Property implements Serializable {
    //Firebase Globals
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("quotes");
    //Class attributes and HashMaps of tasks
    private String jobTitle;
    private String roomName;
    private int roomCount = 0;
    int count = 0;
    private String[] custDetails;
    private String[] jobAddr;
    private String[] roomType = {"Bedroom", "Livingroom", "Kitchen", "Bathroom"};
    private final HashMap<String, Object> roomReturnedObj = new HashMap<>();
    private HashMap<String, Object> roomTasks = new HashMap<>();

    private static Property property = new Property();

    public static Property getInstance(){
        return property;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }

    public String getRoomName(){
        return roomName;
    }

    public void setCustDetails(String[] jobAddr) {
        this.custDetails = jobAddr;
    }

    public String[] getCustDetails() {
        return custDetails;
    }

    public void setJobAddr(String[] jobAddr) {
        this.jobAddr = jobAddr;
    }

    public String[] getJobAddr() {
        return jobAddr;
    }

    public void setRoomType(String[] roomType) {
        this.roomType = roomType;
    }

    public String[] getRoomType() {
        return roomType;
    }

    public void setRoomCount(int counter){
        this.roomCount = ++roomCount;
    }
    public int getRoomCount(){return roomCount;}

    //Add room and quotes to final object
    public void setRoomObjectFromResult(HashMap<String, Object> roomHashMap) {
        roomReturnedObj.putAll(roomHashMap);
    }
    public HashMap<String, Object>getRoomObjectFromResult(){
        return roomReturnedObj;
    }

    public void clearRoomResObj(){
        roomReturnedObj.clear();
    }

    public HashMap<String, Object>getRoomTasks(){
        return roomTasks;
    }

    public void setRoomTasks(HashMap<String, Object> tasksHashMap){
        roomTasks.putAll(tasksHashMap);
    }

    //Test database for quotes and get count
    public int checkDB(){
        tradesFile.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot query = task.getResult();
                count = query.size();
                Log.d("Test", "Property.java, quotes count: " + count);
            }
        });

        return count;
    }
}
