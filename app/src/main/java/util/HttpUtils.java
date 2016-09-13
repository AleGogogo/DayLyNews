package util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by rain on 2016/9/13.
 */
public class HttpUtils {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler){

           client.get(getAbsoluteUrl(url),requestParams,asyncHttpResponseHandler);

    }

    public static void post(String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler){

         client.post(getAbsoluteUrl(url),requestParams,asyncHttpResponseHandler);

    }


    private static String getAbsoluteUrl(String relativeUrl){

        return Constant.BASEURL+relativeUrl;
    }
}
