package pt.isel.pdm.grupo17.thothnews.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

public class SingleNewFragment extends Fragment {

    private ThothNew mThothNew;

    public SingleNewFragment() {
    }

    public static SingleNewFragment newInstance(ThothNew thothNew){
        SingleNewFragment f = new SingleNewFragment();
        Bundle b = new Bundle();
        b.putSerializable(TagUtils.TAG_SERIALIZABLE_NEW, thothNew);
        f.setArguments(b);
        return f;
    }

    @Override
      public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThothNew = (ThothNew) getArguments().getSerializable(TagUtils.TAG_SERIALIZABLE_NEW);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance){
        View view = inflater.inflate(R.layout.activity_single_new, parent, false);

        if(mThothNew != null){
            final TextView title = (TextView) view.findViewById(R.id.new_view_title);
            final TextView when = (TextView) view.findViewById(R.id.new_view_when);
            final TextView content = (TextView) view.findViewById(R.id.new_view_content);

            title.setText(mThothNew.getTitle());
            when.setText(mThothNew.getFormattedWhen());
            content.setText(mThothNew.getContent());
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ContentValues values = new ContentValues();
            values.put(ThothContract.News.READ, 1);
            getActivity().getContentResolver().update(UriUtils.News.parseFromNewID(mThothNew.getID()), values, null, null );
        }
    }

}