package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.MapViewParameters;
import ch.epfl.javelo.gui.Waypoint;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.HashMap;
import java.util.function.Consumer;

public final class WaypointsManager {
    private final static String PIN_PATH = "pin";
    private final static String OUT_C_PATH = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String OUT_PATH = "pin_outside";
    private final static String IN_C_PATH = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final static String IN_PATH = "pin_inside";
    private final static String FIRST_PATH = "first";
    private final static String MID_PATH = "middle";
    private final static String LAST_PATH = "last";
    private final static String ERROR = "Aucune route à proximité !";

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> parameters;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorConsumer;
    private final Pane pane;

    private final HashMap<Waypoint, Group> groupMap = new HashMap<>();
    private Point2D mousePoint;

    public WaypointsManager(Graph graph,
                            ObjectProperty<MapViewParameters> parameters,
                            ObservableList<Waypoint> waypoints,
                            Consumer<String> errorConsumer) {

        this.graph = graph;
        this.parameters = parameters;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;

        this.pane = new Pane();
        pane.setPickOnBounds(false);

        waypoints.addListener(this::setSVG);
        parameters.addListener(c -> updateSVG());

        for (Waypoint waypoint : waypoints) {
            addedWaypoint(waypoint);
        }
        updateSVG();
    }

    public Pane pane() {
        return pane;
    }

    public void addWaypoint(double x, double y) {
        Waypoint point = createWaypoint(x, y);

        if (point == null) {
            return;
        }
        if (waypoints.contains(point)) {
            return;
        }

        waypoints.add(point);
    }

    private Waypoint createWaypoint(double x, double y) {
        PointWebMercator pointWM = parameters.get().pointAt(x, y);

        PointCh pointCh = pointWM.toPointCh();
        if (pointCh == null) {
            errorConsumer.accept(ERROR);
            return null;
        }
        int nodeId = graph.nodeClosestTo(pointCh, 500);
        if (nodeId == -1) {
            errorConsumer.accept(ERROR);
            return null;
        }
        return new Waypoint(graph.nodePoint(nodeId), nodeId);
    }

    private void setSVG(ListChangeListener.Change<? extends Waypoint> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                for (Waypoint waypoint : c.getAddedSubList()) {
                    addedWaypoint(waypoint);
                }
            }
            if (c.wasRemoved()) {
                for (Waypoint waypoint : c.getRemoved()) {
                    removedWaypoint(waypoint);
                }
            }
        }
        updateSVG();
    }

    private void addedWaypoint(Waypoint waypoint) {
        SVGPath outline = new SVGPath();
        outline.setContent(OUT_C_PATH);
        outline.getStyleClass().add(OUT_PATH);

        SVGPath circle = new SVGPath();
        circle.setContent(IN_C_PATH);
        circle.getStyleClass().add(IN_PATH);

        Group pin = new Group(outline, circle);
        pin.getStyleClass().add(PIN_PATH);
        pin.setOnMouseClicked(event -> MouseClickManager(event, waypoint));
        pin.setOnMouseDragged(e -> MouseDragManager(e, pin));
        pin.setOnMouseReleased(e -> MouseReleaseManager(e, pin, waypoint));
        pin.setOnMousePressed(this::MousePressManager);

        pane.getChildren().add(pin);
        groupMap.put(waypoint, pin);
    }


    private void removedWaypoint(Waypoint waypoint) {
        pane.getChildren().remove(groupMap.remove(waypoint));
    }


    private void updateSVG() {
        for (Waypoint waypoint : waypoints) {
            Group pin = groupMap.get(waypoint);

            pin.getStyleClass().remove(FIRST_PATH);
            pin.getStyleClass().remove(MID_PATH);
            pin.getStyleClass().remove(LAST_PATH);
            PointWebMercator pinPoint = PointWebMercator.ofPointCh(graph.nodePoint(waypoint.nodeId()));
            MapViewParameters param = parameters.get();

            pin.setLayoutX(param.viewX(pinPoint));
            pin.setLayoutY(param.viewY(pinPoint));

            int last = waypoints.size() - 1;
            int index = waypoints.indexOf(waypoint);

            if (index == 0) {
                pin.getStyleClass().add(FIRST_PATH);
            } else if (index == last) {
                pin.getStyleClass().add(LAST_PATH);
            } else {
                pin.getStyleClass().add(MID_PATH);
            }
        }
    }

    private void MouseClickManager(MouseEvent mouseEvent, Waypoint waypoint) {
        if (mouseEvent.isStillSincePress()) {
            waypoints.remove(waypoint);
        }
    }

    private void MousePressManager(MouseEvent mouseEvent) {
        mousePoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
    }


    private void MouseDragManager(MouseEvent mouseEvent, Group pin) {
        pin.setLayoutX(pin.getLayoutX() + mouseEvent.getX() - mousePoint.getX());
        pin.setLayoutY(pin.getLayoutY() + mouseEvent.getY() - mousePoint.getY());
    }

    private void MouseReleaseManager(MouseEvent mouseEvent, Group pin, Waypoint waypoint) {
        if (!mouseEvent.isStillSincePress()) {
            int index = waypoints.indexOf(waypoint);
            Waypoint newWaypoint = createWaypoint(pin.getLayoutX(), pin.getLayoutY());
            if (newWaypoint != null && !waypoints.contains(newWaypoint)) {

                removedWaypoint(waypoint);
                waypoints.remove(index);

                waypoints.add(index, newWaypoint);
            } else {
                updateSVG();
            }
        }
    }
    //TODO: (SUPER IMPORTANT!!!!!)this class needs to be rewritten, but it works
}