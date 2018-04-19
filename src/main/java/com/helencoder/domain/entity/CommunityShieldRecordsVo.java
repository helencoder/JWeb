package com.helencoder.domain.entity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Community_Shield_Records(审核记录表)
 *
 * Created by zhenghailun on 2018/4/19.
 */
public class CommunityShieldRecordsVo {
    private int id;
    private String data;
    private String ml_res;
    private String dl_res;
    private String final_res;
    private String artificial_res;
    private String artificial_record;
    private Timestamp create_time;
    private Timestamp update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMlRes() {
        return ml_res;
    }

    public void setMlRes(String mlRes) {
        this.ml_res = mlRes;
    }

    public String getDlRes() {
        return dl_res;
    }

    public void setDlRes(String dlRes) {
        this.dl_res = dlRes;
    }

    public String getFinalRes() {
        return final_res;
    }

    public void setFinalRes(String finalRes) {
        this.final_res = finalRes;
    }

    public String getArtificialRes() {
        return artificial_res;
    }

    public void setArtificialRes(String artificialRes) {
        this.artificial_res = artificialRes;
    }

    public String getArtificialRecord() {
        return artificial_record;
    }

    public void setArtificialRecord(String artificialRecord) {
        this.artificial_record = artificialRecord;
    }

    public Timestamp getCreateTime() {
        return create_time;
    }

    public void setCreateTime() {
        Timestamp timeStamp = new Timestamp(new Date().getTime());
        this.create_time = timeStamp;
    }

    public void setCreateTime(Timestamp timestamp) {
        this.create_time = timestamp;
    }

    public void setCreateTime(Date date) {
        Timestamp timeStamp = new Timestamp(date.getTime());
        this.create_time = timeStamp;
    }

    public Timestamp getUpdateTime() {
        return update_time;
    }

    public void setUpdateTime() {
        Timestamp timeStamp = new Timestamp(new Date().getTime());
        this.update_time = timeStamp;
    }

    public void setUpdateTime(Timestamp timestamp) {
        this.update_time = timestamp;
    }

    public void setUpdateTime(Date date) {
        Timestamp timeStamp = new Timestamp(date.getTime());
        this.update_time = timeStamp;
    }
}
