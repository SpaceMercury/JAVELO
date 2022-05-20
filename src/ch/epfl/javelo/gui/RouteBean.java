package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class RouteBean{
    private final ObservableList<Waypoint> waypoints;
    private final ReadOnlyObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;

    public RouteBean(RouteComputer computer) {
        this.waypoints = FXCollections.observableArrayList();
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
