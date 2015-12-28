package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.aws;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 地址分词器，实现地址的拆分和合并
 * Created by zszhang on 2015/5/20.
 */
public class AddressSegmenterImpl implements AddressSegmenter {
    private final static char DELEMITER_CHAR = ' ';
    private final static String DEFALUT_ADDRESS_UNITS = "(/|市|县|区|小区|路|街道|道|镇|乡|营|房|厂|城|场|坝|社|幢|局|排|期|层|室|村|巷|座|号|郡|舍|栋|郢|大厦|集团|街|庄|单元|号|桥|队|组|巷|学校|大学|中学|小学|园|圩|部|所|屯|店|馆|公司|厅|塘|处|胡同|岗|银行|支行|委|故居|寺|庙|庵|岛|号楼|号院|亭|口|墩|集|集镇|楼|苑)";
    // 以'|'线分割的地址单元字符串，系统将以此地址单元进行分词。
    private final String addressUnits;
    private final Pattern pattern;
    // 控制是否在应用软件里面进行分词，还是只在底层搜索引擎分词。取false值，相当于本类未生效。
    private final boolean useAppWordSegment;

    public AddressSegmenterImpl(String addressUnits, boolean useAppWordSegment) {
        if (null == addressUnits || addressUnits.isEmpty())
            this.addressUnits = DEFALUT_ADDRESS_UNITS;
        else
            this.addressUnits = "(" + addressUnits + ")";
        pattern = Pattern.compile(this.addressUnits);
        this.useAppWordSegment = useAppWordSegment;
    }

    /**
     * 对输入地址进行分词
     *
     * @param addr 输入地址
     * @return 分词后的结果
     */
    @Override
    public String splitAddr(String addr) {
        if (!useAppWordSegment) return addr;

        if (null == addr || addr.isEmpty())
            return null;

        String result = full2Half(addr);

        Matcher matcher = pattern.matcher(result);
        if (matcher != null)
            result = matcher.replaceAll("$0 ");

        result = result.trim();
        return result;
    }

    /**
     * 对分词结果进行反向恢复
     *
     * @param splitAddr 分词的结果
     * @return 恢复后的地址
     */
    @Override
    public String recoverAddr(String splitAddr) {
        if (!useAppWordSegment) return splitAddr;

        if (null == splitAddr || splitAddr.isEmpty())
            return null;

        return splitAddr.replaceAll(String.valueOf(DELEMITER_CHAR), "");
    }

    /**
     * 将准备写入ES中的数据进行分词
     *
     * @param data 准备写入ES中的数据
     * @return
     */
    @Override
    public void splitAddrData(Map<String, Object> data) {
        if (!useAppWordSegment) return;

        if (null == data || data.isEmpty()) return;
        String value = (String) data.get("name");
        if (null == value || value.isEmpty()) return;
        String splitAddr = this.splitAddr(value);
        data.put("name", splitAddr);
    }

    /**
     * 将从ES中读出的数据进行恢复
     *
     * @param data 从ES中读出的数据
     * @return
     */
    @Override
    public void recoverAddrData(Map<String, Object> data) {
        if (!useAppWordSegment) return;

        if (null == data || data.isEmpty()) return;
        String value = (String) data.get("name");
        String recoverValue = null;
        if (null != value && !value.isEmpty()) {
            recoverValue = this.recoverAddr(value);
            data.put("name", recoverValue);
        }
        value = (String) data.get("highlightText");
        if (null != value && !value.isEmpty()) {
            recoverValue = this.recoverAddr(value);
            data.put("highlightText", recoverValue);
        }
    }

    // 全角转半角，同时将标点符号替换为空格
    protected String full2Half(String fullStr) {
        if (null == fullStr || fullStr.isEmpty()) {
            return fullStr;
        }
        char[] ca = fullStr.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] >= 65281 && ca[i] <= 65374) {
                ca[i] = (char) (ca[i] - 65248);
            } else if (ca[i] == 12288) { // 空格
                ca[i] = (char) 32;
            }
            if (ca[i] >= 0x21 && ca[i] <= 0x2f && ca[i] != '-' && ca[i] != '#' || ca[i] >= 0x3a && ca[i] <= 0x40 || ca[i] >= 0x5b && ca[i] <= 0x60 || ca[i] >= 0x7b && ca[i] <= 0x7e) {
                // 标点符号转为空格, -号不转，是因为可能有-1，-2等代表负数
                ca[i] = DELEMITER_CHAR;
            }
        }
        return new String(ca);
    }
}
