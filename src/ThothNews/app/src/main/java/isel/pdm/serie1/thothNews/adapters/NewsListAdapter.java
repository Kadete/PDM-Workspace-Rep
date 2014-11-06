package isel.pdm.serie1.thothNews.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import isel.pdm.serie1.thothNews.R;
import isel.pdm.serie1.thothNews.activities.SingeNewActivity;
import isel.pdm.serie1.thothNews.model.ThothClassNewListItem;

import static isel.pdm.serie1.thothNews.model.ThothClassNewListItem.Status;


public class NewsListAdapter extends ArrayAdapter<ThothClassNewListItem> {

    static protected final String TAG_SELECT_NEW_ID = "New-Selected-ID";

    Context context;

    public NewsListAdapter(Context context, int layout, List<ThothClassNewListItem> listData) {
        super(context, layout, listData);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NewItemHolder holder;
        final ThothClassNewListItem newItem = getItem(position);
        View _new = convertView;
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(_new == null){
            _new = mInflater.inflate(R.layout.layout_new_item,null);
            holder = new NewItemHolder();

            holder.title = (TextView)_new.findViewById(R.id.new_item_title);
            holder.new_id = (TextView)_new.findViewById(R.id.new_item_id);
            holder.when = (TextView)_new.findViewById(R.id.new_item_when);
            holder.checkRead = (CheckBox)_new.findViewById(R.id.new_item_checkread);

            _new.setTag(holder);
        }else{
            holder = (NewItemHolder)_new.getTag();
        }
        holder.title.setText(newItem.getTitle());
        holder.when.setText(String.valueOf(newItem.getFormattedWhen()));
        holder.checkRead.setChecked(newItem.getStatus() == Status.READ);

        String idStr = String.valueOf(newItem.getId());
        holder.new_id.setText(idStr);

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
                Intent i = new Intent(context, SingeNewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(TAG_SELECT_NEW_ID, newId);

                v.getContext().startActivity(i);
            }
        });
        return _new;
    }

}

class NewItemHolder {
    public TextView new_id;
    public TextView title;
    public TextView when;
    public CheckBox checkRead;
}

