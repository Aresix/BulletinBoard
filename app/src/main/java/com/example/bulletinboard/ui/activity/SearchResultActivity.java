package com.example.bulletinboard.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.bulletinboard.R;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        String query = getIntent().getStringExtra("query");
        TextView queryText = (TextView) findViewById(R.id.query);
        queryText.setText(query); // 显示搜索的关键字
    }
}