package com.helencoder.service;

import com.helencoder.dao.DataBaseDao;
import com.helencoder.domain.entity.CommunityEventsVo;
import com.helencoder.domain.entity.CommunityShieldRecordsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据服务
 *
 * Created by zhenghailun on 2018/4/19.
 */
@Component
public class RecordService {
    @Autowired
    private DataBaseDao dataBaseDao;

    /**
     * 获取用户事件记录数据
     */
    public List<CommunityEventsVo> getCommunityEventsList() {
        return dataBaseDao.getCommunityEventsList();
    }

    /**
     * 记录用户事件记录数据
     */
    public void recordCommunityEvent(CommunityEventsVo communityEventsVo) {
        dataBaseDao.recordCommunityEvent(communityEventsVo);
    }

    /**
     * 获取审核记录表数据
     */
    public List<CommunityShieldRecordsVo> getCommunityShieldRecordsList() {
        return dataBaseDao.getCommunityShieldRecordsList();
    }

    /**
     * 记录审核数据
     */
    public void recordCommunityShieldRecord(CommunityShieldRecordsVo communityShieldRecordsVo) {
        dataBaseDao.recordCommunityShieldRecord(communityShieldRecordsVo);
    }

}
