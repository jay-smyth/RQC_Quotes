package newjob;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rqcquotes.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class addTaskDialog extends DialogFragment{

    String mString;
    taskDialogListener listener;

    /*
     * Class interface from jobRoomDetails, interfaces are called from the parent activity class
     */
    public interface taskDialogListener {
        void onDialogPositiveClick(String taskName, String taskPrice);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (taskDialogListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement taskDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(getArguments()!= null) {
            mString = getArguments().getString("title");
        }
        //Inflate the layout dialog_add_task.xml
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);

        //Pattern matcher for literal strings from different addTaskDialog calls
        Pattern p = Pattern.compile("Add new task +");
        Matcher m = p.matcher(mString);
        Pattern q = Pattern.compile("Add Task +");
        Matcher n = q.matcher(mString);

        /*
         *Test for new task and add trade names, this means that the calling window is jobRoomDetails and this is a new task
         * and it will be assigned to a trade upon creation
         */
            //addTaskDialog called by jobRoomDetails new task button
            builder.setTitle("Add new task and assign it to a trade");
            builder.setView(view)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Get text and double from edittext views
                            EditText taskNameEditText = (EditText)view.findViewById(R.id.taskNameEditText);
                            EditText taskCostEditText = (EditText)view.findViewById(R.id.taskCostEditText);

                            Log.d("Test", "addTaskDialog, edit text boxes: " + taskNameEditText.getText().toString());

                            //Save the new task to task hashmap
                            listener.onDialogPositiveClick(taskNameEditText.getText().toString(), taskCostEditText.getText().toString());

                            Bundle result = new Bundle();
                            result.putString("taskNameResult", taskNameEditText.getText().toString());
                            result.putString("taskCostResult", taskCostEditText.getText().toString());
                            getParentFragmentManager().setFragmentResult("resultKeyAddTaskDialog", result);

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Cancel operation and return
                            listener.onDialogNegativeClick(addTaskDialog.this);
                        }
                    });

        return builder.create();
    }
}
