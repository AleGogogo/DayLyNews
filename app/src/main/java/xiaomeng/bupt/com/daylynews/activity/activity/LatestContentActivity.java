package xiaomeng.bupt.com.daylynews.activity.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import model.Content;
import model.StoriesEntity;
import view.RevealBackGroundView;
import xiaomeng.bupt.com.daylynews.R;

import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

/**
 * Created by LYW on 2016/9/23.
 */
public class LatestContentActivity extends AppCompatActivity {
    private StoriesEntity entity;
    private Content mContent;
    private RevealBackGroundView revealBackGroundView;
    private AppBarLayout mAppBarLayout;
    private WebView mWebView;
    private ImageView iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest_content_layout);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.id_appbarlayout);
        //为何？
        mAppBarLayout.setVisibility(View.INVISIBLE);
        revealBackGroundView = (RevealBackGroundView) findViewById(R.id.id_revealbackgroundview);
        Toolbar toobar = (Toolbar) findViewById(R.id.id_collapsing_toolbar);
        setSupportActionBar(toobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iv = (ImageView) findViewById(R.id.id_collapsing_toolbar_iv);
        mWebView = (WebView) findViewById(R.id.id_nestedscrollview_web);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);

    }
}
