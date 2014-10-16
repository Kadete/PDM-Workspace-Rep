package isel.pdm.serie1.thothNews;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kadete on 15/10/2014.
 */

class CustomListAdapter extends ArrayAdapter<ThothClassNewItem> {

    Context context;

    public CustomListAdapter(Context context, int layout, List<ThothClassNewItem> listData) {
        super(context, layout, listData);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ThothClassNewItem newItem = getItem(position);
        View _new = convertView;
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(_new == null){
            _new = mInflater.inflate(R.layout.new_item_layout,null);
            holder = new ViewHolder();
            holder.id = (TextView)_new.findViewById(R.id.new_item_id);
            holder.title = (TextView)_new.findViewById(R.id.new_item_title);
            holder.when = (TextView)_new.findViewById(R.id.new_item_when);
            _new.setTag(holder);
        }else{
            holder = (ViewHolder)_new.getTag();
        }
        holder.id.setText(String.valueOf(newItem.id));
        holder.title.setText(newItem.title);
        holder.when.setText(newItem.when);
        return _new;
    }

}

class ThothClassNewItem{
public int id;
public String title;
public String when;
public LinksClassNewItem _links;

    ThothClassNewItem(int id, String title, String when, String self){
        this.id = id;
        this.title = title;
        this.when = when;
        _links = new LinksClassNewItem(self);
    }
}

class LinksClassNewItem{
    public String self;
    LinksClassNewItem(String self){
        this.self = self;
    }

}

class ViewHolder{
    public TextView title;
    public TextView when;
    public TextView id;
}

