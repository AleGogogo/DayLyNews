package fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.IOException;
import java.util.List;

import adapter.NewsItemAdapter;
import model.News;

import model.StoriesEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Constant;
import util.HttpUtils;
import xiaomeng.bupt.com.daylynews.R;

/**
 * Created by LYW on 2016/9/21.
 */
public class NewsFragment extends BaseFragment {

    private ListView newsListView;
    private String urlId;
    private String title;
    private NewsItemAdapter mAdapter;
    private News news;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private TextView title_tv;
    private ImageView title_img;
    private MyHandler myHandler;
    private List<StoriesEntity> stories;
    private static final int LOADING_THEME_NANMES = 100;

    public NewsFragment(String id, String title){
        urlId = id;
        this.title = title;
    }
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_layout,container,false);
        mImageLoader = ImageLoader.getInstance();
        newsListView = (ListView) view.findViewById(R.id.id_news_listview);
        View header = LayoutInflater.from(mActivity).inflate(R.layout.news_header,
                newsListView,false);
        title_tv = (TextView) header.findViewById(R.id.id_news_title_tv);
        title_img = (ImageView)header.findViewById(R.id.id_news_ivg);
        newsListView.addHeaderView(header);

        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        myHandler = new MyHandler();
        if (HttpUtils.isNetWorkConnected(mActivity)){

            HttpUtils.get(Constant.THEMENEWS + urlId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String reslut = response.body().string();
                    parseNewsJson(reslut);
                }
            });
        }
    }

    private void parseNewsJson(String reslut) {
        Gson gson = new Gson();
        news = gson.fromJson(reslut,News.class);
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
         stories = news.getStories();
        mImageLoader.displayImage(news.getImage(),title_img,options);
        Message message = myHandler.obtainMessage();
        message.obj = stories;
        message.what = LOADING_THEME_NANMES;
        myHandler.sendMessage(message);

    }

    private class MyHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADING_THEME_NANMES:
                    stories = (List<StoriesEntity>) msg.obj;
                    mAdapter = new NewsItemAdapter(mActivity,stories);
                    newsListView.setAdapter(mAdapter);
                    break;
            }
        }
    }
}
