package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.ps.support;

import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.aui.vo.GridVo;
import com.sinovate.ngrms.pahdx.svrinter.mapseladdr.das.dao.GridInfoDao;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;

/**
 * Created by limaple on 15/8/12.
 */
public class GetMapInfo {
    private static final Logger logger = LoggerFactory.getLogger(GetMapInfo.class);

    @Autowired
    private GridInfoDao gridInfoDao;

    public GetMapInfo(){

    }

    public void finalize() throws Throwable {

    }

    public void init() {
        //从网格表中提取网格特征放入grid_Points表
        //gridInfoDao.getGridCharacter();
    }

    public ArrayList<String> getGridName(Map<String, Double> lngAndLat) {
        Double lng = lngAndLat.get("lng");
        Double lat = lngAndLat.get("lat");

        logger.info("+++获取一定范围内的网格列表+++");
        List<Integer> matchedGridIds = gridInfoDao.matchedGridIds(lng, lat);
        logger.info("共包含" + matchedGridIds.size() + "个网格");
        Point targetPoint = new Point(lngAndLat.get("lng"), lngAndLat.get("lat"));

        ArrayList<String> grid_info = new ArrayList<>();

        for (Integer gridId: matchedGridIds) {
            GridVo grid = gridInfoDao.queryGrid(gridId);
            String[] polygons;
            if (grid.getGridPolygon().contains("GON")) {
                polygons = grid.getGridPolygon().split("GON")[1].split("\\)\\),\\(\\(");
            } else {
                polygons = grid.getGridPolygon().split("\\)\\),\\(\\(");
            }
            Boolean conts = false;
            Boolean negative = false;
            for(String polygon: polygons) {
                String[] polygon_ins = polygon.split("\\),\\(");
                for (String polygon_in: polygon_ins) {
                    List<Point> points = new ArrayList<>();
                    String[] pointStrs = polygon_in.replace('(', ' ').replace(')', ' ').trim().split(", ");
                    for (String pointStr: pointStrs) {
                        points.add(new Point(Double.parseDouble(pointStr.split(" ")[0]), Double.parseDouble(pointStr.split(" ")[1])));
                    }
                    if (containsPoint(targetPoint, points)) {
                        conts = true;
                        if (getArea(points) < 0) {
                            negative = true;
                        }
                    }
                }
            }
            if (conts && !negative) {
                grid_info.add(grid.getGridType());
                grid_info.add(grid.getSubareaName());
                grid_info.add(grid.getGridName());
                grid_info.add(gridInfoDao.get68Ad(gridId));
                grid_info.add(grid.getGridPolygon());
                grid_info.add(gridId.toString());
                grid_info.add(grid.getAreaId());
                return grid_info;
            }
        }
        return null;
    }

    public Map<String,Double> getLngAndLat(Double lng, Double lat){
        Map<String,Double> map=new HashMap<>();
        String url = "http://api.map.baidu.com/geoconv/v1/?coords=" + lng + "," + lat + "&from=1&to=5&ak=fo01QPSZFnAGNewh6Lqssuu1";
        String json = loadJSON(url);
        JSONObject obj = JSONObject.fromObject(json);
        if(obj.get("status").toString().equals("0")){
            double longitude=obj.getJSONArray("result").getJSONObject(0).getDouble("x");
            double latitude = obj.getJSONArray("result").getJSONObject(0).getDouble("y");
            longitude = 2 * lng-longitude;
            latitude = 2 * lat-latitude;
            //logger.info("lng: " + longitude + " lat: " + latitude);
            map.put("lng", longitude);
            map.put("lat", latitude);
			/*logger.info("经度："+ longitude +"---纬度："+ latitude);*/
        } else {
            logger.info("未找到相匹配的经纬度！");
        }
        return map;
    }

    public String loadJSON (String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return json.toString();
    }

    /**
     * 检查多边形是否包含了某点
     * @param point 传入需要检查的点
     * @param vertices 组成多边形的点集
     * @return 返回boolean标识点是否包含在多边形内
     */
    public static boolean containsPoint(Point point, List<Point> vertices) {
        int verticesCount = vertices.size();
        int nCross = 0;
        for (int i = 0; i < verticesCount; ++i) {
            Point p1 = vertices.get(i);
            Point p2 = vertices.get((i + 1) % verticesCount);

            // 求解 y=p.y 与 p1 p2 的交点
            if ( p1.getY().equals(p2.getY()) ) {   // p1p2 与 y=p0.y平行
                continue;
            }
            if ( point.getY() < Math.min(p1.getY(), p2.getY()) ) { // 交点在p1p2延长线上
                continue;
            }
            if ( point.getY() >= Math.max(p1.getY(), p2.getY()) ) { // 交点在p1p2延长线上
                continue;
            }
            // 求交点的 X 坐标
            Double x = (point.getY() - p1.getY()) * (p2.getX() - p1.getX())
                    / (p2.getY() - p1.getY()) + p1.getX();
            if ( x > point.getX() ) { // 只统计单边交点
                nCross++;
            }
        }
        // 单边交点为偶数，点在多边形之外
        return (nCross%2==1);
    }

    /**
     * S = 0.5 * ((x0*y1-x1*y0) + (x1*y2-x2*y1) + ... + (xn*y0-x0*yn))
     * @param list 组成多边形的点集，要求首尾坐标相同，即尾坐标重复一次首坐标，否则代码改为
     *             Point p2 = list.get((i + 1) % list.size())
     * @return 多边形面积area
     *
     */
    public double getArea(List<Point> list) {
        double area = 0.00;
        for (int i = 0; i < list.size() - 1; i++) {
            Point p1 = list.get(i);
            Point p2 = list.get(i + 1);
            area += p1.getX() * p2.getY() - p2.getX() * p1.getY();
        }
        area = area / 2.00;

        return area;
    }

}

class GridCharacter {
    private Integer gridId;
    private Double lng;
    private Double lat;

    GridCharacter(Integer gridId, Double lng, Double lat) {
        this.gridId = gridId;
        this.lng = lng;
        this.lat = lat;
    }

    public Integer getGridId() {
        return gridId;
    }

    public Double getLng() {
        return lng;
    }

    public Double getLat() {
        return lat;
    }
}

class Point {
    Double x, y;

    Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }


    public Double getY() {
        return y;
    }

}