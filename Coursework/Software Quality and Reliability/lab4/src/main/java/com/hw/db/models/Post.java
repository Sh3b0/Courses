package com.hw.db.models;


import java.sql.Array;
import java.time.ZoneId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;


public class Post {

    private String author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp created;
    private String forum;
    private Integer id;
    private boolean isEdited;
    private String message;
    private  Integer parent;
    private  Integer thread;
    private Object[] branch;
    @JsonCreator
    public Post(
            @JsonProperty("author") String author,
            @JsonProperty("created") Timestamp created,
            @JsonProperty("forum") String forum,
            @JsonProperty("message") String message,
            @JsonProperty("parent") Integer parent,
            @JsonProperty("thread") Integer thread,
            @JsonProperty("isEdited") Boolean isEdited
    ){
        this.author=author;
//        if(created==null)
//        {
//            this.created=new Timestamp(Instant.now().toEpochMilli());
//        }
//        else
////        if(created!=null)
//        {
            this.created=created;
//        }
//        else{
//
////            this.created=new Timestamp(Instant.now().atZone(ZoneId.of("UTC+03:00")).toEpochSecond());
////            this.created=Instant.now().atZone(ZoneId.of("UTC+03:00"));
//        }
//        this.created=created;
        this.forum=forum;

        this.isEdited= Boolean.FALSE;
        if(isEdited!=null)
        {
            this.isEdited = isEdited;
        }
        this.message=message;
        this.parent=parent;
        this.thread=thread;

    }

    public Post(){};

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getisEdited() {
        return isEdited;
    }

    public void setisEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object[] getBranch() {
        return branch;
    }

    public void setBranch(Object[] branch) {
        this.branch = branch;
    }
}
