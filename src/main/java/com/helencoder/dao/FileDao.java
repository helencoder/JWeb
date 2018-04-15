package com.helencoder.dao;

import com.helencoder.domain.utils.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通过额外配置文件引入的数据
 *
 * Created by zhenghailun on 2018/3/26.
 */
@Repository
public class FileDao {

    @Value("${data.path}")
    private String dataPath;

    /**
     * 获取文本内容
     */
    public List<String> wordList() {
        return FileIO.getFileDataByLine(dataPath);
    }
}
