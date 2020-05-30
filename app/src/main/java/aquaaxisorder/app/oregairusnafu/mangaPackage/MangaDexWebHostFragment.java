package aquaaxisorder.app.oregairusnafu.mangaPackage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import aquaaxisorder.app.oregairusnafu.R;

public class MangaDexWebHostFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manga_mangadex_webhost_fragment, container, false);
        WebView mangaWebView = view.findViewById(R.id.fragment_manga_webview);
        mangaWebView.getSettings().setJavaScriptEnabled(true);
        mangaWebView.getSettings().setLoadWithOverviewMode(true);
        mangaWebView.getSettings().setUseWideViewPort(true);
        mangaWebView.getSettings().setDomStorageEnabled(true);
        mangaWebView.loadUrl("https://mangadex.org/chapter/725ef732-d3aa-4aac-8e8d-befc38413d16");
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mangadex.org/chapter/725ef732-d3aa-4aac-8e8d-befc38413d16")));
        return view;
    }


}
