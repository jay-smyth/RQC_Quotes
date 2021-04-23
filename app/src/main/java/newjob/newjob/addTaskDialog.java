package newjob;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.rqcquotes.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class addTaskDialog extends DialogFragment{
    //Trade title for dialog title
    String mString;
    //Result listener
    private taskDialogListener listener;

    //Class interface from jobRoomDetails, interfaces are called from the parent activity class
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
            requireActivity().setTitle(mString);
        }
        //Inflate the layout dialog_add_task.xml
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);

        /*
         *Test for new task and add trade names, this means that the calling window is jobRoomDetails and this is a new task
         * and it will be assigned to a trade upon creation
         */
            builder.setTitle("Add new task and assign it to a trade");
            builder.setView(view)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Get text and double from edittext views
                            EditText taskNameEditText = (EditText)view.findViewById(R.id.taskNameEditText);
                            EditText taskCostEditText = (EditText)view.findViewById(R.id.taskCostEditText);
                            //TODO find out why validating doesn't provide interface feedback
                            if(taskNameEditText.getText().toString().isEmpty()) {
                                taskNameEditText.setError("Please enter a Title");
                                Log.d("Test", "addTaskDialog, edit text boxes empty");
                            } else if(taskCostEditText.getText().toString().isEmpty()){
                                taskCostEditText.setError("Please enter a cost value");
                                Log.d("Test", "addTaskDialog, edit text boxes empty");
                            } else {
                                Log.d("Test", "addTaskDialog, edit text boxes: " + taskNameEditText.getText().toString());
                                //Test interface listener
                                listener.onDialogPositiveClick(taskNameEditText.getText().toString(), taskCostEditText.getText().toString());
                                Bundle result = new Bundle();
                                result.putString("taskNameResult", taskNameEditText.getText().toString());
                                result.putString("taskCostResult", taskCostEditText.getText().toString());
                                getParentFragmentManager().setFragmentResult("resultKeyAddTaskDialog", result);
                            }
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
