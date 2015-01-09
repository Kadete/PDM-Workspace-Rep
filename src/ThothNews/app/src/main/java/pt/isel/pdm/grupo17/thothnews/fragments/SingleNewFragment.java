package pt.isel.pdm.grupo17.thothnews.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.utils.ResolverUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class SingleNewFragment extends Fragment {

    private ThothNew mThothNew;

    public SingleNewFragment() { }

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
        View view = inflater.inflate(R.layout.fragment_single_new, parent, false);

        final TextView title = (TextView) view.findViewById(R.id.new_view_title);
        final TextView when = (TextView) view.findViewById(R.id.new_view_when);
        final WebView webViewContent = (WebView) view.findViewById(R.id.new_webview_content);

        if(mThothNew != null){
            title.setText(mThothNew.getTitle());
            when.setText(mThothNew.getFormattedWhen());
            webViewContent.setBackgroundColor(Color.TRANSPARENT);
            webViewContent.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

//            String template = "<html><body>%s</body></html>";
//            String content = mThothNew.getContent();
//            if(content.contains("http://")){ // finding links without tag
//                String aux = "";
//                for(String word : content.split(" ")){
//                    if(word.startsWith("http://"))
//                        word = " <a href=\""+ word + "\">"+word +"</a> ";
//                    aux += word + " ";
//                }
//                content = aux;
//            }
//            String body = String.format(template, content);
            webViewContent.loadDataWithBaseURL(null, mThothNew.getContent(), "text/html", "UTF-8", null);
            if(ClassSectionsActivity.isTwoPane())
                ResolverUtils.updateNew(getActivity(),mThothNew.getID(),true);
        }else
            title.setText("Some Error Occur");

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ResolverUtils.updateNew(getActivity(),mThothNew.getID(),true);
        }
    }
}