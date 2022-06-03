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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @author ventura
 */
public final class WaypointsManager {
    //String constants used in the class
    private static final double SEARCH_DISTANCE = 500;
    private static final String ERROR = "Aucune route à proximité !";
    private static final String PIN = "pin";
    private static final String OUT = "pin_outside";
    private static final String IN = "pin_inside";
    private static final String OUT_DESIGN = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private static final String IN_DESIGN = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private static final String GREEN = "first";
    private static final String BLUE = "middle";
    private static final String RED = "last";

    //All the attributes used in the class
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> property;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorConsumer;
    private final Pane pane;
    private final HashMap<Waypoint, Group> pins;
    private ObjectProperty<Point2D> mouseLocation;

    /**
     * Public constructor for WaypointsManager
     * @param graph The graph it takes
     * @param parameters The mapview parameters
     * @param waypoints The list of waypoints given to it
     * @param errorConsumer The error consumer
     */
    public WaypointsManager(Graph graph,
                            ObjectProperty<MapViewParameters> parameters,
                            ObservableList<Waypoint> waypoints,
                            Consumer<String> errorConsumer) {

        this.graph = graph;
        this.property = parameters;
        this.waypoints = waypoints;
        this.pins = new HashMap<>();
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();
        pane.setPickOnBounds(false);
        waypoints.addListener((ListChangeListener<? super Waypoint> ) c -> {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (Waypoint waypoint : c.getAddedSubList()) {
                            setPin(waypoint);
                        }
                    }
                    if (c.wasRemoved()) {
                        for (Waypoint waypoint : c.getRemoved()) {
                            pane.getChildren().remove(pins.remove(waypoint));
                        }
                    }
                }
                updatePins();
        });
        parameters.addListener(c -> updatePins());
        for (Waypoint waypoint : waypoints) {
            setPin(waypoint);
        }
        updatePins();
    }

    /**
     * Method that returns the pane
     * @return pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Public method that adds a Waypoint to the waypoint list
     * @param x the xcoord of the new waypoint
     * @param y the ycoord of the new waypoint
     */
    public void addWaypoint(double x, double y) {
        PointWebMercator pointWM = property.get().pointAt(x, y);
        PointCh pointCh = pointWM.toPointCh();
        if (pointCh == null || (graph.nodeClosestTo(pointCh, SEARCH_DISTANCE) == -1)) {
            errorConsumer.accept(ERROR);
            return;
        }
        waypoints.add(new Waypoint(graph.nodePoint(graph.nodeClosestTo(pointCh, SEARCH_DISTANCE)),
                graph.nodeClosestTo(pointCh, SEARCH_DISTANCE)));
    }

    /**
     * Private method that takes care of updating the Styleclass of a pin
     * @param waypoint The waypoint whose pin is to be updated
     */
    private void setPin(Waypoint waypoint) {
        SVGPath outline = new SVGPath();
        SVGPath circle = new SVGPath();
        outline.setContent(OUT_DESIGN);
        outline.getStyleClass().add(OUT);
        circle.setContent(IN_DESIGN);
        circle.getStyleClass().add(IN);
        Group pin = new Group(outline, circle);
        pin.getStyleClass().add(PIN);
        pin.setOnMouseClicked(event -> click(event, waypoint));
        pin.setOnMouseDragged(e -> drag(e, pin));
        pin.setOnMouseReleased(e -> release(e, waypoint, pin));
        pin.setOnMousePressed(this::press);
        pane.getChildren().add(pin);
        pins.put(waypoint, pin);
    }

    /**
     * Private method that updates all the pins in the list
     * changing their design depending on their new position
     */
    private void updatePins() {
        for (Waypoint waypoint : waypoints) {
            Group pin = pins.get(waypoint);
            PointWebMercator pinPoint = PointWebMercator.ofPointCh(graph.nodePoint(waypoint.nodeId()));
            MapViewParameters param = property.get();
            pin.setLayoutX(param.viewX(pinPoint));
            pin.setLayoutY(param.viewY(pinPoint));
            int id = waypoints.indexOf(waypoint);
            pin.getStyleClass().remove(GREEN);
            pin.getStyleClass().remove(BLUE);
            pin.getStyleClass().remove(RED);
            if (id == 0) {
                pin.getStyleClass().add(GREEN);
            } else if (id == waypoints.size() - 1) {
                pin.getStyleClass().add(RED);
            } else {
                pin.getStyleClass().add(BLUE);
            }
        }
    }

    /**
     * Private method that removes a waypoint if it is clicked on
     * @param mouseEvent The click
     * @param waypoint The waypoint that is to be removed
     */
    private void click(MouseEvent mouseEvent, Waypoint waypoint) {
        if (mouseEvent.isStillSincePress()) {
            waypoints.remove(waypoint);
        }
    }

    /**
     * Private method that updates the mouse location when
     * the mouse button is pressed
     * @param mouseEvent
     */
    private void press(MouseEvent mouseEvent) {
        mouseLocation = new SimpleObjectProperty<>(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
    }

    /**
     * Private method that updates a pins placement while it is being dragged
     * @param mouseEvent the drag
     * @param pin the pin
     */
    private void drag(MouseEvent mouseEvent, Group pin) {
        pin.setLayoutX(pin.getLayoutX() + mouseEvent.getX() - mouseLocation.getValue().getX());
        pin.setLayoutY(pin.getLayoutY() + mouseEvent.getY() - mouseLocation.getValue().getY());
    }

    /**
     * Private method that updates the location of the waypoint of a pin
     * after it has been replaced by dragging
     * @param mouseEvent the release of the drag
     * @param waypoint the waypoint to be updated
     * @param pin the waypoints pin
     */
    private void release(MouseEvent mouseEvent, Waypoint waypoint, Group pin) {
        if (!mouseEvent.isStillSincePress()) {
            int id = waypoints.indexOf(waypoint);
            PointWebMercator pointWM = property.get().pointAt(pin.getLayoutX(), pin.getLayoutY());
            PointCh pointCh = pointWM.toPointCh();
            Waypoint newWaypoint = null;
            if (pointCh == null || (graph.nodeClosestTo(pointCh, SEARCH_DISTANCE) == -1)) {
                errorConsumer.accept(ERROR);
            } else {
                newWaypoint = new Waypoint(graph.nodePoint(graph.nodeClosestTo(pointCh, SEARCH_DISTANCE)),
                        graph.nodeClosestTo(pointCh, SEARCH_DISTANCE));
            }
            if (newWaypoint != null && !waypoints.contains(newWaypoint)) {
                pane.getChildren().remove(pins.remove(waypoint));
                waypoints.set(id, newWaypoint);
            } else {
                updatePins();
            }
        }
    }
}