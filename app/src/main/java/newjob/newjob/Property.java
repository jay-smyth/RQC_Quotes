package newjob;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.HashMap;

public class Property implements Serializable {
    //Firebase Globals
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String mAuthId = mAuth.getUid();
    CollectionReference tradesFile = db.collection("users").document(mAuthId).collection("properties");

    private String jobTitle;
    private String roomName;
    private int roomCount;
    private String[] jobAddr;
    private String[] roomType = {"Bedroom", "Livingroom", "Kitchen", "Bathroom"};
    private HashMap<String, Object> roomReturnedObj = new HashMap<>();
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

    public void setRoomObjectFromResult(HashMap<String, Object> roomHashMap) {
        roomReturnedObj.putAll(roomHashMap);
    }

    public HashMap<String, Object>getRoomObjectFromResult(){
        return roomReturnedObj;
    }


    public HashMap<String, Object>getRoomTasks(){
        return roomTasks;
    }

    public void setRoomTasks(HashMap<String, Object> tasksHashMap){
        roomTasks.putAll(tasksHashMap);
    }

    public void writeTradeToDB(String title, HashMap<String, Object>roomTasks){
        tradesFile.document(title).set(roomTasks);
    }
}
