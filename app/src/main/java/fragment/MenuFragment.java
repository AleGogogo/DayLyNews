package fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import model.NewsListItem;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Constant;
import util.HttpUtils;
import xiaomeng.bupt.com.daylynews.R;

/**
 * Created by LYW on 2016/9/16.
 */
public class MenuFragment extends BaseFragment {
    private LinearLayout li_menu;
    private TextView tv_login,tv_download,tv_main,tv_backup;
    private ListView mListView;
    private static final int SHOW_LIST = 20;
    private responseHandler mHandler = new responseHandler();
    private ArrayList<NewsListItem> items;
    private NewsTypeAdapter mAdapter;
    private static final String TAG = "MenuFragment";

    class responseHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_LIST:
                    mAdapter = new NewsTypeAdapter();
                    mListView.setAdapter(mAdapter);
                    break;
                default:

            }
        }
    }



    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.menu_layout,container,false);
            li_menu = (LinearLayout)view.findViewById(R.id.id_menu_layout);
            tv_login = (TextView)view.findViewById(R.id.id_menu_login);
            tv_backup = (TextView)view.findViewById(R.id.id_menu_myCollection_tv);
            tv_download = (TextView)view.findViewById(R.id.id_menu_offlinedownload_tv);
            tv_main = (TextView)view.findViewById(R.id.id_menu_main_tv);
            mListView = (ListView)view.findViewById(R.id.id_menu_listview);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int
                        position, long id) {
                    //和mainFragment很相似
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_from_right,R.anim.slide_out_to_left)
                            .replace(R.id.id_fl,new NewsFragment(items.get(position).getId(),
                                    items.get(position).getName()),"news")
                            .commit();
                }
            });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    protected void initData() {
        super.initData();
         items = new ArrayList<>();
         if (HttpUtils.isNetWorkConnected(getActivity())){
             HttpUtils.get(Constant.THEME_NEWSiTEMS, new Callback() {
                 @Override
                 public void onFailure(Call call, IOException e) {
                     Log.d(TAG, "onFailure: 新闻列表获取失败");
                 }

                 @Override
                 public void onResponse(Call call, Response response) throws IOException {
                     String json = response.body().string();
                     Log.d(TAG, "onResponse: json is "+json);
                     try {
                         JSONObject jsonObject = new JSONObject(json);
                         parseJson(jsonObject);
                         Log.d(TAG, "向后台获取数据成功！"+response);
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }

                 }
             });
         }else {
             Toast.makeText(getActivity(),"网络链接错误",Toast.LENGTH_SHORT).show();
         }

    }

    /**
     *响应实例
     * {
     "limit": 1000,
     "subscribed": [ ],
     "others": [
     {
     "color": 8307764,
     "thumbnail": "http://pic4.zhimg.com/2c38a96e84b5cc8331a901920a87ea71.jpg",
     "description": "内容由知乎用户推荐，海纳主题百万，趣味上天入地",
     "id": 12,
     "name": "用户推荐日报"
     },
     ...
     ]
     * @param
     */
    private void parseJson(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("others");
            for (int i =0;i<jsonArray.length();i++){
                NewsListItem item = new NewsListItem();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String id = jsonObject.getString("id");
                item.setName(name);
                item.setId(id);
                items.add(item);
                Log.d(TAG, "添加了"+ id +" 个新闻列表");
            }
            Message message = mHandler.obtainMessage();
            message.what = SHOW_LIST;
            message.obj = items;
            mHandler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    class NewsTypeAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NewsListItem item = items.get(position);
            if (convertView == null){
                convertView =LayoutInflater.from(getActivity()).inflate(R.layout.menu_item_layout,
                        parent,false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.id_menu_newsitem);
            textView.setText(item.getName());
            return convertView;
        }
    }
}
