package adapter;

import android.content.Context;
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

import java.util.List;

import model.StoriesEntity;
import xiaomeng.bupt.com.daylynews.R;
import xiaomeng.bupt.com.daylynews.activity.activity.MainActivity;

/**
 * Created by LYW on 2016/9/21.
 */
public class NewsItemAdapter extends BaseAdapter{
    private Context mContext;
    private List<StoriesEntity> entities;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private boolean isLight ;

    public NewsItemAdapter(Context context, List<StoriesEntity> entities){
        mContext = context;
        this.entities = entities;
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        isLight =((MainActivity)mContext).isLight();

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
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder= new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.news_item,
                    parent,false);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.id_newsitem_tv_title);
            viewHolder.iv_image = (ImageView)convertView.findViewById(R.id.id_news_item_iv_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置夜间模式
        viewHolder.tv_title.setTextColor(mContext.getResources().getColor(isLight ? android.R.color.black : android.R.color.white));

        ((LinearLayout) viewHolder.iv_image.getParent().getParent().getParent()).setBackgroundColor(
                       mContext.getResources().getColor(isLight ? R.color.light_news_item:
                       R.color.dark_news_item)
        );

        ((FrameLayout) viewHolder.tv_title.getParent().getParent()).setBackgroundResource(
                isLight ? R.drawable.item_seleor_background_light:
                        R.drawable.item_seleor_background_light
                 );

          StoriesEntity stroiesEntity = entities.get(position);
        viewHolder.tv_title.setText(entities.get(position).getTitle());
          if (stroiesEntity.getImages()!=null){
              viewHolder.iv_image.setVisibility(View.VISIBLE);
              mImageLoader.displayImage(stroiesEntity.getImages().get(0),
                      viewHolder.iv_image,options);
          }else {
              viewHolder.iv_image.setVisibility(View.GONE);

          }

        return convertView;
    }

    private class ViewHolder {
        TextView tv_title;
        ImageView iv_image;

    }
}
