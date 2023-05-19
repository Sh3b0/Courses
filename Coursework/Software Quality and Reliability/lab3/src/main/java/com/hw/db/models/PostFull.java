package com.hw.db.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostFull {
    private com.hw.db.models.User User;
    private com.hw.db.models.Forum Forum;
    private com.hw.db.models.Post Post;
    private com.hw.db.models.Thread Thread;
    @JsonCreator
    PostFull(
            @JsonProperty("user") com.hw.db.models.User User,
            @JsonProperty("forum") com.hw.db.models.Forum Forum,
            @JsonProperty("post") com.hw.db.models.Post Post,
            @JsonProperty("thread") com.hw.db.models.Thread Thread
    ){
        this.User=User;
        this.Forum=Forum;
        this.Post=Post;
        this.Thread=Thread;
    }

    public com.hw.db.models.User getUser() {
        return User;
    }

    public void setUser(com.hw.db.models.User user) {
        User = user;
    }

    public com.hw.db.models.Forum getForum() {
        return Forum;
    }

    public void setForum(com.hw.db.models.Forum forum) {
        Forum = forum;
    }

    public com.hw.db.models.Post getPost() {
        return Post;
    }

    public void setPost(com.hw.db.models.Post post) {
        Post = post;
    }

    public com.hw.db.models.Thread getThread() {
        return Thread;
    }

    public void setThread(com.hw.db.models.Thread thread) {
        Thread = thread;
    }
}
