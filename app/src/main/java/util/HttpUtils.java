package util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.net.ConnectivityManagerCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Created by rain on 2016/9/13.
 */
public class HttpUtils {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static void get(String url, Callback responseCallback) {

        Request request = new Request.Builder().url(getAbsoluteUrl(url))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(responseCallback);

    }

    public static void getImage(String url, Callback responseCallback) {

        Request request = new Request.Builder().url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(responseCallback);
    }


    private static String getAbsoluteUrl(String relativeUrl) {

        return Constant.BASEURL + relativeUrl;
    }

    /*
     检查网络
    */
    public static boolean isNetWorkConnected(Context mContext) {
        if (mContext != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) mContext.getSystemService(mContext
                            .CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            //进行一下判空
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isAvailable();
            }

        }
        return false;

    }
}
