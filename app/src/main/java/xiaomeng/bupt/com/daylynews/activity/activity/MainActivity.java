package xiaomeng.bupt.com.daylynews.activity.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import db.CacheDBHelper;
import fragment.MainFragment;
import fragment.MenuFragment;
import fragment.NewsFragment;
import xiaomeng.bupt.com.daylynews.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private SwipeRefreshLayout mSrfLayout;
    private Toolbar mToolbar;
<<<<<<< HEAD
    private MenuFragment menuFragment ;
    private boolean isLight = true ;
    private CacheDBHelper dbHelper;
=======
    private MenuFragment menuFragment;
    private boolean isLight = false;
>>>>>>> 537903c1e5057958806399c601d3e877e0d939ab
    //menuFragment 主题新闻里表的id
    private String curId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new CacheDBHelper(this,1);
        initView();
        LoadLatest();
    }

    public CacheDBHelper getDBHelper(){
        return dbHelper;
    }
    private void LoadLatest() {
        replace();
        curId = "latest";
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_main_layout);
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));
        setStatusBarColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));
        mSrfLayout = (SwipeRefreshLayout) findViewById(R.id.id_reflashlayout);
        mFrameLayout = (FrameLayout) findViewById(R.id.id_fl);
        mSrfLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {


            @Override
            public void onRefresh() {
                replaceFragment();
                mSrfLayout.setRefreshing(false);
            }
        });
        //这个看不出来有什么区别啊
//        mSrfLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.id_action_mode) {
            isLight = !isLight;
            //这句话没啥用
            item.setTitle(isLight ?"夜间模式":"日间模式");

            mToolbar.setBackgroundColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));
            setStatusBarColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));}
        if (curId.equals("latest")){
            ((MainFragment)getSupportFragmentManager().findFragmentByTag("latest")).updateTheme();
        }else {
            ((NewsFragment)getSupportFragmentManager().findFragmentByTag("news")).updateTheme();
        }
        ((MenuFragment)getSupportFragmentManager().findFragmentByTag("news")).updateTheme();

        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment() {
        if (curId.equals("latest")) {
            replace();

        } else {

        }
    }

    private void replace() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                .replace(R.id.id_fl, new MainFragment(), "latest")
                .commit();
    }

    public void setToolBarTitle(String text) {
        mToolbar.setTitle(text);
    }

    public boolean isLight(){return isLight;}

    //这个有何用意
    public void setCurId(String id) {
        curId = id;
    }

    public void coloseMenu() {
        mDrawerLayout.closeDrawers();
    }

    public void setSwipeRefreshEnable(boolean enable) {
        mSrfLayout.setEnabled(enable);
    }

    /**
     * Check if we're running on Android 5.0 or higher
     *
     * @param statusBarColor
     */
    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                //我的理解是FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS代表系统有责任为状态栏填充透明颜色
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
    }
}
