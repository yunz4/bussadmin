package org.example.demo;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class RoutePainter implements Painter<JXMapViewer> {
    private final List<GeoPosition> route;

    public RoutePainter(List<GeoPosition> route) {
        this.route = route;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        if (route.size() < 2) return; // Need at least two points

        g = (Graphics2D) g.create();
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));

        // Convert geographical coordinates to screen points
        for (int i = 0; i < route.size() - 1; i++) {
            Point2D pt1 = map.convertGeoPositionToPoint(route.get(i));
            Point2D pt2 = map.convertGeoPositionToPoint(route.get(i + 1));
            g.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) pt2.getX(), (int) pt2.getY());
        }

        g.dispose();
    }

    public static void main(String[] args) {
        new mainMap().setVisible(true);
    }
}


