package view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by LYW on 2016/9/23.
 */
public class RevealBackGroundView extends View {
    //我们想要掌控动画的状态（未开始，开始了，结束）
    //Reveal效果必须是圆形的
    //Reveal动画必须从点击的那个点开始
    //我们还要能主动结束动画
    public static final int STATE_NOT_STARTED = 0;
    public  static final int STATE_FILL_STARTED = 1;
    public  static final int STATE_FINISHED = 2;
    private static final int FILL_TIME = 600;
    private int startLocationX ;
    private int startLocationY ;
    private OnStateChangeListener onStateChangeListener;

    private Paint fillPaint;
    private static final Interpolator  INTERPOLATOR = new AccelerateInterpolator();
    //android 3.0之后添加的一些动画
    ObjectAnimator revealAnimator;
    private int currentRedius;
    private int state = STATE_NOT_STARTED;


    public RevealBackGroundView(Context context) {
        super(context);
        iniUI();
    }

    private void iniUI() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
    }

    public RevealBackGroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniUI();
    }

    public RevealBackGroundView(Context context, AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniUI();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealBackGroundView(Context context, AttributeSet attrs, int
            defStyleAttr, int defStyleRes, Paint fillPaint) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.fillPaint = fillPaint;
        iniUI();
    }

    /**
     * Reveal动画必须从点击的那个点开始
     * @param tapLocationOnScreen 点击的坐标
     */
    public void startFromLocation(int[] tapLocationOnScreen){
        changeState(STATE_FILL_STARTED);
        startLocationX = tapLocationOnScreen[0];
        startLocationY = tapLocationOnScreen[1];
        //translate 应该是一个渐变的效果
        revealAnimator = ObjectAnimator.ofInt(this,"currentRadius",getWidth()+getHeight())
                .setDuration(FILL_TIME);
        revealAnimator.setInterpolator(INTERPOLATOR);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_FINISHED);
            }
        });
        revealAnimator.start();

    }

    public void setFinishedFrame(){
        changeState(STATE_FINISHED);
        invalidate();
    }
    /**
     * 这个方法在调用了invalidate()方法后调用
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (state == STATE_FINISHED){
            canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
        }else {
            canvas.drawCircle(startLocationX,startLocationY,currentRedius,fillPaint);
        }
    }

    private void changeState(int state) {
        //判断此刻状态和传入的状态一样？
        if (this.state == state){
            return;
        }
        this.state = state;
        if (onStateChangeListener != null){
            onStateChangeListener.onStateChange(state);
        }
    }
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener){
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setFilPaintColor(int color){
            fillPaint.setColor(color);
    }

    public void setCurrentRedius(int redius){
        this.currentRedius = redius;
        invalidate();
    }

    /**
     * Created by rain on 2016/9/26.
     */
    public static interface OnStateChangeListener {
        void onStateChange(int state);
    }
}
