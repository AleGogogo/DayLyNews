package xiaomeng.bupt.com.daylynews.activity.activity;

import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import fragment.MainFragment;
import fragment.MenuFragment;

import xiaomeng.bupt.com.daylynews.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private SwipeRefreshLayout mSrfLayout;
    private Toolbar mToolbar;
    private MenuFragment menuFragment;
    private boolean isLight;
    //menuFragment 主题新闻里表的id
    private String curId ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        LoadLatest();
    }

    private void LoadLatest() {
        rePlce();
        curId = "latest";
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_main_layout);
        mToolbar = (Toolbar)findViewById(R.id.id_toolbar);
        mSrfLayout = (SwipeRefreshLayout)findViewById(R.id.id_reflashlayout);
        mFrameLayout = (FrameLayout)findViewById(R.id.id_fl);
        mSrfLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {


            @Override
            public void onRefresh() {
                replaceFragment();
                mSrfLayout.setRefreshing(false);
            }
        });
    }

    private void replaceFragment() {
        if (curId.equals("latest")){
            rePlce();

        }else{

        }
    }

    private void rePlce() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right,R.anim.slide_out_to_left)
                .replace(R.id.id_fl,new MainFragment(),"latest")
                .commit();
    }

    public void setToolBarTitle(String text){
        mToolbar.setTitle(text);
    }
    public void setCurId(String id){
        curId = id;
    }
    public void coloseMenu(){
        mDrawerLayout.closeDrawers();
    }
}
