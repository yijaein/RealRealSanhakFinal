package com.google.firebase.quickstart.database.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties

/*
    2017_09_28 이재인 좌표값 위도 경도 추가
    2017_09_30 이재인 이미지 저장경로 추가

 */
public class Post {

    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;
    public String lon;
    public String lat;
    public Uri photoUri;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body, String lon, String lat ,Uri photoUri) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.lon=lon;
        this.lat= lat;
        this.photoUri = photoUri;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("lon",lon);
        result.put("lat",lat);
        result.put("photoUri",photoUri);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
