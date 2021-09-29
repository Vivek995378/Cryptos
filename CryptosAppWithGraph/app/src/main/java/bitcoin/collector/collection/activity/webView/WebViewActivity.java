package bitcoin.collector.collection.activity.webView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import bitcoin.collector.collection.R;

import static bitcoin.collector.collection.util.AppUtil.setStatusTheme;

public class WebViewActivity extends AppCompatActivity {

    private TextView tvTitle;
    private WebView webview;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        setStatusTheme(this);

        tvTitle = findViewById(R.id.textView49);
        webview = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar10);
        webview.setWebChromeClient(new WebChromeClient() {
            @SuppressLint("NewApi")
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                try {
                    if (newProgress < 100) {
                        progressBar.setProgress(newProgress);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        if (getIntent().getBooleanExtra("isAbout", false)) {
            tvTitle.setText("About Us");
        } else {
            tvTitle.setText("Privacy policy");
        }
        webview.setWebViewClient(new WebClient());
        WebSettings set = webview.getSettings();
        set.setJavaScriptEnabled(true);
        String url = getIntent().getStringExtra("url").trim();
        if (!url.contains("https://")) {
            url = "https://" + url;
        }
        webview.loadUrl(url);
    }

    public void backWebView(View view) {
        finish();
    }

    private class WebClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            try {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}