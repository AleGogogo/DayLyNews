package view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
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
    private static final int STATE_NOT_STARTED = 0;
    private static final int STATE_FILE_STARTED = 1;
    private static final int STATE_FINISHED = 2;
    private static final int FILL_TIME = 600;

    private Paint filPaint;
    private static final Interpolator  INTERPOLATOR = new AccelerateInterpolator();
     ObjectAnimator revealAnimator;
    private int currentRedius;
    private int state = STATE_NOT_STARTED;


    public RevealBackGroundView(Context context) {
        super(context);
        iniUI();
    }

    private void iniUI() {
        filPaint = new Paint();
        filPaint.setStyle(Paint.Style.FILL);
        filPaint.setColor(Color.WHITE);
    }

    public RevealBackGroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RevealBackGroundView(Context context, AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealBackGroundView(Context context, AttributeSet attrs, int
            defStyleAttr, int defStyleRes, Paint filPaint) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.filPaint = filPaint;
        iniUI();
    }
    public void startFromLocation(int[] tapLocationOnScreen){

    }
    private void setFilPaintColor(int color){
            filPaint.setColor(color);
    }

}
