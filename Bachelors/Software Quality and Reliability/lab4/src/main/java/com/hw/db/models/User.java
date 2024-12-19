package com.hw.db.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private String about;
    private String email;
    private String fullname;
    private String nickname;



    @JsonCreator
    public User(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("email") String email,
            @JsonProperty("fullname") String fullname,
            @JsonProperty("about") String about
    ){
        this.nickname=nickname;
        this.email=email;
        this.fullname=fullname;
        this.about=about;
    };

    public User() {

    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
