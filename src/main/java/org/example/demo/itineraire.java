package org.example.demo;

import javafx.util.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.HashMap;
import java.util.Map;

public class itineraire {
    private int busNum;
    private Pair<String , GeoPosition> source;
    private Pair<String , GeoPosition> destination;
    private Map<Integer , Pair<String , GeoPosition>> pointArret;
    public itineraire() {
        pointArret = new HashMap<>();
    }
    public int getBusNum() { return busNum; }
    public void setBusNum(int busNum) { this.busNum = busNum; }
    public Pair<String , GeoPosition> getSource() {
        return source;
    }
    public void setSource(Pair<String , GeoPosition> source) {
        this.source = source;
    }
    public Pair<String , GeoPosition> getDestination() {
        return destination;
    }
    public void setDestination(Pair<String , GeoPosition> destination) {
        this.destination = destination;
    }
    public Map<Integer , Pair<String , GeoPosition>> getPointArret() {
        return pointArret;
    }
    public void setPointArret(Map<Integer , Pair<String , GeoPosition>> pointArret) {
        this.pointArret = pointArret;
    }
    public void addPointArret(Integer pointId, Pair<String , GeoPosition> point) {
        pointArret.put(pointId, point);
    }
}