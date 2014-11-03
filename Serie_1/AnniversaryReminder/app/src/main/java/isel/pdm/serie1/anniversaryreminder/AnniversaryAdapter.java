package isel.pdm.serie1.anniversaryreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static isel.pdm.serie1.anniversaryreminder.Utils.*;

public class AnniversaryAdapter extends BaseAdapter {

    private final List<AnniversaryItem> mItems = new ArrayList<AnniversaryItem>();
    private final Context mContext;

    public AnniversaryAdapter(Context context) {
        mContext = context;
    }

    public void orderList(){
        Comparator<AnniversaryItem> myComparator = new Comparator<AnniversaryItem>() {
            public int compare(AnniversaryItem obj1,AnniversaryItem obj2) {
                return obj1.getDate().compareTo(obj2.getDate());
            }
        };
        Collections.sort(mItems, myComparator);
    }

    public void add(AnniversaryItem item) {
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

        final AnniversaryItem anniversaryItem = (AnniversaryItem)getItem(position);

        LayoutInflater _layoutInflater = (LayoutInflater)mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout itemLayout = (RelativeLayout) _layoutInflater.inflate(R.layout.layout_anniversary_item, null);

        final TextView nameView = (TextView) itemLayout.findViewById(R.id.titleView);
        nameView.setText(anniversaryItem.getName());

        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
        dateView.setText(SHOW_DATE_FORMATTER.format(anniversaryItem.getDate()));

        final ImageView imageView = (ImageView) itemLayout.findViewById(R.id.photoImageView);
        imageView.setImageURI(anniversaryItem.getThumbnailUri());

        return itemLayout;
    }

}
