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

/**
 * Created by Kadete on 03/12/2014.
 */
public class SingleNewFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance){

        View view = inflater.inflate(R.layout.activity_single_new, parent, false);
        final TextView title = (TextView) view.findViewById(R.id.new_view_title);
        final TextView when = (TextView) view.findViewById(R.id.new_view_when);
        final TextView content = (TextView) view.findViewById(R.id.new_view_content);

        Bundle b = getArguments();
        ThothNew thothNew = (ThothNew) b.getSerializable(TagUtils.TAG_SELECT_NEW);

        title.setText(thothNew.getTitle());
        when.setText(thothNew.getFormattedWhen());
        content.setText(thothNew.getContent());

        ContentValues values = new ContentValues();
        values.put(ThothContract.News.READ, 1);
        getActivity().getContentResolver().update(UriUtils.News.parseFromNewID(thothNew.getID()), values, null, null );

        return view;
    }

    public static SingleNewFragment newInstance(ThothNew model){
        SingleNewFragment f = new SingleNewFragment();
        Bundle b = new Bundle();
        b.putSerializable(TagUtils.TAG_SELECT_NEW, model);
        f.setArguments(b);
        return f;
    }
}