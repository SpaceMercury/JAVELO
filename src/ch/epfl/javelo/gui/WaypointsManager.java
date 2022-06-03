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
        waypoints.addListener(this::setSVG);
        parameters.addListener(c -> updatePins());
        for (Waypoint waypoint : waypoints) {
            setPin(waypoint);
        }
        updatePins();
    }

    public Pane pane() {
        return pane;
    }

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

    private void setSVG(ListChangeListener.Change<? extends Waypoint> c) {
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
    }

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

    private void click(MouseEvent mouseEvent, Waypoint waypoint) {
        if (mouseEvent.isStillSincePress()) {
            waypoints.remove(waypoint);
        }
    }

    private void press(MouseEvent mouseEvent) {
        mouseLocation = new SimpleObjectProperty<>(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
    }


    private void drag(MouseEvent mouseEvent, Group pin) {
        pin.setLayoutX(pin.getLayoutX() + mouseEvent.getX() - mouseLocation.getValue().getX());
        pin.setLayoutY(pin.getLayoutY() + mouseEvent.getY() - mouseLocation.getValue().getY());
    }

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