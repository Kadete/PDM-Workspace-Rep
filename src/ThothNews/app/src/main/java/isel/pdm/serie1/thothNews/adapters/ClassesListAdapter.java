package isel.pdm.serie1.thothNews.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import isel.pdm.serie1.thothNews.R;
import isel.pdm.serie1.thothNews.activities.NewsActivity;
import isel.pdm.serie1.thothNews.model.ThothClass;

/**
 * Created by Kadete on 28/10/2014.
 */
public class ClassesListAdapter extends ArrayAdapter<ThothClass> {


    static protected final String TAG_SELECT_CLASS_ID = "Class-Selected-ID";
    static protected final String TAG_SELECT_CLASS_NAME = "Class-Selected-Name";
    Context context;

    public ClassesListAdapter(Context context, int layout, List<ThothClass> listData) {
        super(context, layout, listData);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ClassViewHolder holder;
        final ThothClass classItem = getItem(position);
        View _new = convertView;
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(_new == null){
            _new = mInflater.inflate(R.layout.layout_class_item, null);
            holder = new ClassViewHolder();

            holder.id = (TextView)_new.findViewById(R.id.class_item_id);
            holder.full_name = (TextView)_new.findViewById(R.id.class_item_full_name);
            holder.teacher = (TextView)_new.findViewById(R.id.class_item_teacher);

            _new.setTag(holder);
        }else{
            holder = (ClassViewHolder)_new.getTag();
        }

        holder.full_name.setText(classItem.get_fullname());
        holder.teacher.setText(classItem.get_teacher());

        String idStr = String.valueOf(classItem.get_id());
        holder.id.setText(idStr);


        _new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView tv_id = (TextView) v.findViewById(R.id.class_item_id);
                String newId = String.valueOf(tv_id.getText());

                TextView tv_fullname = (TextView) v.findViewById(R.id.class_item_full_name);
                String fullname = String.valueOf(tv_fullname.getText());

                Intent i = new Intent(context, NewsActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                i.putExtra(TAG_SELECT_CLASS_ID, newId);
                i.putExtra(TAG_SELECT_CLASS_NAME, fullname);

                v.getContext().startActivity(i);
            }
        });
        return _new;
    }

}

class ClassViewHolder{
    public TextView id;
    public TextView full_name;
    public TextView teacher;
}
