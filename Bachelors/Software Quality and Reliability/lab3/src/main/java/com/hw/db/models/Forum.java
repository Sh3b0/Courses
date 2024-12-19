package com.hw.db.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Forum {
    private Number posts;
    private String slug;
    private Number threads;
    private String title;
    private String user;

    @JsonCreator
    public Forum(
            @JsonProperty("posts") Number posts,
            @JsonProperty("slug") String slug,
            @JsonProperty("threads") Number threads,
            @JsonProperty("title") String title,
            @JsonProperty("user") String user
    ){
        this.posts=posts;
        this.slug=slug;
        this.threads=threads;
        this.title=title;
        this.user=user;

    }

    public Forum() {

    }

    public Number getPosts() {
        return posts;
    }

    public void setPosts(Number posts) {
        this.posts = posts;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Number getThreads() {
        return threads;
    }

    public void setThreads(Number threads) {
        this.threads = threads;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
