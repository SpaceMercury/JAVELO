package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class RouteBean{
    private final RouteComputer computer;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    public RouteBean(RouteComputer computer) {
        this.computer = computer;
        this.waypoints = FXCollections.observableArrayList();
        this.route = new SimpleObjectProperty<>();
        this.highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        this.elevationProfile = new SimpleObjectProperty<>();
        waypoints.addListener((Observable o) -> {

        });
    }


    public DoubleProperty highlightedPositionProperty() {
        return this.highlightedPosition;
    }

    public double getHighlightedPosition() {
        return this.highlightedPosition.getValue();
    }

    public void setHighlightedPosition(double position) {
        this.highlightedPosition.setValue(position);
    }
}
