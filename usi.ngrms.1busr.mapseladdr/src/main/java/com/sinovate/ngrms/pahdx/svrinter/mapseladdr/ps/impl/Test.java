package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/6/4.
 */
public class Test {

    public static void main (String[] args) {
        String str = "安徽合肥合肥市 蜀山区 科学大道 <em>兴园</em> <em>小区</em> <em>南区</em> <em>01幢</em> <em>3单元</em> <em>3层</em> <em>305室</em>";
        str = str.replace(" ","");
        String[] a = str.split("</em>");
        System.out.println(str);
        String r = null;
        for (int i = 0 ; i < a.length ; i ++) {
            r = a[i].substring(a[i].indexOf("<em>")+4,a[i].length());
            System.out.println(r);
        }
    }
}
