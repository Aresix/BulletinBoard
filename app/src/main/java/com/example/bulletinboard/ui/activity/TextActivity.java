package com.example.bulletinboard.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bulletinboard.utils.ArticleFetcherUtil;
import com.example.bulletinboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.file.FileSchemeHandler;
import io.noties.markwon.linkify.LinkifyPlugin;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class TextActivity extends AppCompatActivity {

    private TextView mTitle, mAuthor, mPublishTime, mContent;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String token;
    private String id;
    private String url;
    private String title;
    private String author;
    private String publishTime;
    private Handler mHandler;
    private String content;
    private SharedPreferences sp;
    private Markwon markwon;
    private static int contentStyle = 0; // 0：pure_content 1：rich_content
    private int curTextSize = 18;
    private int delat = 2;
    private static final String TAG = "aresix";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        author = intent.getStringExtra("author");
        publishTime = intent.getStringExtra("publishTime");
        mHandler = new Handler();
        mTitle = findViewById(R.id.articleTitle);
        mAuthor = findViewById(R.id.articleAuthor);
        mPublishTime = findViewById(R.id.articlePublishTime);
        mContent = findViewById(R.id.articleContent);
        mContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (contentStyle == 0){
            getArticleData();
        }
        else if (contentStyle == 1){
            getRichArticleData();
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                        mTitle.setText(title);
                        mAuthor.setText(author);
                        mPublishTime.setText(publishTime);
                        mContent.setText((String) msg.obj);
                }
                else if (msg.what == 1) {
                    mTitle.setText(title);
                    mAuthor.setText(author);
                    mPublishTime.setText(publishTime);
                    markwon.setMarkdown(mContent, (String) msg.obj);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.text, menu);
        return true;
    }

    // 顶部按钮设计点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        try {
            switch (item.getItemId()) {
                case R.id.enlarge:
                    if (curTextSize >= 26)
                        Toast.makeText(getApplicationContext(), "不能再大啦！", Toast.LENGTH_LONG).show();
                    else {
                        curTextSize += delat;
                        mContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, curTextSize);
                    }
                    break;
                case R.id.shorten:
                    if (curTextSize <= 10)
                        Toast.makeText(getApplicationContext(), "不能再小啦！", Toast.LENGTH_LONG).show();
                    else {
                        curTextSize -= delat;
                        mContent.setTextSize(curTextSize);
                    }
                    break;
                case R.id.pure:
                    if (contentStyle == 1) {
                        getArticleData();
                        contentStyle = 0;
                    }
                    else if (contentStyle == 0){
                        getRichArticleData();
                        contentStyle = 1;
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "onOptionsItemSelected: ", e);
        }
        return true;
    }

    private void getArticleData(){
        sp = getSharedPreferences("loginToken", 0);
        token = sp.getString("token", null);
        url = "https://vcapi.lvdaqian.cn/article/" + id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result;
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    result = new ArticleFetcherUtil().getUrlString(okHttpClient, url, token);
                    Log.e(TAG, result);
                    if (!result.isEmpty()) {
                        content = Json2String(result);
                        Message message = mHandler.obtainMessage(0);
                        message.obj = content;
                        mHandler.sendMessage(message);
                    }
                } catch (IOException ioe) {
                    Log.e(TAG, "onCreate: ", ioe);
                }
            }
        }).start();
    }

    private void getRichArticleData() {
        sp = getSharedPreferences("loginToken", 0);
        token = sp.getString("token", null);
        url = "https://vcapi.lvdaqian.cn/article/" + id  + "?markdown=1";
        String[] content = {""};
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result;
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    result = new ArticleFetcherUtil().getUrlString(okHttpClient, url, token);
                    Log.d(TAG, "onCreate: " + result + token + " " + url);
                    if (!result.isEmpty())
                        content[0] = Json2String(result);
                    if (content[0] != null) {
                        String s = showRichText(content[0]);
                        Message message = mHandler.obtainMessage(1);
                        message.obj = s;
                        mHandler.sendMessage(message);
                    }
                    Log.d(TAG, "Helios: " + content[0]);
                } catch (IOException ioe) {
                    Log.e(TAG, "onCreate: ", ioe);
                }
            }
        }).start();
    }

    private String Json2String(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String data = object.getString("data");
            Log.d(TAG, "Json2String: " + data);
            if (!data.isEmpty()) return data.substring(13);
        } catch (JSONException je) {
            Log.e(TAG, "run: failed to parse JSON", je);
        }
        return null;
    }

    @SuppressLint("HandlerLeak")
    private String showRichText(String s) {
        final String[] content = {""};
        content[0] = s;
        String cnt1 = content[0].replace("](", "](file:///android_asset/img/");
        cnt1 = cnt1.replace(") #", ") \n#");
        cnt1 = cnt1.replace("   ![]", "![]");
        // cnt1 是处理完后的Markdown代码 -> 为了正确显示图片
        Log.d(TAG, "handleMessage: (*&^%$#@!@#$%^&*&^%$#@!\n" + cnt1);
        return showMarkDown(cnt1);
    }

    private String showMarkDown(String content) {
        markwon = Markwon.builder(getApplicationContext())
                .usePlugin(ImagesPlugin.create())
                .usePlugin(LinkifyPlugin.create(Linkify.PHONE_NUMBERS | Linkify.WEB_URLS))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(ImagesPlugin.class, new Action<ImagesPlugin>() {
                            @Override
                            public void apply(@NonNull ImagesPlugin imagesPlugin) {
                                imagesPlugin.addSchemeHandler(FileSchemeHandler.createWithAssets(getApplicationContext()));
                            }
                        });
                    }
                })
                .build();
        Log.d(TAG, "run: markdown error??\n" + content);
        return content;
        /*
        try {
            markwon.setMarkdown(mContent, content);
        } catch (Exception e) {
            Log.d(TAG, "Ares: " + e.toString());
        }
        Log.d(TAG, "run: markdown error222??");

         */
    }
}