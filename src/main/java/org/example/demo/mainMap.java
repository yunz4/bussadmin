package org.example.demo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class mainMap extends JPanel {

    private List<DefaultWaypoint> waypoints = new ArrayList<>();
    private boolean isFirstMarkerActive = false;
    private boolean isSecondMarkerActive = false;
    private GeoPosition firstMarkerPosition;
    private GeoPosition secondMarkerPosition;
    private List<GeoPosition> route = new ArrayList<>();
    WaypointPainter<DefaultWaypoint> waypointPainter ;
    public double sourcelat = 0;
    public double sourcelon = 0;
    public double destlat = 0;
    public double destlon = 0;
    private JXMapViewer jXMapViewer;

    private List<DefaultWaypoint> customMarkers = new ArrayList<>();
    private boolean isAddingCustomMarker = false;
    private boolean addPA = false;
    public mainMap() {
        initComponents();
        init();
    }

    private void addCustomMarker(GeoPosition position) {
        DefaultWaypoint waypoint = new DefaultWaypoint(position);
        customMarkers.add(waypoint);

        waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(new HashSet<>(customMarkers));


        // Rafraîchir la carte
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(waypointPainter);

        if (jXMapViewer.getOverlayPainter() instanceof CompoundPainter) {
            CompoundPainter<JXMapViewer> existingPainter = (CompoundPainter<JXMapViewer>) jXMapViewer.getOverlayPainter();
            List<Painter<JXMapViewer>> combinedPainters = new ArrayList<>(existingPainter.getPainters());
            combinedPainters.add(waypointPainter);
            jXMapViewer.setOverlayPainter(new CompoundPainter<>(combinedPainters));
        } else {
            jXMapViewer.setOverlayPainter(new CompoundPainter<>(painters));
        }
    }

    private void init() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jXMapViewer.setTileFactory(tileFactory);

        // Set initial map position (Guelmim, Morocco coordinates)
        GeoPosition geo = new GeoPosition(28.9884, -10.0574);
        jXMapViewer.setAddressLocation(geo);
        jXMapViewer.setCenterPosition(geo);
        jXMapViewer.setZoom(5);

        // Mouse interactions
        PanMouseInputListener mm = new PanMouseInputListener(jXMapViewer);
        jXMapViewer.addMouseListener(mm);
        jXMapViewer.addMouseMotionListener(mm);
        jXMapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(jXMapViewer));

        // Add mouse listener to add markers when enabled
        jXMapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                GeoPosition clickedPosition = jXMapViewer.convertPointToGeoPosition(e.getPoint());
                if (isFirstMarkerActive) {
                    firstMarkerPosition = clickedPosition; // Save first marker position
                    addBusStopMarker(firstMarkerPosition);
                    isFirstMarkerActive = false;
                    sourcelat = clickedPosition.getLatitude();
                    sourcelon = clickedPosition.getLongitude();
                    System.out.println(firstMarkerPosition);
                } else if (isSecondMarkerActive) {
                    secondMarkerPosition = clickedPosition; // Save second marker position
                    addBusStopMarker(secondMarkerPosition);
                    isSecondMarkerActive = false;
                    destlat = clickedPosition.getLatitude();
                    destlon = clickedPosition.getLongitude();
                    System.out.println(secondMarkerPosition.getLatitude());
                }

                // Update the route when both markers are set
                if (firstMarkerPosition != null && secondMarkerPosition != null) {
                    route.clear();
                    route.add(firstMarkerPosition);
                    route.add(secondMarkerPosition);
                    setCombinedOverlayPainter(route);
                    setRoute(firstMarkerPosition, secondMarkerPosition);
                }
                if (isAddingCustomMarker) {
                    addCustomMarker(clickedPosition);
                    pointArretPosition = clickedPosition;
                    isAddingCustomMarker = false;
                }
            }
        });
    }
    GeoPosition pointArretPosition = new GeoPosition(0,0);
    public GeoPosition ajouterPointArret(){
        isAddingCustomMarker = true;
        return pointArretPosition;
    }

    public void clearMarkers() {
        // Clear route and waypoints
        route.clear();
        waypoints.clear();
        customMarkers.clear(); // Clear custom markers as well
        waypointPainter.clearCache();
        firstMarkerPosition = null;
        secondMarkerPosition = null;

        // Remove any existing overlays on the map
        jXMapViewer.setOverlayPainter(null); // Clear all overlays


    }


    public GeoPosition markSource() {
        isFirstMarkerActive = true;
        waypoints.clear();
        route.clear();
        firstMarkerPosition = null;
        secondMarkerPosition = null;
        jXMapViewer.setOverlayPainter(null);
        return new GeoPosition(sourcelat , sourcelon);
    }



    public GeoPosition markDestination() {
        isSecondMarkerActive = true;
        return new GeoPosition(destlat, destlon);
    }
    private void addBusStopMarker(GeoPosition position) {
        DefaultWaypoint waypoint = new DefaultWaypoint(position);
        waypoints.add(waypoint);

        WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(new HashSet<>(waypoints));
        jXMapViewer.setOverlayPainter(waypointPainter);
    }
    private void setCombinedOverlayPainter(List<GeoPosition> route) {
        // Create the RoutePainter with the current route
        RoutePainter routePainter = new RoutePainter(route);

        // Create the WaypointPainter for the markers
        WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<>();
        Set<DefaultWaypoint> waypointSet = new HashSet<>(waypoints);
        waypointPainter.setWaypoints(waypointSet);

        // Create a list of painters to combine
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(routePainter);
        painters.add(waypointPainter);

        // Create a composite painter that combines both route and waypoints
        CompoundPainter<JXMapViewer> combinedPainter = new CompoundPainter<>(painters);

        jXMapViewer.setOverlayPainter(combinedPainter);
    }

    private void setRoute(GeoPosition fp, GeoPosition sp) {
        try {
            String url = "https://router.project-osrm.org/route/v1/driving/" + fp.getLongitude() + "," + fp.getLatitude() +
                    ";" + sp.getLongitude() + "," + sp.getLatitude() + "?overview=full&geometries=geojson";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the response and update route
            JSONObject json = new JSONObject(response.toString());
            if (json.has("routes")) {
                JSONArray routes = json.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject geometry = route.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");

                    List<GeoPosition> routePoints = new ArrayList<>();
                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray point = coordinates.getJSONArray(i);
                        double lon = point.getDouble(0);
                        double lat = point.getDouble(1);
                        routePoints.add(new GeoPosition(lat, lon));
                    }

                    RoutePainter routePainter = new RoutePainter(routePoints);

                    WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<>();
                    Set<DefaultWaypoint> waypointSet = new HashSet<>(waypoints);
                    waypointPainter.setWaypoints(waypointSet);

                    List<Painter<JXMapViewer>> painters = new ArrayList<>();
                    painters.add(routePainter);
                    painters.add(waypointPainter);

                    CompoundPainter<JXMapViewer> combinedPainter = new CompoundPainter<>(painters);
                    jXMapViewer.setOverlayPainter(combinedPainter);
                }
            } else {
                System.out.println("The 'routes' key was not found in the JSON response.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        jXMapViewer = new JXMapViewer();


        // Layout and adding components
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        add(jXMapViewer, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);


    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Map Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new mainMap());
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
