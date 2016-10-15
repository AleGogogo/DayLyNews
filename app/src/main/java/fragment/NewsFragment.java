package fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import adapter.NewsItemAdapter;
import db.CacheDBHelper;
import model.News;
import model.StoriesEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Constant;
import util.HttpUtils;
import xiaomeng.bupt.com.daylynews.R;
import xiaomeng.bupt.com.daylynews.activity.activity.MainActivity;
import xiaomeng.bupt.com.daylynews.activity.activity.NewsContentActivity;

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
    private List<StoriesEntity> stories;
    private CacheDBHelper dbHelper;
    private static final int LOADING_THEME_NANMES = 100;
    private static final String TAG = "NewsFragment";

    public NewsFragment(String id, String title){
        urlId = id;
        this.title = title;
    }
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        ((MainActivity) mActivity).setToolBarTitle(title);
        dbHelper = ((MainActivity)mActivity).getDBHelper();
        View view = inflater.inflate(R.layout.news_layout,container,false);
        mImageLoader = ImageLoader.getInstance();
        newsListView = (ListView) view.findViewById(R.id.id_news_listview);
        View header = LayoutInflater.from(mActivity).inflate(R.layout.news_header,
                newsListView,false);
        title_tv = (TextView) header.findViewById(R.id.id_news_title_tv);
        title_img = (ImageView)header.findViewById(R.id.id_news_ivg);
        newsListView.addHeaderView(header);
        initListener();
        return view;
    }

    private void initListener() {

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                startingLocation[0] += view.getWidth() / 2;
                StoriesEntity entity = (StoriesEntity) parent.getAdapter().getItem(position);
                Intent intent = new Intent(mActivity, NewsContentActivity.class);
                intent.putExtra(Constant.START_LOCATION, startingLocation);
                intent.putExtra("entity", entity);
                intent.putExtra("isLight", ((MainActivity) mActivity).isLight());
                startActivity(intent);
                Log.d(TAG, "onItemClick: 点击成功！");
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if (HttpUtils.isNetWorkConnected(mActivity)){

            HttpUtils.get(Constant.THEMENEWS + urlId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String reslut = response.body().string();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("replace into CacheList(date,json) values(" + (Constant.BASE_COLUMN + Integer.parseInt(urlId)) + ",' "
                            + reslut + "')");
                    db.close();
                    parseNewsJson(reslut);
                }
            });
        }else {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //不明白Constant.BASE_COLUMN 这个存在的意义
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + (Constant.BASE_COLUMN + Integer.parseInt(urlId)), null);
            if (cursor.moveToFirst()){
                String result = cursor.getString(cursor.getColumnIndex("json"));
                parseNewsJson(result);
            }
            cursor.close();
            db.close();
        }
    }

    private void parseNewsJson(String reslut) {
        Gson gson = new Gson();
        news = gson.fromJson(reslut,News.class);
     //   Log.d(TAG, "parseNewsJson:  news is "+news.toString());
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
         stories = news.getStories();
        for (int i=0;i<stories.size();i++){
            Log.d(TAG, "parseNewsJson: --=---"+stories.get(i).toString());
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageLoader.displayImage(news.getImage(),title_img,options);
                title_tv.setText(news.getDescription());
                mAdapter = new NewsItemAdapter(mActivity,news.getStories());
                newsListView.setAdapter(mAdapter);
            }
        });



    }

        public void updateTheme(){
            mAdapter.notifyDataSetChanged();
        }

}
