package com.youyou.uucar.Utils.Support;


import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于替换多个字符串的类，与MultiStringReplacer不同的是它会忽略非中文字符
 * 如：关键字里面有“操比”，那么同时也会过滤“操x比”之类的词
 *
 * @author meteor
 */
public final class MultiStringReplacerEx {
    // 子节点列表，Map的key是子节点对应的字符
    private Map<Character, MultiStringReplacerEx> childs = new HashMap<Character, MultiStringReplacerEx>();

    // 要替换的旧字符串
    private String oldWord;

    // 新字符串
    private String newWord;

    /*
     * 判断某个字符是否是汉字 汉字字符对应的 int 值为 从 19968 到 171941 （包括19968和171941）
     */
    private static boolean isChineseCharacter(char c) {
        return (19968 <= (int) c) && ((int) c <= 171941);
    }

    private static boolean isIgnoreCharacter(char c) {
//        return c != '<' && c != '>';
//		return !isChineseCharacter(c) && c != '<' && c != '>';
        // mark: 默认所有字符都可以被替换,而不只是中文,有需要再改
        return false;
    }

    /**
     * 用于存放查找关键字时的位置信息
     *
     * @author meteor
     */
    private static class FindResultPosInfo {
        MultiStringReplacerEx msr;    // 关键字对应的节点
        int idx;            // 关键字出现的位置
        int length;            // 关键字出现的长度

        FindResultPosInfo(MultiStringReplacerEx msr, int idx, int length) {
            this.msr = msr;
            this.idx = idx;
            this.length = length;
        }
    }

    /**
     * 添加一对替换关键字
     *
     * @param oldWords 要替换的字符串
     * @param newWords 新字符串
     */
    public void add(String oldWords, String newWords) {
        add(oldWords, 0, newWords);
    }

    private void add(String oldWords, int idx, String newWords) {
        if (oldWords.length() == idx) {
            // 达了树的叶子节点，此时对该节点设置字符串对
            this.newWord = newWords;
            this.oldWord = oldWords;
            return;
        }

        // 建立一个字节点
        char next_ch = oldWords.charAt(idx);
        MultiStringReplacerEx ti = childs.get(next_ch);
        if (ti == null) {
            ti = new MultiStringReplacerEx();
            childs.put(next_ch, ti);
        }

        // 递归建立下一个字节点
        ti.add(oldWords, idx + 1, newWords);
    }

    /**
     * 从字符串指定位置开始比较当前位置是否有关键字匹配
     */
    private FindResultPosInfo compareWords(String s, int idx, int ignoreCount, int deep) {
        // 找到匹配节点，并且该节点没有子节点（满足此条件是为了最长匹配）时，直接返回当前位置
        if (this.oldWord != null && this.childs.size() == 0) {
            final int n = this.oldWord.length() + ignoreCount;
            return new FindResultPosInfo(this, idx - n, n);
        }

        // 如果字符串结束，则返回null
        if (idx >= s.length())
            return null;

        // 查找匹配的字节点
        char c = s.charAt(idx);
        if (deep > 0 && isIgnoreCharacter(c)) {
            //if(deep > 0){
            int i = 0;
            for (; isIgnoreCharacter(c) && idx + i < s.length(); ++i)
                c = s.charAt(idx + i);
            if (i > 0) {
                --i;
                idx += i;
                ignoreCount += i;
            }
        }
        MultiStringReplacerEx ti = childs.get(c);
        if (ti == null)
            return null;

        // 递归匹配其余的字节点
        FindResultPosInfo fi = ti.compareWords(s, idx + 1, ignoreCount, deep + 1);

        // 如果其余的字节点不匹配，则检查当前节点是否需要替换的节点
        if (fi == null && ti.oldWord != null) {
            final int n = ti.oldWord.length() + ignoreCount;
            return new FindResultPosInfo(ti, idx + 1 - n, n);
        }

        return fi;
    }

    private FindResultPosInfo findWords(String s, int idx) {
        // 逐个位置比较是否匹配替换关键字
        for (int i = idx; i < s.length(); ++i) {
            FindResultPosInfo wi = compareWords(s, i, 0, 0);
            if (wi != null)
                return wi;
        }
        return null;
    }

    /**
     * 查找第一个出现的关键字
     *
     * @param s   要查找的字符串
     * @param idx 起始位置
     * @return Pair(找到的关键字，关键字在字符串中的位置)，如果没找到，返回null
     */
    public Pair<String, Integer> findFirstWord(String s, int idx) {
        FindResultPosInfo fi = findWords(s, idx);
        if (fi == null)
            return null;
        return Pair.create(fi.msr.oldWord, fi.idx);
    }

    /**
     * 查找第一个出现的关键字
     *
     * @param s 要查找的字符串
     * @return Pair(找到的关键字，关键字在字符串中的位置)
     */
    public Pair<String, Integer> findFirstWord(String s) {
        return findFirstWord(s, 0);
    }

    /**
     * 检查字符串中是否存在需要替换的关键字
     *
     * @param s 要检查的字符串
     * @return
     */
    public boolean existWords(String s) {
        return findWords(s, 0) != null;
    }

    /**
     * 计算字符串中出现的关键字次数
     *
     * @param s 要检查的字符串
     * @return 关键字出现的次数
     */
    public int countKeywords(String s) {
        FindResultPosInfo wi = findWords(s, 0);        // 查找第一个替换的位置
        if (wi == null)
            return 0;
        int writen = 0;
        int count = 0;
        for (; wi != null && wi.idx < s.length(); ) {
            ++count;
            writen = wi.idx + wi.length;
            wi = findWords(s, writen);                // 查找下一个替换位置
        }
        return count;
    }

    /**
     * 替换字符串中的旧字符串
     *
     * @param s 要替换的字符串
     * @return
     */
    public String replace(String s) {
        FindResultPosInfo wi = findWords(s, 0);        // 查找第一个替换的位置
        if (wi == null)
            return s;
        StringBuilder sb = new StringBuilder(s.length());
        int writen = 0;
        for (; wi != null && wi.idx < s.length(); ) {
            sb.append(s, writen, wi.idx);            // append 原字符串不需替换部分
            sb.append(wi.msr.newWord);                    // append 新字符串
            writen = wi.idx + wi.length;    // 忽略原字符串需要替换部分
            wi = findWords(s, writen);                // 查找下一个替换位置
        }
        sb.append(s, writen, s.length());            // 替换剩下的原字符串
        return sb.toString();
    }

    public static void main(String[] argv) {
        MultiStringReplacerEx m = new MultiStringReplacerEx();
        m.add("做爱", "**");
        System.out.println(m.replace("sdd 爱做abc爱123"));
    }
}