package com.helencoder.dao;

import com.helencoder.domain.entity.CommunityEventsVo;
import com.helencoder.domain.entity.CommunityShieldRecordsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库相关
 *
 * Created by zhenghailun on 2018/4/19.
 */
@Repository
public class DataBaseDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取用户事件记录数据
     */
    public List<CommunityEventsVo> getCommunityEventsList() {
        String sql = "SELECT * FROM Community_Events";
        List<CommunityEventsVo> list = jdbcTemplate.query(sql, new RowMapper<CommunityEventsVo>() {
            @Override
            public CommunityEventsVo mapRow(ResultSet resultSet, int i) throws SQLException {
                CommunityEventsVo communityEventsVo = new CommunityEventsVo();
                communityEventsVo.setId(resultSet.getInt("id"));
                communityEventsVo.setUserId(resultSet.getString("userId"));
                communityEventsVo.setRequestData(resultSet.getString("requestData"));
                communityEventsVo.setRequestTime(resultSet.getTimestamp("requestTime"));
                return communityEventsVo;
            }
        });

        return list;
    }

    /**
     * 记录用户事件记录数据
     */
    public void recordCommunityEvent(CommunityEventsVo communityEventsVo) {
        try {
            String sql = "INSERT INTO Community_Events (userId, requestData, requestTime) VALUES (?, ?, ?)";
            int res = jdbcTemplate.update(sql, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setString(1, communityEventsVo.getUserId());
                    ps.setString(2, communityEventsVo.getRequestData());
                    ps.setTimestamp(3, communityEventsVo.getRequestTime());
                }
            });

            if (res < 1) {
                System.out.println("记录用户事件记录数据出错！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取审核记录表数据
     */
    public List<CommunityShieldRecordsVo> getCommunityShieldRecordsList() {
        String sql = "SELECT * FROM Community_Shield_Records";
        List<CommunityShieldRecordsVo> list = jdbcTemplate.query(sql, new RowMapper<CommunityShieldRecordsVo>() {
            @Override
            public CommunityShieldRecordsVo mapRow(ResultSet resultSet, int i) throws SQLException {
                CommunityShieldRecordsVo communityShieldRecordsVo = new CommunityShieldRecordsVo();
                communityShieldRecordsVo.setId(resultSet.getInt("id"));
                communityShieldRecordsVo.setData(resultSet.getString("data"));
                communityShieldRecordsVo.setMlRes(resultSet.getString("ml_res"));
                communityShieldRecordsVo.setDlRes(resultSet.getString("dl_res"));
                communityShieldRecordsVo.setFinalRes(resultSet.getString("final_res"));
                communityShieldRecordsVo.setArtificialRes(resultSet.getString("artificial_res"));
                communityShieldRecordsVo.setArtificialRecord(resultSet.getString("artificial_record"));
                communityShieldRecordsVo.setCreateTime(resultSet.getTimestamp("create_time"));
                communityShieldRecordsVo.setUpdateTime(resultSet.getTimestamp("update_time"));
                return communityShieldRecordsVo;
            }
        });

        return list;
    }

    /**
     * 记录审核数据
     */
    public void recordCommunityShieldRecord(CommunityShieldRecordsVo communityShieldRecordsVo) {
        try {
            String sql = "INSERT INTO Community_Shield_Records (data, ml_res, dl_res, final_res, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?)";
            int res = jdbcTemplate.update(sql, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setString(1, communityShieldRecordsVo.getData());
                    ps.setString(2, communityShieldRecordsVo.getMlRes());
                    ps.setString(3, communityShieldRecordsVo.getDlRes());
                    ps.setString(4, communityShieldRecordsVo.getFinalRes());
                    ps.setTimestamp(5, communityShieldRecordsVo.getCreateTime());
                    ps.setTimestamp(6, communityShieldRecordsVo.getUpdateTime());
                }
            });
            if (res < 1) {
                System.out.println("记录审核记录数据出错！");
            }
        } catch (Exception ex) {

        }
    }

}
