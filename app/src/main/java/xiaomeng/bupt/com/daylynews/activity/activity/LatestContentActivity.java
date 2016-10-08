package xiaomeng.bupt.com.daylynews.activity.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
public class LatestContentActivity extends AppCompatActivity implements RevealBackGroundView.OnStateChangeListener {
    private StoriesEntity entity;
    private Content mContent;
    private RevealBackGroundView revealBackgroundView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;

    private WebView mWebView;
    private ImageView iv;
    private MyHander myHander;
    private static final int SHOW_LATEST_NAEWS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest_content_layout);
        initView();
        initData();
        setupRealBackground(savedInstanceState);
    }

    private void setupRealBackground(Bundle savedInstanceState) {
        revealBackgroundView.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);
            revealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    revealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    revealBackgroundView.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            revealBackgroundView.setFinishedFrame();
        }
    }

    private void initData() {
        entity = (StoriesEntity) getIntent().getSerializableExtra("entity");
        mCollapsingToolbarLayout.setTitle(entity.getTitle());
        myHander = new MyHander();
        if (HttpUtils.isNetWorkConnected(LatestContentActivity.this)) {
            HttpUtils.get(Constant.CONTENT + entity.getId(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(LatestContentActivity.this, "没有获取数据成功"
                            , Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    result  = result.replaceAll("'","''");
                    parseJson(result);
                }
            });
        }

    }

    private void parseJson(String result) {
        Gson gson = new Gson();
        mContent = gson.fromJson(result, Content.class);
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        imageLoader.displayImage(mContent.getImage(), iv, options);
        Message message = myHander.obtainMessage();
        message.what = SHOW_LATEST_NAEWS;
        message.obj = mContent;
        myHander.handleMessage(message);

    }

    private void initView() {

        initAppBarLayout();
        initToolBar();
        initWebView();

    }

    private void initAppBarLayout() {
        revealBackgroundView = (RevealBackGroundView) findViewById(R.id.id_revealbackgroundview);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.id_appbarlayout);
        //为何？
        mAppBarLayout.setVisibility(View.INVISIBLE);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
    }


    private void initWebView() {
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

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iv = (ImageView) findViewById(R.id.id_collapsing_toolbar_iv);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_toleft_from_right);

    }

    @Override
    public void onStateChange(int state) {
        if (revealBackgroundView.STATE_FINISHED == state) {
            mAppBarLayout.setVisibility(View.VISIBLE);

        }
    }

    private class MyHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LATEST_NAEWS:
                    mContent = (Content) msg.obj;
                    String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
                    String html = "<html><head>" + css + "</head><body>" + mContent.getBody() + "</body></html>";
                    html = html.replace("<div class=\"img-place-holder\">", "");
                    mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
            }
        }
    }
}
