package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo;

/**
 * Created by limaple on 15/8/12.
 */
public class GridVo {
    private String gridName;
    private String gridPolygon;
    private String gridType; //城市、农村
    private String subareaName;
    private String areaId;


    public String getGridPolygon() {
        return gridPolygon;
    }

    public void setGridPolygon(String gridPolygon) {
        this.gridPolygon = gridPolygon;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getGridType() {
        return gridType;
    }

    public void setGridType(String gridType) {
        this.gridType = gridType;
    }

    public String getSubareaName() {
        return subareaName;
    }

    public void setSubareaName(String subareaName) {
        this.subareaName = subareaName;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
