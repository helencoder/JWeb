package com.helencoder.domain.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.*;

/**
 * 公公方法类
 *
 * Created by zhenghailun on 2018/3/20.
 */
public class BasicUtil {

    private static Segment segment = HanLP.newSegment()
            .enableAllNamedEntityRecognize(true)
            .enableCustomDictionaryForcing(true)
            .enableNumberQuantifierRecognize(true);

    /**
     * 分词、List形式输出结果
     *
     * @param content 文本内容
     * @param flag 是否进行词性过滤
     * @return List<Term>
     */
    public static List<Term> segToList(String content, boolean flag) {
        List<Term> termList = segment.seg(content);
        List<Term> wordList = new ArrayList<Term>();
        for (Term term : termList) {
            if (flag) {
                if (!term.nature.toString().equals("w")) {
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
    public static String segToStr(String content, String delimiter, boolean flag) {
        List<Term> termList = segment.seg(content);
        StringBuffer sb = new StringBuffer();
        for (Term term : termList) {
            if (flag) {
                if (!term.nature.toString().equals("w")) {
                    sb.append(term.word);
                    sb.append(delimiter);
                }
            } else {
                sb.append(term.word);
                sb.append(delimiter);
            }
        }

        return sb.toString().substring(0, sb.toString().lastIndexOf(delimiter));
    }

    /**
     * 数组转换为字符串
     *
     * @param list
     * @param delimiter 分隔符
     * @return 字符串
     */
    public static String mkString(List<String> list, String delimiter) {
        StringBuffer sb = new StringBuffer();
        for (String data : list) {
            sb.append(data);
            sb.append(delimiter);
        }
        String str = sb.toString();

        if (str.isEmpty()) {
            return "";
        } else {
            return str.substring(0, str.lastIndexOf(delimiter));
        }
    }

    /**
     * Map排序(按键值排序)
     *
     * @param map  (Map)
     * @param flag true降序 false升序
     * @return sortedMap
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map, boolean flag) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (flag) { // 降序
                    return o2.getValue().compareTo(o1.getValue());
                } else {    // 升序
                    return o1.getValue().compareTo(o2.getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * 检测是否包含中文字符(utf-8编码)
     *
     * @param str
     * @return boolean
     */
    public static boolean isContainsChineseCharacter(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.toString(str.charAt(i)).matches("[\u4E00-\u9FA5]+")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检测是否包含数字
     *
     * @param str
     * @return boolean
     */
    public static boolean isContainsNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检测是否包含其他字符(暂时除中文外)
     *
     * @param str
     * @return boolean
     */
    public static boolean isContainsOtherCharacter(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.toString(str.charAt(i)).matches("[\u4E00-\u9FA5]+")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 中文转换为拼音(暂时仅对只包含中文字符的进行处理)
     *
     * @param inputString
     * @return boolean
     */
    public static String transformToPinyin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        // 设置输出格式(小写,无音调)
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] input = inputString.trim().toCharArray();
        StringBuffer output = new StringBuffer("");

        try {
            for (int i = 0; i < input.length; i++) {
                // 检测是否属于中文
                if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output.append(temp[0]);
                    output.append(" ");
                } else {
                    output.append(Character.toString(input[i]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        return output.toString();
    }

}
