package view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
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
public class Kanner extends FrameLayout implements View.OnClickListener{
    private List<View> views;
    private List<ImageView> iv_dots;
    private ViewPager vp;
    private Handler mHandler = new Handler();
    private List<Latest.Top_stories> top_storiesEntities;
    private Context mContext;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private boolean isAutoPaly;
    private int delayTime;
    private int currentIntem;
    private OnItemClickListener mItemClickListener;
    private static final String TAG = "Kanner";

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

        top_storiesEntities = new ArrayList<>();
        iv_dots = new ArrayList<>();
        views = new ArrayList<>();
        delayTime = 2000;

    }

    //不知用意何在
    public void setTop_storiesEntities(List<Latest.Top_stories>
                                               top_storiesEntities) {
        this.top_storiesEntities = top_storiesEntities;
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
        //如果调用inflate方法，传入了ViewGroup root参数，则会从root中得到由layout_width和
        // layout_height组成的LayoutParams，在attachToRoot设置为false的话，就会对我们加载的视图View
        // 设置该LayoutParams。
        View view = LayoutInflater.from(mContext).inflate(R.layout
                .kanner_layout, this, true);
        vp = (ViewPager) view.findViewById(R.id.id_viewpager);
        LinearLayout ll_dot = (LinearLayout) view.findViewById(R.id.id_ll_dot);
        ll_dot.removeAllViews();

        int len = top_storiesEntities.size();
        Log.d(TAG, "initUI: kanner's len is"+len);
        for (int i = 0; i < len; i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            ll_dot.addView(imageView, params);
            iv_dots.add(imageView);
        }

        for (int i = 0; i <len + 1; i++) {
            View fm= LayoutInflater.from(mContext).inflate(R.layout
                    .kanner_content_layout
                    , null);
            ImageView imageView = (ImageView) fm.findViewById(R.id.img_kanner);
            TextView title = (TextView) fm.findViewById(R.id.id_tvt_kannercontent_title);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == 0){
               //显示图片，用imageloader
                mImageLoader.displayImage(top_storiesEntities.get(len-1).getImage(),
                        imageView,options);
                title.setText(top_storiesEntities.get(len-1).getTitle());
            }
            //在本来的最后一张图片后面，再添加一张和第一张一样的图片来充当一个缓冲
            else {
                mImageLoader.displayImage(top_storiesEntities.get(i-1).getImage(),
                        imageView,options);
                title.setText(top_storiesEntities.get(i-1).getTitle());
            }
            fm.setOnClickListener(this);
            views.add(fm);
        }
            vp.setAdapter(new MyPagerAdapter());
        //Set whether this view can receive the focus.
            vp.setFocusable(true);
        //设置初始页面
            vp.setCurrentItem(1);
            currentIntem = 1;
            vp.addOnPageChangeListener(new MyOnPagerListener());
            startPlay();

    }

    private void startPlay(){
       isAutoPaly = true;
       mHandler.postDelayed(task,delayTime);
    }
    //为什么不把它定义在startplay的内部
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPaly) {
                currentIntem = (currentIntem % (top_storiesEntities.size() + 1)) + 1;
                //True to smoothly scroll to the new item, false to transition immediately
                //false 代表立刻话过去
                if (currentIntem == 1) {
                    vp.setCurrentItem(currentIntem, false);
                    mHandler.post(task);
                }else {
                    vp.setCurrentItem(currentIntem);
                    mHandler.postDelayed(task,5000);
                }
            }else {
                mHandler.postDelayed(task,5000);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (mItemClickListener !=null){
            Latest.Top_stories entity= top_storiesEntities.get(
                    vp.getCurrentItem()-1);
            mItemClickListener.click(v,entity);
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object
                object) {
            container.removeView((View) object);
        }
    }

    private class MyOnPagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int
                positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected: position["+position+"]");
           for (int i =0;i<iv_dots.size();i++){
               if (i == position-1){
                   iv_dots.get(position).setImageResource(R.drawable.dot_focus);
               }else {
                   iv_dots.get(position).setImageResource(R.drawable.dot_blur);
               }
           }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
              switch (state){
                  case 1:
                      isAutoPaly = false;
                      break;
                  case 2:
                      isAutoPaly = true;
                      break;
                  case 0:
                      if (vp.getCurrentItem()== 0){
                          vp.setCurrentItem(top_storiesEntities.size(),false);
                      }else if (vp.getCurrentItem()== top_storiesEntities.size()+1){
                          vp.setCurrentItem(1,false);
                      }
                      currentIntem = vp.getCurrentItem();
                      isAutoPaly = true;
                      break;
              }
        }

    }

    public interface OnItemClickListener{
        void click(View v, Latest.Top_stories top_stories);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mItemClickListener = listener;
    }
}
