package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.abl.bo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/11.
 */
public class ResAbilityBo implements Serializable{

    private String productName;//宽带、语音

    private String terminalPortType;//<!--接入方式，1-ADSL,2-LAN,3-FTTH,4-铜缆-->

    private String terminal;//终端规格(字符型)

    private String isE8C;//<!--是否E8C，数值，0-否，1-是-->

    private String ponType;//<!--PON口的GEPON属性，数值，1-EPON、 2-GPON ,其他情况返回空 -->

    private String ability;//<!--资源能力，数值，0-具备，1-不具备-->

    private String logicRate;//<!--理论带宽，字符串-->

    private String isFirstChoice;//<!--是否首选，数值，0-否，1-是-->

    private String postDirection;//局向

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTerminalPortType() {
        return terminalPortType;
    }

    public void setTerminalPortType(String terminalPortType) {
        this.terminalPortType = terminalPortType;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getIsE8C() {
        return isE8C;
    }

    public void setIsE8C(String isE8C) {
        this.isE8C = isE8C;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getPonType() {
        return ponType;
    }

    public void setPonType(String ponType) {
        this.ponType = ponType;
    }

    public String getIsFirstChoice() {
        return isFirstChoice;
    }

    public void setIsFirstChoice(String isFirstChoice) {
        this.isFirstChoice = isFirstChoice;
    }

    public String getLogicRate() {
        return logicRate;
    }

    public void setLogicRate(String logicRate) {
        this.logicRate = logicRate;
    }

    public String getPostDirection() {
        return postDirection;
    }

    public void setPostDirection(String postDirection) {
        this.postDirection = postDirection;
    }

    @Override
    public String toString() {
        return "ResAbilityBo{" +
                "productName='" + productName + '\'' +
                ", terminalPortType='" + terminalPortType + '\'' +
                ", isE8C='" + isE8C + '\'' +
                ", ponType='" + ponType + '\'' +
                ", ability='" + ability + '\'' +
                ", postDirection='" + postDirection + '\'' +
                '}';
    }
}
