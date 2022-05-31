package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

/**
 * @author ventura
 */
public final class WaypointsManager {
    //Constants used in the class
    private static final String GREEN = "first";
    private static final String BLUE = "middle";
    private static final String RED = "last";
    private static final String PIN_NAME = "pin";
    private static final String OUT = "pin_outside";
    private static final String IN = "pin_inside";
    private static final String OUT_DESIGN = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private static final String IN_DESIGN = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private static final String ERROR = "Aucune route à proximité !";
    private static final int SEARCH_RADIUS = 500;
    //Attributes of the class
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> property;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorConsumer;
    private final Pane pane;
    private final HashMap<Waypoint, Group> currentPins = new HashMap<>();
    private final ObjectProperty<Point2D> mouseLocation;

    /**
     * Constructor of the WaypointsManager class
     *
     * @param graph         the graph of Manager
     * @param property      the object property on the mapview
     * @param waypoints     the observable list of waypoints
     * @param errorConsumer the error consumer
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.property = property;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;
        this.mouseLocation = new SimpleObjectProperty<>();
        this.pane = new Pane();
        this.pane.setPickOnBounds(false);
    }
    //TODO: Remake the whole class for Waypoints Manager
    //TODO: Debug the duo WPM and BMM with Coralies classes, to see where the error might be
}


