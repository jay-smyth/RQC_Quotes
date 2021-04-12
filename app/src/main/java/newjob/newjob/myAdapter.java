package newjob;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rqcquotes.R;

import java.util.ArrayList;
import java.util.Map;

public class myAdapter extends BaseAdapter{
        private final ArrayList data;

        //Constructor with Map data type parameter
        public myAdapter(Map<String, Object> map){
            data = new ArrayList<>();
            data.addAll(map.entrySet());
        }

        //Return item total within the list
        @Override
        public int getCount() {
            return data.size();
        }

        //Return List item at specified position
        @Override
        public Map.Entry<String, Object> getItem(int position){
            return (Map.Entry)data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        //
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final View result;
            if(view == null){
                //inflate the custom layout for myAdapter ListViews
                result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_adpater, viewGroup, false);
            } else {
                result = view;
            }
            //Get ListView Item
            Map.Entry<String, Object> item = getItem(i);
            //Set results for each list item to TextViews in Custom Layout
            ((TextView)result.findViewById(android.R.id.text1)).setText(item.getKey());
            ((TextView)result.findViewById(android.R.id.text2)).setText("£" + item.getValue().toString());

            return result;
        }



}
