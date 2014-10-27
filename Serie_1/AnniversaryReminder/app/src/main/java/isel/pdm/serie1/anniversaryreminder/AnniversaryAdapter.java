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
import java.util.Iterator;
import java.util.List;

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

        /*TODO: melhorar performance*/
        Iterator<AnniversaryItem> it = mItems.iterator();
        while(it.hasNext()){
            AnniversaryItem annItem = it.next();
            if(annItem.getName().compareTo(item.getName()) == 0){
                mItems.remove(annItem);
                break;
            }
        }

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

        final TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        titleView.setText(anniversaryItem.getName());

        final ImageView imageView = (ImageView) itemLayout.findViewById(R.id.photoImageView);
        imageView.setImageBitmap(anniversaryItem.getImageBitmap());

        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
        dateView.setText(AnniversaryItem.FORMAT.format(anniversaryItem.getDate()));

        return itemLayout;
    }

}
