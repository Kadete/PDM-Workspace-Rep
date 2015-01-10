package pt.isel.pdm.grupo17.thothnews.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

public class WebViewFragment extends Fragment {

    private String curURL;
    public WebView webview;

    public void init(String url) {
        curURL = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_webview, container, false);

        if (curURL != null) {
            webview = (WebView) view.findViewById(R.id.webPage);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new webClient());
            webview.loadUrl(curURL);
        }
        return view;
    }

    public boolean onKeyDown(int keyCode) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return false;
    }

    private class webClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            String host = Uri.parse(url).getHost();
            if (host.equals(UriUtils.THOTH_HOST)) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

}