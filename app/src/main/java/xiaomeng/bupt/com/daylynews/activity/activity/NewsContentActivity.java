package xiaomeng.bupt.com.daylynews.activity.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;

import java.io.IOException;

import model.Content;
import model.StoriesEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Constant;
import util.HttpUtils;
import view.RevealBackGroundView;
import xiaomeng.bupt.com.daylynews.R;

/**
 * Created by LYW on 2016/9/23.
 */
public class NewsContentActivity extends AppCompatActivity implements RevealBackGroundView.OnStateChangeListener{
    private WebView mWebView;
    private CoordinatorLayout coordinatorLayout;
    private RevealBackGroundView revealBackgroundView;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolBar;
    private StoriesEntity entity;
    private Content mContent;
    private MyHandler myHandler = new MyHandler();
    private static final String TAG = "NewsContentActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content_layout);
        entity = (StoriesEntity) getIntent().getSerializableExtra("entity");
        initView();
        initDate();
        setUpReavalBackgroundView(savedInstanceState);
    }

    private void setUpReavalBackgroundView(Bundle savedInstanceState) {
        revealBackgroundView.setOnStateChangeListener(this);
        if (savedInstanceState == null){
            final int[] loaction = getIntent().getIntArrayExtra(Constant.START_LOCATION);
            revealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //remove 一下是干啥
                    revealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    revealBackgroundView.startFromLocation(loaction);
                    return true;
                }
            });

        }
    }

    private void initView() {
        initReavalBackgroundView();
        initCoordinatorLayout();

    }

    private void initReavalBackgroundView() {
        revealBackgroundView = (RevealBackGroundView) findViewById(R.id.id_news_content_revealbackGroundView);
        initCoordinatorLayout();
    }

    private void initCoordinatorLayout() {
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.id_coordinatorLayout);
        //why?
        coordinatorLayout.setVisibility(View.INVISIBLE);
        initAppBarLayout();
        initNestedScrollView();
    }

    private void initNestedScrollView() {
        mWebView = (WebView)findViewById(R.id.id_news_content_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);
    }

    private void initAppBarLayout() {
        mAppBarLayout = (AppBarLayout)findViewById(R.id.id_news_content_appbarlayout);
        mToolBar = (Toolbar)findViewById(R.id.id_news_content_toolbar);
        mToolBar.setTitle("享受阅读的乐趣");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initDate() {
        if (HttpUtils.isNetWorkConnected(NewsContentActivity.this)){
            HttpUtils.get(Constant.CONTENT + entity.getId(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: 请求数据失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    result = result.replaceAll("'","''");
                    paraseJson(result);
                }
            });
        }

    }

    private void paraseJson(String result) {
        Gson gson = new Gson();
        mContent = gson.fromJson(result,Content.class);
        //file的绝对路径对么？
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + mContent.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackGroundView.STATE_FINISHED == state){

            coordinatorLayout.setVisibility(View.VISIBLE);

        }
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_to_left);
    }
}
