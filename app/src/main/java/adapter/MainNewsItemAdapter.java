package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


import model.StoriesEntity;
import util.Constant;
import xiaomeng.bupt.com.daylynews.R;
import xiaomeng.bupt.com.daylynews.activity.activity.MainActivity;

/**
 * Created by LYW on 2016/9/20.
 */
public class MainNewsItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<StoriesEntity> entities;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private boolean isLight;

    public MainNewsItemAdapter(Context mContext) {
        this.mContext = mContext;
        //因为如果传进来entities的话后续就不可以动态添加数据了
        entities = new ArrayList<>();
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        isLight = ((MainActivity) mContext ).isLight();
    }

    public void addList(List<StoriesEntity> items) {
        this.entities.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StoriesEntity story = entities.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout
                            .main_news_item
                    , parent, false);
            holder.tv_topic = (TextView) convertView.findViewById(R.id
                    .id_newsitem_topic);
            holder.tv_title = (TextView) convertView.findViewById(R.id
                    .id_main_newsitem_tv_title);
            holder.iv_title = (ImageView) convertView.findViewById(R.id
                    .id_main_newsitem_iv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_title.setTextColor(mContext.getResources().getColor(isLight ? android.R.color.black : android.R.color.white));
        ((LinearLayout) holder.iv_title.getParent().getParent().getParent()).setBackgroundColor(mContext.getResources().
                getColor(isLight ? R.color.light_news_item : R.color.dark_news_item));
        holder.tv_topic.setTextColor(mContext.getResources().
                getColor(isLight ? R.color.light_news_topic : R.color.dark_news_topic));
        if (story.getType() == Constant.TOPIC) {
            //View.GONE 不可见不占据布局
            ((FrameLayout)holder.tv_topic.getParent()).setBackgroundColor(Color.TRANSPARENT);
            holder.tv_title.setVisibility(View.GONE);
            holder.iv_title.setVisibility(View.GONE);
            holder.tv_topic.setVisibility(View.VISIBLE);
            holder.tv_topic.setText(story.getTitle());
        } else {
            if (holder.tv_topic != null) {
                holder.tv_topic.setVisibility(View.GONE);
            }
            ((FrameLayout) holder.tv_topic.getParent()).setBackgroundResource(isLight ? R.drawable.item_seleor_background_light :
                    R.drawable.item_background_selector_dark);
            holder.tv_title.setVisibility(View.VISIBLE);
            holder.iv_title.setVisibility(View.VISIBLE);
            holder.tv_title.setText(story.getTitle());
            mImageLoader.displayImage(story.getImages().get(0),
                    holder.iv_title, options);
        }

        return convertView;
    }

    class ViewHolder {
        TextView tv_topic;
        TextView tv_title;
        ImageView iv_title;
    }
}
