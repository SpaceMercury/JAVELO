package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public final class RouteBean{
    private static final double MAX_DISTANCE = 5.0;
    private static final int MAX_SIZE = 100;
    private static final float FACTOR = 0.75f;
    private final RouteComputer computer;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    public RouteBean(RouteComputer computer) {
        this.computer = computer;
        this.waypoints = observableArrayList();
        this.route = new SimpleObjectProperty<>(null);
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.elevationProfile = new SimpleObjectProperty<>(null);
        updateListeners();
    }

    private void updateListeners() {
        /**
         * NodePair is a class/record that will facilitate the handling of
         * nodes within the cache memory map created to win time
         * in calculating the fastest way, taking the node ids
         * as construction parameters.
         */
        record NodePair(int firstId, int secondId) {}
        HashMap<NodePair, Route> routeCache = new LinkedHashMap<>(MAX_SIZE, FACTOR, true);
        waypoints.addListener((ListChangeListener<? super Waypoint>) c -> {
            boolean nullProperty = false; //will become true if two waypoints cannot be connected or list is too short
            if(waypoints.size() > 1) {
                List<Route> subRoutes = new ArrayList<>();
                Route bestRoute;
                int startId, endId;
                for (int i = 0; i < waypoints.size() - 1; i++) {
                    startId = waypoints.get(i).nodeId();
                    endId = waypoints.get(i).nodeId();
                    boolean cached = false;
                    NodePair current = new NodePair(startId, endId);
                    if(!routeCache.containsKey(current)) {
                        if(routeCache.size() < MAX_SIZE - 1) {
                            routeCache.put(current, computer.bestRouteBetween(current.firstId, current.secondId));
                            cached = true;
                        }
                    } else {
                        cached = true;
                    }
                    if(cached) {
                        bestRoute = routeCache.get(current);
                    } else {
                        bestRoute = computer.bestRouteBetween(current.firstId, current.secondId);
                    }
                    subRoutes.add(bestRoute);
                }
            }
        });
    }

    //---------------------------------------------------------------------
    //Bean classes for the point list
    //---------------------------------------------------------------------

    public ObservableList<Waypoint> getWaypoints() {
        return this.waypoints;
    }

    //---------------------------------------------------------------------
    //Bean classes for the route
    //---------------------------------------------------------------------

    public ReadOnlyObjectProperty<Route> getRouteProperty() {
        return this.route;
    }

    public Route getRoute() {
        return this.route.getValue();
    }

    //---------------------------------------------------------------------
    //Bean classes for the highlighted position
    //---------------------------------------------------------------------

    public DoubleProperty getHighlightedPositionProperty() {
        return this.highlightedPosition;
    }

    public double getHighlightedPosition() {
        return this.highlightedPosition.get();
    }

    public void setHighlightedPosition(double position) {
        this.highlightedPosition.setValue(position);
    }

    //---------------------------------------------------------------------
    //Bean classes for the elevation profile
    //---------------------------------------------------------------------

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty() {
        return this.elevationProfile;
    }

    public ElevationProfile getElevationProfile() {
        return this.elevationProfile.getValue();
    }
}