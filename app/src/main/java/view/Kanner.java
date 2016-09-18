package view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import model.Latest;
import xiaomeng.bupt.com.daylynews.R;

/**
 * Created by LYW on 2016/9/17.
 */
public class Kanner extends FrameLayout {
    private List<View> views;
    private List<ImageView> iv_dots;
    private ViewPager vp;
    private Handler mHandler = new Handler();
    private List<Latest.Top_stroies> top_stroiesEntities;
    private Context mContext;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private boolean isAutoPaly;
    private int delayTime;

    public Kanner(Context context) {
        this(context, null);
    }

    public Kanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 在使用了自定义属性的时候要调用的个自定义方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public Kanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        //体会一下为什么在构造方法里new一个这个
        top_stroiesEntities = new ArrayList<>();
        iv_dots = new ArrayList<>();
        views = new ArrayList<>();
        delayTime = 2000;
    }

    //不知用意何在
    public void setTop_stroiesEntities(List<Latest.Top_stroies>
                                               top_stroiesEntities) {
        this.top_stroiesEntities = top_stroiesEntities;
        reset();
    }

    /**
     * 每次设置顶端新闻的时候都会调用这个方法
     */
    private void reset() {
        views.clear();
        initUI();
    }

    private void initUI() {
        View view = LayoutInflater.from(mContext).inflate(R.layout
                .kanner_layout, this, false);
        vp = (ViewPager) view.findViewById(R.id.id_viewpager);
        LinearLayout ll_dot = (LinearLayout) view.findViewById(R.id.id_ll_dot);

        int len = top_stroiesEntities.size();
        for (int i = 0; i < len; i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            ll_dot.addView(imageView, params);
            ll_dot.addView(imageView);
        }

        for (int i = 0; i < len + 1; i++) {
            View fm= LayoutInflater.from(mContext).inflate(R.layout
                    .kanner_content_layout
                    , null);
            ImageView imageView = (ImageView) fm.findViewById(R.id.img_kanner);
            TextView title = (TextView) fm.findViewById(R.id.id_tvt_kannercontent_title);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == 0){
               //显示图片，用imageloader
                mImageLoader.displayImage(top_stroiesEntities.get(len-1).getImage(),
                        imageView,options);
                title.setText(top_stroiesEntities.get(len-1).getTitle());
            }
            //在本来的最后一张图片后面，再添加一张和第一张一样的图片来充当一个缓冲
            else if (i == len+1){
                mImageLoader.displayImage(top_stroiesEntities.get(0).getImage(),
                        imageView,options
                );
                title.setText(top_stroiesEntities.get(0).getTitle());
            }
        }
    }

}
