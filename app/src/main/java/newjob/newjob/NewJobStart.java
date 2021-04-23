package newjob;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.rqcquotes.R;

import java.util.ArrayList;

public class NewJobStart extends AppCompatActivity {
    //UI Globals
    private EditText titleEditText;
    private String titleOfRoom;
    //For pass TradeTitles from LaunchPad.java
    private ArrayList<String> tradeTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job_start);

        titleEditText = findViewById(R.id.jobTitle);
        Bundle bundle = getIntent().getExtras();
        //Get passed Trade Titles from bundle
        if (bundle != null) {
            tradeTitles = bundle.getStringArrayList("TradeTitleArray");
            Log.d("Test", "NewJobStart, tradeTitles received from bundle" + tradeTitles + ", Room count: " + Property.getInstance().getRoomCount());
            //Check for created rooms and present to listView
            if(Property.getInstance().getRoomCount() > 0) {
                Log.d("Test", "NewJobStart - return, before clear: " + Property.getInstance().getRoomObjectFromResult());
                Property.getInstance().getRoomTasks().clear();
                //Present created rooms to listView
                putToList();
            }
        } else {
            Log.d("Test", "NewJobStart, tradeTitles received nothing from bundle" + tradeTitles);
            //TODO add recovery option to retrieve data or create new instances of it.
            }

       //Begin room selection and push to array
        Button addRoom = findViewById(R.id.addRoom);
        addRoom.setOnClickListener(view -> {
            //Dropdown menu for room type selection
            PopupMenu roomTypeMenu = new PopupMenu(NewJobStart.this, view);
            for(int i = 0; i < Property.getInstance().getRoomType().length; i++) {
                roomTypeMenu.getMenu().add(Property.getInstance().getRoomType()[i]);
            }
            roomTypeMenu.getMenu().add("Add room type +");
            roomTypeMenu.show();
            //Get room type and add to Property Room Name
            roomTypeMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    titleOfRoom = titleEditText.getText().toString();
                    if(!item.toString().equals("Add room type +")){
                        if(titleOfRoom.equals("")){
                            Property.getInstance().setRoomName(item.toString());
                            Log.d("Test", "NewJobStart - menu click, room type: " + item);
                        }else {
                            Property.getInstance().setRoomName(item.toString() + ": " + titleOfRoom);
                            Log.d("Test", "NewJobStart - menu click, room name edit text full: " + titleOfRoom);
                        }

                        passToIntent(tradeTitles);
                    }
                    return false;
                }
            });
        });

        Button savePropertyBtn = (Button)findViewById(R.id.saveJobBtn);
        savePropertyBtn.setOnClickListener(View -> {
            Log.d("Test", "NewJobStart - menu click, room name edit text full: " + titleOfRoom);
            //Property.getInstance().writeTradeToDB(Property.getInstance().getRoomName(), Property.getInstance().getRoomObjectFromResult());
            Intent finalDetails = new Intent(getBaseContext(), newjob.finalDetails.class);
            startActivity(finalDetails);
        });

    }

    //Create new room activity
    public void passToIntent(ArrayList<String> tradeTitles){
        Intent newJobIntent = new Intent(getBaseContext(), jobRoomDetails.class);
        newJobIntent.putExtra("TradeTitleArray", tradeTitles);

        startActivity(newJobIntent);
    }
    //Present saved rooms to listview
    public void putToList(){
        ListView listView = findViewById(R.id.roomList);
        Log.d("Test", "NewJobStart - return, after clear: " + Property.getInstance().getRoomObjectFromResult());
        myAdapter adapter = new myAdapter(Property.getInstance().getRoomObjectFromResult());
        listView.setAdapter(adapter);
        ;
    }
}

