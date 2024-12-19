package com.hw.db.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class Thread {
    private Integer id;
    private String author;
    private String forum;
    private  String message;
    private String slug;
    private String title;
    private Integer votes;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp created;

    @JsonCreator
    public Thread(
            @JsonProperty("author") String author,
            @JsonProperty("created") Timestamp created,
            @JsonProperty("forum") String forum,
            @JsonProperty("message") String message,
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("votes") Integer votes
    ){
        this.author=author;
        this.created = created;
        this.forum=forum;
        this.message=message;
        this.slug=slug;
        this.title=title;
        this.votes=votes;
    }

    public Thread() {

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
