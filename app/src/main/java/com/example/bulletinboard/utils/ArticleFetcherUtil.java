package com.example.bulletinboard.utils;

import android.util.Log;


import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleFetcherUtil {
    final static private String TAG = "ArticleFetcherUtil";
    public String getUrlString(OkHttpClient client, String url, String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        String result= "";
        try  {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "get: 网络GET请求失败！提示信息："+e.getMessage()+"url:"+url);
        }
        return result;
    }
}
