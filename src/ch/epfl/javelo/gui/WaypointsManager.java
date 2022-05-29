package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author ventura
 */
public final class WaypointsManager {
    //Constants used in the class
    private static final String GREEN = "first";
    private static final String BLUE = "middle";
    private static final String RED = "last";
    private static final String PIN = "pin";
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
    private Point2D mouseLocation;

    /**
     * Constructor of the WaypointsManager class
     * @param graph the graph of Manager
     * @param property the object property on the mapview
     * @param waypoints the observable list of waypoints
     * @param errorConsumer the error consumer
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.property = property;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();
        this.pane.setPickOnBounds(false);
        property.addListener((o, oV, nV) -> makePins());
        waypoints.addListener((ListChangeListener<? super Waypoint>) c -> makePins());
    }

    /**
     * Method that returns the pane
     * @return the pane that contains the waypoints
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Method that adds a new point from a selected point (providing it is possible)
     * @param x x-coord of the selected point
     * @param y y-coord of the selected point
     */
    public void addWaypoint(double x, double y) {
        PointWebMercator pointWM = property.get().pointAt(x, y);
        PointCh point = pointWM.toPointCh();
        Waypoint waypoint = newWaypoint(point);
        if(!(waypoint == null) && !(waypoints.contains(waypoint))) {
            waypoints.add(waypoint);
        }
    }

    /**
     * Method that will turn a PointCh into a point (if it is possible)
     * @param point the PointCh that is to be turned into a point
     * @return a new point, or null if it could not be made
     */
    private Waypoint newWaypoint(PointCh point) {
        if(point == null) {
            errorConsumer.accept(ERROR);
            return null;
        }
        int nodeId = graph.nodeClosestTo(point, SEARCH_RADIUS);
        if(nodeId == -1) {
            errorConsumer.accept(ERROR);
            return null;
        }
        return new Waypoint(point, nodeId);
    }

    /**
     * Method that will create all the pins for the waypoints
     */
    private void makePins() {
        List<Node> pins = new ArrayList<>();
        for(int i = 0; i < waypoints.size(); i++) {
            //creation of a new pin
            SVGPath ext = new SVGPath();
            SVGPath in = new SVGPath();
            ext.setContent(OUT_DESIGN);
            ext.getStyleClass().add(OUT);
            in.setContent(IN_DESIGN);
            in.getStyleClass().add(IN);
            Group pin = new Group(ext, in);
            pin.getStyleClass().add(PIN);

            //check which pin it is(first, last, or just in the middle)
            if(i == 0) {
                pin.getStyleClass().add(GREEN);
            } else if(i == waypoints.size() - 1) {
                pin.getStyleClass().add(RED);
            } else {
                pin.getStyleClass().add(BLUE);
            }

            //pin gets added to the list
            pins.add(pin);

            //Correct positioning of the new marker
            pin.setLayoutX(property.get().viewX(PointWebMercator.ofPointCh(waypoints.get(i).point())));
            pin.setLayoutY(property.get().viewY(PointWebMercator.ofPointCh(waypoints.get(i).point())));

            int currentPinId = i;
            //Deleting a point
            pin.setOnMouseClicked(e -> {if(e.isStillSincePress()) {
                waypoints.remove(currentPinId);
            }
            });

            //Saving the displacement of a point while it is being moved
            pin.setOnMousePressed(e -> mouseLocation = new Point2D(e.getX(), e.getY()));

            //Setting the new value of the coordinates of the point
            pin.setOnMouseDragged(e -> {
                pin.setLayoutX(pin.getLayoutX() + e.getX() - mouseLocation.getX());
                pin.setLayoutY(pin.getLayoutY() + e.getY() - mouseLocation.getY());
            });

            //Replacing the old point with the new one in the list
            pin.setOnMouseReleased(e -> {if(!e.isStillSincePress()) {
                PointWebMercator point = property.get().pointAt(e.getSceneX(), e.getSceneY());
                PointCh other = point.toPointCh();
                Waypoint waypoint = newWaypoint(other);
                if(waypoint != null && !(waypoints.contains(waypoint))) {
                    waypoints.set(currentPinId, waypoint);
                } else {
                    makePins();
                }
            }
            });
            property.addListener((waypoints) -> makePins());
        }
        pane.getChildren().setAll(pins);
    }
}