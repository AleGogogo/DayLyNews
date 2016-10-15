package fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import adapter.MainNewsItemAdapter;
import db.CacheDBHelper;
import model.Before;
import model.Latest;
import model.StoriesEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Constant;
import util.HttpUtils;
import view.Kanner;
import xiaomeng.bupt.com.daylynews.R;
import xiaomeng.bupt.com.daylynews.activity.activity.LatestContentActivity;
import xiaomeng.bupt.com.daylynews.activity.activity.MainActivity;

/**
 * Created by LYW on 2016/9/18.
 */
public class MainFragment extends BaseFragment {

    private Kanner kanner;
    private Context mContext;
    private ListView mListView;
    private Before before;
    private Latest latest;
    private String date;
    private boolean isLoading;
    private static final String TAG = "MainFragment";
    private static final int LOADING_LATEST_NEWS = 100;
    private static final int LOADING_BEFORE_NEWS = 101;
    private MyHandler myHandler;
    private MainNewsItemAdapter mAdapter;

    @Override
    protected View initView(final LayoutInflater inflater, ViewGroup container,
                            final Bundle savedInstanceState) {
        ((MainActivity)mActivity).setToolBarTitle("今日热闻");
        myHandler = new MyHandler();
        View view = inflater.inflate(R.layout.main_news_layout,container,false);
        mListView = (ListView) view.findViewById(R.id.id_main_news_listview);
        View header = LayoutInflater.from(mActivity).inflate(R.layout.kanner,null);
        kanner = (Kanner) header.findViewById(R.id.id_kanner);
        kanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
            @Override
            public void click(View v, Latest.Top_stories top_stories) {
            //new 一个story 把top_stories 放进去，因为他们的数据格式差不多，并且story 继承了序列化
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                //为啥/2?
                startingLocation[0] += v.getWidth()/2;
                StoriesEntity storiesEntity = new StoriesEntity();
                storiesEntity.setId(top_stories.getId());
                storiesEntity.setTitle(top_stories.getTitle());
                Intent intent = new Intent(mActivity, LatestContentActivity.class);
                intent.putExtra("entity", storiesEntity);
                intent.putExtra(Constant.START_LOCATION,startingLocation);
                startActivity(intent);
            }
        });
        mListView.addHeaderView(header);
        mAdapter = new MainNewsItemAdapter(mActivity);
        mListView.setAdapter(mAdapter);
        initListener();

        Log.d(TAG, "initView: 初始化已完成");
        return view;
    }

    private void initListener() {
        /**
         * 解决滑动冲突
         */
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            // firstVisibleItem 表示在当前屏幕显示的第一个listItem在整个listView里面的位置（下标从0开始)
            //visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数
            //           totalItemCount表示ListView的ListItem总数
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int
                    visibleItemCount, int totalItemCount) {
                //疑点1
                if (mListView != null && mListView.getChildCount()>0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(
                            firstVisibleItem).getTop() == 0);
                    ((MainActivity)mActivity).setSwipeRefreshEnable(enable);
                }
                if (firstVisibleItem +visibleItemCount == totalItemCount && !isLoading){
                    loaderMore(Constant.BEFORE+date);
                }

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int[] startingLocation = new int[2];
                //什么意思？
                     startingLocation[0] += view.getWidth()/2;
                Log.d(TAG, "onItemClick: mListView 点击成功！");
                Intent intent = new Intent(mActivity, LatestContentActivity.class);
                StoriesEntity entity = (StoriesEntity) parent.getAdapter().getItem(position)    ;
                intent.putExtra(Constant.START_LOCATION,startingLocation);
                intent.putExtra("entity",entity);
                startActivity(intent);
                //感受一下这个是怎么回事？
                mActivity.overridePendingTransition(0,0);
            }
        });
    }

    private void loaderMore( final String s) {
        isLoading = true;
        if (HttpUtils.isNetWorkConnected(mActivity)) {
            HttpUtils.get(s, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    SQLiteDatabase db = ((MainActivity) mActivity).getDBHelper().getWritableDatabase();
                    db.execSQL("replace into CacheList(date,json) values(" + date + ",' " + result + "')");
                    db.close();
                    parseBeforeJson(result);
                }
            });
        }else {
            SQLiteDatabase db = ((MainActivity) mActivity).getDBHelper().getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + date, null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseBeforeJson(json);
            } else {
                db.delete("CacheList", "date < " + date, null);
                isLoading = false;
                Snackbar sb = Snackbar.make(mListView, "没有更多的离线内容了~", Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(getResources().getColor(((MainActivity) mActivity).isLight() ? android.R.color.holo_blue_dark : android.R.color.black));
                sb.show();
            }
            cursor.close();
            db.close();
        }
        }


    @Override
    protected void initData() {
        super.initData();
        loadFirst();
    }

    private void parseBeforeJson(String result) {
        Gson gson = new Gson();
        before = gson.fromJson(result, Before.class);
        if (before == null){
            //为什么要把这里设为false?
            isLoading = false;
            return;
        }
        Message message = myHandler.obtainMessage();
        message.what = LOADING_BEFORE_NEWS;
        message.obj = latest;
        myHandler.sendMessage(message);

    }

    private void loadFirst() {
        isLoading = true;
        if (HttpUtils.isNetWorkConnected(mActivity)){
            HttpUtils.get(Constant.LATEST_NEWS, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: ----loadFirst");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                    CacheDBHelper dbHelper = ((MainActivity) mActivity).getDBHelper();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("replace into CacheList(date,json) values("+Constant.LATEST_COLUMN+",' " + json+ "')");
                    db.close();
                    parseLatesJson(json);
                }
            });
        }else {
            CacheDBHelper dbHelper = ((MainActivity) mActivity).getDBHelper();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + Constant.LATEST_COLUMN, null);
            if (cursor.moveToNext()){
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseLatesJson(json);
            }else {
                isLoading = false;
            }

        }
    }

    private void parseLatesJson(String json) {
        Gson gson = new Gson();
        Log.d(TAG, "parseLatesJson: original jsonString["+json+"]");
        latest = gson.fromJson(json,Latest.class);

        Log.d(TAG, "latest is "+latest.toString());

        Message message = myHandler.obtainMessage();
        message.what = LOADING_LATEST_NEWS;
        message.obj = latest;
        myHandler.sendMessage(message);
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADING_LATEST_NEWS:
                    latest = (Latest) msg.obj;
                    date = latest.getDate();
                    kanner.setTop_storiesEntities(latest.getTop_stroies());
                    List<StoriesEntity> stories = latest.getStories();
                    StoriesEntity storiesEntity = new StoriesEntity();
                    storiesEntity.setTitle("今日热闻");
                    storiesEntity.setType(Constant.TOPIC);
                    stories.add(0,storiesEntity);
                    mAdapter.addList(stories);
                    isLoading = false;
                    break;
                case LOADING_BEFORE_NEWS:
                    before = (Before) msg.obj;
                    date = before.getDate();
                    List<StoriesEntity> storiesEntities = before
                            .getStories();
                    StoriesEntity entity = new StoriesEntity();
                    entity.setTitle(covert(date));
                    entity.setType(Constant.TOPIC);
                    storiesEntities.add(0,entity);
                    mAdapter.addList(storiesEntities);
                    isLoading = false;
                    break;
            }
        }
    }

    private String covert(String date) {
        StringBuffer stringBuffer = new StringBuffer();
        //substring @param start the start offset.
        //* @param end the end+1 offset.
//        stringBuffer.append(date.substring(0,4)+"年");
//        stringBuilder.append(date.substring(4,6)+"月");
//        stringBuilder.append(date.substring(6,8)+"日");
        String result = date.substring(0,4);
        result += "年";
        result += date.substring(4,6);
        result += "月";
        result += date.substring(6,8);
        result += "日";
        return result;
    }

    public void updateTheme(){
        mAdapter.notifyDataSetChanged();
    }
}
