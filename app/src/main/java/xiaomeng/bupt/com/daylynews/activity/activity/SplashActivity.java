package xiaomeng.bupt.com.daylynews.activity.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import java.io.File;

import xiaomeng.bupt.com.daylynews.R;

/**
 * Created by rain on 2016/9/13.
 */
public class SplashActivity extends Activity {
    private ImageView mStartImage;
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_layout);
        mStartImage = (ImageView) findViewById(R.id.id_start_image);
        initIamge();

    }

    private void initIamge() {
        //这个具体目录是哪里？
        //res//mimap-xh？
        File dir = getFilesDir();

        final File imgFile = new File (dir,"start.jpg");
        if (imgFile.exists()){
            mStartImage.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }else{
            mStartImage.setImageResource(R.mipmap.start);
            Log.d(TAG, "initIamge: --------");
        }

    }
}
