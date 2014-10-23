package isel.pdm.serie1.birthdayreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static isel.pdm.serie1.birthdayreminder.BirthdayItem.FORMAT;

public class BirthdayAdapter extends BaseAdapter {

    private final List<BirthdayItem> mItems = new ArrayList<BirthdayItem>();
    private final Context mContext;

    public BirthdayAdapter(Context context) {
        mContext = context;
    }

    public void add(BirthdayItem item) {

        mItems.add(item);
        notifyDataSetChanged();
    }

    public void clear() {

        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return mItems.size();

    }

    @Override
    public Object getItem(int pos) {

        return mItems.get(pos);

    }

    @Override
    public long getItemId(int pos) {

        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BirthdayItem birthdayItem = (BirthdayItem)getItem(position);

        LayoutInflater _layoutInflater = (LayoutInflater)mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout itemLayout = (RelativeLayout) _layoutInflater.inflate(R.layout.birthday_item, null);

        final TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        titleView.setText(birthdayItem.getTitle());

        final ImageView imageView = (ImageView) itemLayout.findViewById(R.id.photoImageView);
        imageView.setImageBitmap(birthdayItem.getImageBitmap());

        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
        dateView.setText(FORMAT.format(birthdayItem.getDate()));

        return itemLayout;
    }
}
