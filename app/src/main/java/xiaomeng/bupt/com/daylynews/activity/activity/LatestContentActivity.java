package xiaomeng.bupt.com.daylynews.activity.activity;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import db.WebcheDBHelper;
import model.Content;
import model.StoriesEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Constant;
import util.HttpUtils;
import view.RevealBackgroundView;
import xiaomeng.bupt.com.daylynews.R;

/**
 * Created by LYW on 2016/9/23.
 */
public class LatestContentActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {
    private StoriesEntity entity;
    private Content mContent;
    private RevealBackgroundView revealBackgroundView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private WebView mWebView;
    private ImageView iv;
    private WebcheDBHelper dbHelper;
    private static final int SHOW_LATEST_NAEWS = 100;
    private static final String TAG = "LatestContentActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest_content_layout);
        dbHelper = new WebcheDBHelper(this,1);
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
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("replace into Cache(newsId,json) values ("+entity.getId()+",'"+result+"')");
                    result = result.replaceAll("'", "''");
                    parseJson(result);
                }
            });
        }else {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from Cache where newsId = " + entity.getId(), null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseJson(json);
            }
            cursor.close();
            db.close();
        }

    }

    private void parseJson(String result) {
        Gson gson = new Gson();
        mContent = gson.fromJson(result, Content.class);
        Log.d(TAG, "parseJson: mContent is" + mContent.toString());
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
//        Message message = myHander.obtainMessage();
//        message.what = SHOW_LATEST_NAEWS;
//        message.obj = mContent;
//        myHander.sendMessage(message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageLoader.displayImage(mContent.getImage(), iv, options);
                Log.d(TAG, "handleMessage: ");
                String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
                String html = "<html><head>" + css + "</head><body>" + mContent.getBody() + "</body></html>";
                html = html.replace("<div class=\"img-place-holder\">", "");
//                writeHtmlFile(html);
                mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
//                mWebView.setWebViewClient(new WebViewClient(){
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        view.loadUrl(url);
//                        return true;
//                    }
//                });
//                mWebView.loadUrl("http://www.baidu.com");
            }
        });

    }

    private void initView() {

        initAppBarLayout();
        initToolBar();
        initWebView();

    }

    private void initAppBarLayout() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.id_appbarlayout);
        mAppBarLayout.setVisibility(View.INVISIBLE);
        revealBackgroundView = (RevealBackgroundView) findViewById(R.id.id_revealbackgroundview);
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
            setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @TargetApi(21)
    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.
            Window window = this.getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }


    private class MyHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LATEST_NAEWS:
                    mContent = (Content) msg.obj;


            }
        }
    }

    private void writeHtmlFile(String html) {
        try {
            String address = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/daily/html.html";
            File file = new File(address);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(html.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
