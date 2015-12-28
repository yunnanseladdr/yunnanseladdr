package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.aws;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.testng.annotations.Test;

/**
 * Created by zszhang on 2015/5/27.
 */
public class TestQuanJiaoString {
    private HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    public TestQuanJiaoString() {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public String toPinyin(String fullStr) {
        char[] ca = fullStr.toCharArray();
        StringBuilder pyfull = new StringBuilder();
        StringBuilder pyabc = new StringBuilder();
        for (int i = 0; i < ca.length; i++) {
            try {
                String[] strs = PinyinHelper.toHanyuPinyinStringArray(ca[i], format);
                if (null != strs) {
                    pyfull.append(strs[0]);
                    pyabc.append(strs[0].charAt(0));
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        return pyfull.toString();
    }

    @Test
    public void testmain() {
        TestQuanJiaoString testQuanJiaoString = new TestQuanJiaoString();
        String fullStr = "—②天安门";
        String py = testQuanJiaoString.toPinyin(fullStr);
        System.out.println(fullStr + " --> " + py);
    }
}
