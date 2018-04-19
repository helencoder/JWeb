package com.helencoder.domain.entity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Community_Events(用户事件记录表)
 *
 * Created by zhenghailun on 2018/4/19.
 */
public class CommunityEventsVo {
    private int id;
    private String userId;
    private String requestData;
    private Timestamp requestTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        this.requestTime = timestamp;
    }

    public void setRequestTime(Timestamp timestamp) {
        this.requestTime = timestamp;
    }

    public void setRequestTime(Date date) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        this.requestTime = timestamp;
    }
}
