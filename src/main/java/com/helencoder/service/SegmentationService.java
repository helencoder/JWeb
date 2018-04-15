package com.helencoder.service;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 分词服务
 *
 * Created by zhenghailun on 2018/3/26.
 */
@Service
public class SegmentationService {
    private static Segment segment;
    private String[] allowSpeechTags = {"d", "f", "g", "h", "i", "j", "k", "l", "n", "s", "t", "v", "x"};

    public SegmentationService() {
        if (segment == null) {
            segment = HanLP.newSegment()
                    .enableNameRecognize(true)
                    .enableOrganizationRecognize(true)
                    .enableNumberQuantifierRecognize(true)
                    .enablePlaceRecognize(true)
                    .enableTranslatedNameRecognize(true);
        }
    }

    /**
     * 获取原始HanLP分词组件类
     */
    public Segment getInstance() {
        return segment;
    }

    /**
     * 分词、List形式输出结果
     *
     * @param content 文本内容
     * @param flag 是否进行词性过滤
     * @return List<Term>
     */
    public List<Term> segToList(String content, boolean flag) {
        List<Term> termList = segment.seg(content);
        List<Term> wordList = new ArrayList<Term>();
        for (Term term : termList) {
            if (flag) {
                if (isWordAllow(term)) {
                    wordList.add(term);
                }
            } else {
                wordList.add(term);
            }
        }

        return wordList;
    }

    /**
     * 分词、字符串形式输出结果
     *
     * @param content 文本内容
     * @param flag 是否进行词性过滤
     * @return String
     */
    public String segToStr(String content, String delimiter, boolean flag) {
        List<Term> termList = segment.seg(content);
        StringBuffer sb = new StringBuffer();
        for (Term term : termList) {
            if (flag) {
                if (isWordAllow(term)) {
                    sb.append(term.word);
                    sb.append(delimiter);
                }
            } else {
                sb.append(term.word);
                sb.append(delimiter);
            }
        }

        if (sb.toString().length() > 0) {
            return sb.toString().substring(0, sb.toString().lastIndexOf(delimiter));
        } else {
            return "";
        }
    }

    /**
     * 去除标点符号的特殊过滤
     *
     * @param content 文本内容
     * @param delimiter 分隔符
     */
    public String segWords(String content, String delimiter) {
        List<Term> termList = segment.seg(content);
        StringBuffer sb = new StringBuffer();
        for (Term term : termList) {
            if (!term.nature.toString().equals("w")) {
                sb.append(term.word);
                sb.append(delimiter);
            }
        }

        if (sb.toString().length() > 0) {
            return sb.toString().substring(0, sb.toString().lastIndexOf(delimiter));
        } else {
            return "";
        }
    }


    /**
     * 词性过滤
     *
     * @param term 单词
     * @return boolean
     */
    private boolean isWordAllow(Term term) {
        if (term.nature == null) {
            return false;
        } else {
            String nature = term.nature.toString();
            char firstChar = nature.charAt(0);
            boolean flag = false;
            // 词性过滤
            for (String tag: allowSpeechTags) {
                if (tag.charAt(0) == firstChar) {
                    flag = true;
                }
            }
            return flag && term.word.trim().length() > 1 && !CoreStopWordDictionary.contains(term.word);
        }
    }

}
