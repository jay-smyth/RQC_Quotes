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
import android.widget.Toast;

import com.example.rqcquotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

public class NewJobStart extends AppCompatActivity {
    private EditText titleEditText;
    private String titleOfRoom;
    ArrayList<String> tradeTitles = new ArrayList<>();

    myAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job_start);

        titleEditText = findViewById(R.id.jobTitle);
        titleOfRoom = titleEditText.getText().toString();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            tradeTitles = bundle.getStringArrayList("TradeTitleArray");
            Log.d("Test", "NewJobStart, tradeTitles received from bundle" + tradeTitles + ", Room count: " + Property.getInstance().getRoomCount());

            if(Property.getInstance().getRoomCount() > 0) {
                String test = bundle.getString("addRoomResult");
                Log.d("Test", "NewJobStart, result from jobRoomDetails received from bundle" + test);

                if (test.equals("Success")) {
                    Log.d("Test", "NewJobStart - return, before clear: " + Property.getInstance().getRoomObjectFromResult());
                    Property.getInstance().getRoomTasks().clear();
                    putToList();
                }
            }
        } else {
            Log.d("Test", "NewJobStart, tradeTitles received nothing from bundle" + tradeTitles);
            //MY-TO-DO add recovery option to retrieve data or create new instances of it.
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

            roomTypeMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(!item.toString().equals("Add room type +")){
                        if(titleOfRoom.equals("")){
                            Property.getInstance().setRoomName(item.toString());
                            Log.d("Test", "NewJobStart - menu click, room type: " + item);
                        }else {
                            Property.getInstance().setRoomName(titleOfRoom);
                        }

                        passToIntent(tradeTitles);
                    }
                    return false;
                }
            });
        });
        //End of room selection and push


    }


    public void passToIntent(ArrayList<String> tradeTitles){
        Intent newJobIntent = new Intent(getBaseContext(), jobRoomDetails.class);
        //newJobIntent.putExtra("Property Data", Property.getInstance());
        newJobIntent.putExtra("TradeTitleArray", tradeTitles);

        startActivity(newJobIntent);
    }

    public void putToList(){
        listView = findViewById(R.id.roomList);
        Log.d("Test", "NewJobStart - return, after clear: " + Property.getInstance().getRoomObjectFromResult());
        adapter = new myAdapter(Property.getInstance().getRoomObjectFromResult());
        listView.setAdapter(adapter);
        ;
    }
}

