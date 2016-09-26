package xiaomeng.bupt.com.daylynews.activity.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;


import model.StoriesEntity;
import view.RevealBackGroundView;
import xiaomeng.bupt.com.daylynews.R;

/**
 * Created by LYW on 2016/9/23.
 */
public class NewsContentActivity extends AppCompatActivity {
    private WebView mWebView;
    private CoordinatorLayout coordinatorLayout;
    private RevealBackGroundView revealBackGroundView;
    private Toolbar mToolBar;
    private StoriesEntity entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content_layout);
        initView();
        initDate();
    }

    private void initView() {

    }

    private void initDate() {
    }
}
