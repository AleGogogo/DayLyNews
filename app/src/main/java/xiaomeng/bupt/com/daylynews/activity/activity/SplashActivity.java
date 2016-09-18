package xiaomeng.bupt.com.daylynews.activity.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Constant;
import util.HttpUtils;
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

        final File imgFile = new File(dir, "start.jpg");
        if (imgFile.exists()) {
            mStartImage.setImageBitmap(BitmapFactory.decodeFile(imgFile
                    .getAbsolutePath()));
        } else {
            mStartImage.setImageResource(R.mipmap.start);
            Log.d(TAG, "initIamge: --------");
        }

        final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f,
                1.0f
                //Animation.RELATIVE_TO_SELF 相对于自身
                , 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation
                .RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(3000);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (HttpUtils.isNetWorkConnected(SplashActivity.this)) {
                    HttpUtils.get(Constant.START_IMAGE, new
                            Callback() {
                                @Override
                                public void onFailure(Call call, IOException
                                        e) {
                                    Log.d(TAG, "第一步请求失败");
                                    startActivity();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        String result = response.body().string();
                                        Log.d(TAG, "the result is " +result);
                                        JSONObject jsonObject = new JSONObject
                                                (result);
                                        String url = jsonObject.getString
                                                ("img");
                                        Log.d(TAG, "获取的Url 为："+url);
                                        HttpUtils.get(url, new Callback() {
                                            @Override
                                            public void onFailure(Call call,
                                                                  IOException
                                                                          e) {
                                                    startActivity();
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                byte[] binaryData = response.body().bytes();
                                                saveIamge(imgFile,binaryData);
                                                Log.d(TAG, "onSuccess: 成功获取启动图片！");
                                                startActivity();
                                            }
                                        });
                                } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }});
                }else {
                    Toast.makeText(SplashActivity.this,"网络链接失败",Toast.LENGTH_SHORT).show();
                    startActivity();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
         mStartImage.startAnimation(scaleAnimation);
    }

    private void startActivity() {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        Log.d(TAG, "startActivity: 进入MainActivity");
    }
     /*
     这个方法看不懂啊，有啥用
     */

    private void saveIamge(File file, byte[] bytes) {
        try {
            //为啥要删除文件
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
