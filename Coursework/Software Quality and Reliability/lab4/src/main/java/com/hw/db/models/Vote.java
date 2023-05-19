package com.hw.db.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Vote {
    private String nickname;
    private Integer tid;
    private Integer voice;
    @JsonCreator
    public Vote(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("voice") Integer voice
    ){
     this.nickname=nickname;
     this.voice=voice;
    }

    public Integer getVoice() {
        return voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }
}
