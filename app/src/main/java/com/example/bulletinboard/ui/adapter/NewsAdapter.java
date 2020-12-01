package com.example.bulletinboard.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bulletinboard.entity.News;
import com.example.bulletinboard.R;
import com.example.bulletinboard.ui.activity.LoginActivity;
import com.example.bulletinboard.ui.activity.TextActivity;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.newsViewHolder> {
    private List<News> newsList;
    private Context context;
    private static final int TYPE0 = 0;
    private static final int TYPE1 = 1;
    private static final int TYPE2 = 2;
    private static final int TYPE3 = 3;
    private static final int TYPE4 = 4;

    static class newsViewHolder extends RecyclerView.ViewHolder {
        View newsView;
        TextView title;
        TextView author;
        TextView publishTime;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        ImageView image4;

        public newsViewHolder(View view) {
            super(view);
            newsView = view;
            title = (TextView) view.findViewById(R.id.news_title);
            author = (TextView) view.findViewById(R.id.news_author);
            publishTime = (TextView) view.findViewById(R.id.news_publishTime);
            image1 = (ImageView) view.findViewById(R.id.news_image1);
            image2 = (ImageView) view.findViewById(R.id.news_image2);
            image3 = (ImageView) view.findViewById(R.id.news_image3);
            image4 = (ImageView) view.findViewById(R.id.news_image4);
        }
    }

    public NewsAdapter(List<News> newsList){
        this.newsList = newsList;
    }

    // 将ViewType设为News实体类中的type属性，实现不同layout的选择
    @Override
    public int getItemViewType(int position) {
        return newsList.get(position).getType();
    }

    @NonNull
    @Override
    public newsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type0, parent, false);
        switch (viewType){
            case TYPE0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type0, parent, false);
                break;
            case TYPE1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type1, parent, false);
                break;
            case TYPE2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type2, parent, false);
                break;
            case TYPE3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type3, parent, false);
                break;
            case TYPE4:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type4, parent, false);
                break;
            default:
        }
        final newsViewHolder holder = new newsViewHolder(view);
        holder.newsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                News news = newsList.get(position);
                // Toast.makeText(v.getContext(), "you clicked news: "+news.getTitle(), Toast.LENGTH_LONG).show();
                SharedPreferences sp = context.getSharedPreferences("loginToken", 0);
                Log.e("sharedPreferences", sp.getAll().toString());
                Intent intent;
                if (sp.getString("token", null) == null){
                    intent = new Intent(context, LoginActivity.class); // 没有缓存，跳转至登录页面
                }
                else{
                    intent = new Intent(context, TextActivity.class); // 有缓存，跳转至正文页面
                }
                intent.putExtra("id", news.getId());
                intent.putExtra("title", news.getTitle());
                intent.putExtra("author", news.getAuthor());
                intent.putExtra("publishTime", news.getPublishTime());
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull newsViewHolder holder, int position) {
        News news = newsList.get(position);
        Log.d("news", news.toString());
        holder.title.setText(news.getTitle());
        holder.author.setText(news.getAuthor());
        holder.publishTime.setText(news.getPublishTime());
        if (news.getImage() != ""){
            Glide.with(context).load(getImageId(news.getImage())).into(holder.image1); // 使用Glide对图片加载
            // holder.image1.setImageResource(getImageId(news.getImage()));
        }
        List<String> images = news.getImages();
        if (images.size() > 0){
            Glide.with(context).load(getImageId(images.get(0))).into(holder.image1);
            Glide.with(context).load(getImageId(images.get(1))).into(holder.image2);
            Glide.with(context).load(getImageId(images.get(2))).into(holder.image3);
            Glide.with(context).load(getImageId(images.get(3))).into(holder.image4);
            /*
            holder.image1.setImageResource(getImageId(images.get(0)));
            holder.image2.setImageResource(getImageId(images.get(1)));
            holder.image3.setImageResource(getImageId(images.get(2)));
            holder.image4.setImageResource(getImageId(images.get(3)));
            */
        }
    }

    @Override
    public int getItemCount(){
        return newsList.size();
    }

    // 从图片名获取图片的id
    public int getImageId(String imageName) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        int id = context.getResources().getIdentifier(imageName, "mipmap", appInfo.packageName);
        return id;
    }

}
