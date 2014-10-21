package isel.pdm.serie1.thothNews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static isel.pdm.serie1.thothNews.ThothClassNewItem.Status;

/**
 * Created by Kadete on 15/10/2014.
 */

class CustomListAdapter extends ArrayAdapter<ThothClassNewItem> {


    static private final String TAG = "New-Selected-ID";
    Context context;
    SharedPreferences sharedPref;

    public CustomListAdapter(Context context, int layout, List<ThothClassNewItem> listData) {
        super(context, layout, listData);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ThothClassNewItem newItem = getItem(position);
        View _new = convertView;
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(_new == null){
            _new = mInflater.inflate(R.layout.new_item_layout,null);
            holder = new ViewHolder();

            holder.title = (TextView)_new.findViewById(R.id.new_item_title);
            holder.id = (TextView)_new.findViewById(R.id.new_item_id);
            holder.class_name = (TextView)_new.findViewById(R.id.new_item_class_name);
            holder.when = (TextView)_new.findViewById(R.id.new_item_when);
            holder.checkRead = (CheckBox)_new.findViewById(R.id.new_item_checkread);

            _new.setTag(holder);
        }else{
            holder = (ViewHolder)_new.getTag();
        }
        holder.title.setText(newItem.getTitle());

        holder.when.setText(String.valueOf(newItem.getWhen()));
        holder.checkRead.setChecked(newItem.getStatus() == Status.READ);

        String idStr = String.valueOf(newItem.getId());

        holder.id.setText(idStr);


        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);


        Map<String, ?> allEntries = sharedPref.getAll();
        Set<String> classesNameSelected = sharedPref.getStringSet("multi_select_list_key", null);
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {


            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }



        if(classesNameSelected != null) {

            String[] selected = classesNameSelected.toArray(new String[] {});
            Toast.makeText(context, selected[0], Toast.LENGTH_LONG).show();


//        holder.class_name.setText(String.valueOf(it.next()));

//        while(it.hasNext()){
//            if(it.next() == idStr)
//
//
//        }
//
//        String name = sharedPref.getString(idStr, "N/A");

        }


        holder.title.setTypeface(null,
                (newItem.getStatus() == Status.NOTREAD) ? Typeface.BOLD : Typeface.NORMAL);

        holder.when.setTypeface(null,
                (newItem.getStatus() == Status.NOTREAD) ? Typeface.BOLD : Typeface.NORMAL );

        _new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItem.setStatus(Status.READ);
                holder.checkRead.setChecked(true);

                TextView tv_title = (TextView) v.findViewById(R.id.new_item_title);
                TextView tv_when = (TextView) v.findViewById(R.id.new_item_when);

                tv_title.setTypeface(null, Typeface.NORMAL);
                tv_when.setTypeface(null, Typeface.NORMAL);

                TextView textView = (TextView) v.findViewById(R.id.new_item_id);
                String newId = textView.getText().toString();
                Intent i = new Intent(context, NewViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(TAG, newId);

                v.getContext().startActivity(i);
            }
        });
        return _new;
    }

}

class ViewHolder{
    public TextView title;
    public TextView when;
    public TextView id;
    public TextView class_name;
    public CheckBox checkRead;
}

