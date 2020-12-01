package com.example.bulletinboard.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.bulletinboard.utils.DividerDecorationUtil;
import com.example.bulletinboard.entity.News;
import com.example.bulletinboard.ui.adapter.NewsAdapter;
import com.example.bulletinboard.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<News> newsList = new ArrayList<>();
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNews();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NewsAdapter adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerDecorationUtil(this, DividerDecorationUtil.VERTICAL_LIST));
    }

    // 初始化菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        // menuItem.expandActionView();
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQuery("123", false);
        searchView.clearFocus();
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                // Toast.makeText(MainActivity.this, query, Toast.LENGTH_LONG).show();
                intent.putExtra("query", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        sp = getSharedPreferences("loginToken", 0);
        MenuItem login = menu.findItem(R.id.login);
        MenuItem logout = menu.findItem(R.id.logout);
        if (sp.getString("token", null) == null){
            logout.setVisible(false);
        }
        else{
            login.setVisible(false);
        }
        return true;
    }

    // 刷新菜单
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear(); // 清除菜单重新渲染
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        //menuItem.expandActionView();
        SearchView searchView = (SearchView) menuItem.getActionView(); // 获取搜索对象
        searchView.onActionViewExpanded();
        searchView.setQuery("123", false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class); // 跳转至搜索结果页面
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_LONG).show();
                intent.putExtra("query", query); // 传递搜索关键字
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        sp = getSharedPreferences("loginToken", 0);
        MenuItem login = menu.findItem(R.id.login);
        MenuItem logout = menu.findItem(R.id.logout);
        if (sp.getString("token", null) == null){
            logout.setVisible(false);
        }
        else{
            login.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.login: // 登录，跳转至登录页面
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.logout: // 登出，清除缓存
                sp.edit().clear().commit();
                Toast.makeText(this, "已登出", Toast.LENGTH_LONG).show();
                break;
            default:
        }
        return true;
    }

    private void initNews(){
        try {
            AssetManager assetManager = getAssets(); // assets中的文件无法直接访问，需要使用AssetManager访问
            InputStreamReader reader = new InputStreamReader(assetManager.open("metadata.json"), "UTF-8");
            BufferedReader br = new BufferedReader((reader));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            reader.close();
            String jsonStr = sb.toString();
            JSONArray jsonArray = new JSONArray(jsonStr); // 将形如[{}, {}, {}]的json字符串解析为json数组，
            // JSONObject jsonObject = new JSONObject(jsonStr); // 形如{}的json字符串使用JSONObject类解析
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);
                String id = json.getString("id");
                String title = json.getString("title");
                String author = json.getString("author");
                String publishTime = json.getString("publishTime");
                int type = json.getInt("type");
                String image = "";
                if (json.has("cover")){
                    image = json.getString("cover");
                    image = image.substring(0, image.lastIndexOf("."));
                }
                List<String> images = new ArrayList<>();
                if (json.has("covers")){
                    JSONArray covers = json.getJSONArray("covers"); // 使用JSONArray解析列表数据
                    for (int j = 0; j < covers.length(); j++){
                        String s = covers.getString(j);
                        images.add(s.substring(0, s.lastIndexOf(".")));
                    }
                }
                News news = new News(id, title, author, publishTime, type, image, images);
                newsList.add(news);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}