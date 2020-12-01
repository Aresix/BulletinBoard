package com.example.bulletinboard.entity;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class News {
    private String id;
    private String title;
    private String author;
    private String publishTime;
    private int type;
    private String image; // 无图或单图
    private List<String> images; // 多图

    public News(String id, String title, String author, String publishTime, int type, String image, List<String> images){
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishTime = publishTime;
        this.type = type;
        this.image = image;
        this.images = images;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getImages(){
        return this.images;
    }

    @Override
    public String toString() {
        return "News{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", type=" + type +
                ", image='" + image + '\'' +
                ", images=" + images +
                '}';
    }
}
