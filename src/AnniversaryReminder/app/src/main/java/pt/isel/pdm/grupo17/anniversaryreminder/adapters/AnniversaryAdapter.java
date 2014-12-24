package pt.isel.pdm.grupo17.anniversaryreminder.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pt.isel.pdm.grupo17.anniversaryreminder.R;
import pt.isel.pdm.grupo17.anniversaryreminder.models.AnniversaryItem;
import pt.isel.pdm.grupo17.anniversaryreminder.utils.DateUtils;

public class AnniversaryAdapter extends BaseAdapter {

    private final List<AnniversaryItem> mItems = new ArrayList<>();
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
        View itemLayout = convertView;
        ViewHolder holder;
         /*----------------------SETUP_HOLDER-------------------------*/
        if (itemLayout == null) {
            holder = new ViewHolder();
            LayoutInflater _layoutInflater = (LayoutInflater)mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            itemLayout = _layoutInflater.inflate(R.layout.layout_anniversary_item, null);
            holder.daysView = (TextView) itemLayout.findViewById(R.id.daysView);
            holder.nameView = (TextView) itemLayout.findViewById(R.id.titleView);
            holder.dateView = (TextView) itemLayout.findViewById(R.id.dateView);
            holder.imageView = (ImageView) itemLayout.findViewById(R.id.photoImageView);
            itemLayout.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();
        /*-----------------------CHANGE_PROPS------------------------*/
        int daysLeft = anniversaryItem.getDaysLeft();
        if (daysLeft >= 2){
            itemLayout.setBackgroundResource(R.drawable.darkblue_grad);
            holder.daysView.setText("(" + String.valueOf(daysLeft) + " days left)");
        }
        else if(daysLeft == 1) {
            itemLayout.setBackgroundResource(R.drawable.green_grad);
            holder.daysView.setText("Tomorrow!");
        }
        else if(daysLeft == 0) {
            itemLayout.setBackgroundResource(R.drawable.orange_grad);
            holder.daysView.setText("TODAY!!!");
            holder.daysView.setTextSize(16);
        }
        else
            holder.daysView.setText("Shouldn't appear!!");

        holder.nameView.setText(anniversaryItem.getName());

        holder.dateView.setText(DateUtils.SHOW_DATE_FORMATTER.format(anniversaryItem.getDate()));

        if(anniversaryItem.getThumbnailUri() != null)
            holder.imageView.setImageURI(anniversaryItem.getThumbnailUri());
        else{
            Drawable myPhoto = mContext.getResources().getDrawable( R.drawable.ic_user_default);
            holder.imageView.setImageDrawable(myPhoto);
        }

        return itemLayout;
    }

    static class ViewHolder {
        public TextView daysView;
        public TextView nameView;
        public ImageView imageView;
        public TextView dateView;
    }

}