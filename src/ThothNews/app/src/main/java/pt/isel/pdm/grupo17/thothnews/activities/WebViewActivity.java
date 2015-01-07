package pt.isel.pdm.grupo17.thothnews.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.WebViewFragment;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class WebViewActivity extends FragmentActivity {

    public static final String THOTH_HOST = "thoth.cc.e.ipl.pt";
    public static final String URI_CLASSES_ROOT= "http://thoth.cc.e.ipl.pt/classes";

    private WebViewFragment mWebViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_frame_webview);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mWebViewFragment = new WebViewFragment();
            Bundle bundle = getIntent().getExtras();
            mWebViewFragment.init(bundle.getString(TagUtils.TAG_EXTRA_WEB_VIEW_URL));
            transaction.replace(R.id.fragment_WebView, mWebViewFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         return (mWebViewFragment != null && mWebViewFragment.onKeyDown(keyCode)) || super.onKeyDown(keyCode, event);
    }

}