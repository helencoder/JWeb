package com.helencoder.service;

import com.helencoder.domain.utils.DFA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 过滤服务
 *
 * Created by zhenghailun on 2018/3/26.
 */
@Component
public class FilterService {
    @Autowired
    private DFA dfa;

    public boolean check(String str) {
        return dfa.check(str);
    }
}
