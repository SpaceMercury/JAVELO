package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import ch.epfl.javelo.routing.SingleRoute;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;

import static javafx.collections.FXCollections.observableArrayList;

public final class RouteBean{
    private static final double MAX_DISTANCE = 5.0;
    private static final int MAX_SIZE = 100;
    private final RouteComputer computer;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    public RouteBean(RouteComputer computer) {
        this.computer = computer;
        this.waypoints = observableArrayList();
        this.route = new SimpleObjectProperty<>();
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.elevationProfile = new SimpleObjectProperty<>();
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
        HashMap<NodePair, Route> routeCache = new HashMap<>();
        waypoints.addListener((Observable o) -> {
            boolean nullProperty = false; //will become true if two waypoints cannot be connected or list is too short
            if(waypoints.size() < 2) {
                nullProperty = true;
            }
            for (int i = 0; i < waypoints.size() - 1; i++) {
                NodePair currentNodes = new NodePair(waypoints.get(i).nodeId(), waypoints.get(i + 1).nodeId());
                Route best = null;
                if(routeCache.containsKey(currentNodes)) {
                    best = routeCache.get(currentNodes);
                } else {
                    best = computer.bestRouteBetween(currentNodes.firstId, currentNodes.secondId);
                }
                if(best == null) {
                    nullProperty = true;
                }
                if(!routeCache.containsKey(currentNodes)) {
                    //routeCache.
                }
            }
            if(nullProperty) {
                route.set(null);
                elevationProfile.set(null);
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