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

        public myAdapter(Map<String, Object> map){
            data = new ArrayList<>();
            data.addAll(map.entrySet());
            //testing git
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Map.Entry<String, Object> getItem(int position){
            return (Map.Entry)data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final View result;
            if(view == null){
                result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_adpater, viewGroup, false);
            } else {
                result = view;
            }

            Map.Entry<String, Object> item = getItem(i);

            ((TextView)result.findViewById(android.R.id.text1)).setText(item.getKey());
            ((TextView)result.findViewById(android.R.id.text2)).setText("£" + item.getValue().toString());

            return result;
        }



}
