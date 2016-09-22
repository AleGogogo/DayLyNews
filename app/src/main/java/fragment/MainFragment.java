package fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import adapter.MainNewsItemAdapter;
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
    protected View initView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        ((MainActivity)mActivity).setToolBarTitle("今日热闻");
        View view = inflater.inflate(R.layout.main_news_layout,container,false);
        mListView = (ListView) view.findViewById(R.id.id_main_news_listview);
        View header = LayoutInflater.from(mActivity).inflate(R.layout.kanner,null);
        kanner = (Kanner) header.findViewById(R.id.id_kanner);
//        kanner.setOnItemClickListener(new Kanner.OnItemClickListener() {
//            @Override
//            public void click(View v, Latest.Top_stroies top_stroies) {
//
//            }
//        });
        mListView.addHeaderView(header);
        mAdapter = new MainNewsItemAdapter(mActivity);
        mListView.setAdapter(mAdapter);
        initListener();
        myHandler = new MyHandler();
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
//                boolean enable = (firstVisibleItem == 0)&&(view.getChildAt(
//                        firstVisibleItem).getTop() == 0);
                // TODO: 2016/9/21 不知道这一部是干什么用的
                if (firstVisibleItem +visibleItemCount == totalItemCount && !isLoading){
                    loaderMore(Constant.BEFORE+date);
                }

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
                    parseBeforeJson(result);
                }
            });
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
                        parseLatesJson(json);
                }
            });
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
                    kanner.setTop_stroiesEntities(latest.getTop_stroies());
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
}
