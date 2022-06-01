package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

public final class RouteBean{
    private static final double MAX_DISTANCE = 5.0;
    private static final int MAX_SIZE = 100;
    private static final float FACTOR = 0.75f; //Default load factor for LinkedHashMaps
    private final RouteComputer routeComputer;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    //The idea for the following was found here
    /**
     * https://stackoverflow.com/questions/41184893/delete-oldest-objects-from-hashmap-to-reach-certain-size
     *
     * Note that there is an existing danger as it is not synchronized, however the implementation
     * of a synchronized version using Collections.synchronizedMap() can not be used here, as a simple map
     * does not allow null values, but we in fact want null values to be stored.
     */
    private final HashMap<NodePair, Route> routeCache = new LinkedHashMap<>(MAX_SIZE, FACTOR, true) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry oldest) {
            return size() > MAX_SIZE;
        }
    };

    /**
     * NodePair is a class/record that will facilitate the handling of
     * nodes within the cache memory map created to win time
     * in calculating the fastest way, taking the node ids
     * as construction parameters.
     */
    record NodePair(int firstId, int secondId) {}

    public RouteBean(RouteComputer computer) {
        this.routeComputer = computer;
        this.waypoints = observableArrayList();
        this.route = new SimpleObjectProperty<>(null);
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.elevationProfile = new SimpleObjectProperty<>(null);
        updateListeners();

    }

    private void updateListeners() {
        waypoints.addListener((ListChangeListener<? super Waypoint>) c -> {
            boolean nullProperty = false; //will become true if two waypoints cannot be connected
            if(waypoints.size() > 1) {
                List<Route> subRoutes = new ArrayList<>();
                Route bestRoute;
                int startId, endId;
                for (int i = 0; i < waypoints.size() - 1; i++) {
                    startId = waypoints.get(i).nodeId();
                    endId = waypoints.get(i).nodeId();
                    NodePair current = new NodePair(startId, endId);
                    if(!routeCache.containsKey(current)) {
                        routeCache.put(current, routeComputer.bestRouteBetween(current.firstId, current.secondId));
                    }
                    bestRoute = routeCache.get(current);
                    subRoutes.add(bestRoute);
                    if(bestRoute == null) {
                        nullProperty = true;
                    }
                }
                if(!nullProperty){
                    route.setValue(new MultiRoute(subRoutes));
                    elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.getValue(), MAX_DISTANCE));
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