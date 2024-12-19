package com.hw.db.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Status {
    private Integer User;
    private Integer Forum;
    private Integer Post;
    private Integer Thread;
    @JsonCreator
    public Status(
            @JsonProperty("user") Integer user,
            @JsonProperty("forum") Integer Forum,
            @JsonProperty("post") Integer Post,
            @JsonProperty("thread") Integer Thread
    ){
        this.User=user;
        this.Forum=Forum;
        this.Post=Post;
        this.Thread=Thread;
    }

    public Status() {

    }

    public Integer getUser() {
        return User;
    }

    public void setUser(Integer user) {
        User = user;
    }

    public Integer getForum() {
        return Forum;
    }

    public void setForum(Integer forum) {
        Forum = forum;
    }

    public Integer getPost() {
        return Post;
    }

    public void setPost(Integer post) {
        Post = post;
    }

    public Integer getThread() {
        return Thread;
    }

    public void setThread(Integer thread) {
        Thread = thread;
    }
}
