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
    private final HashMap<Waypoint, Group> pinToWaypoint = new LinkedHashMap<>();
    private final ObjectProperty<Point2D> mouseLocation;

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
        this.mouseLocation = new SimpleObjectProperty<>();
        this.pane = new Pane();
        this.pane.setPickOnBounds(false);
        waypoints.addListener(this::setPins);
        property.addListener(c -> updatePins());


        /**
        for(Waypoint waypoint : waypoints) {
            SVGPath ext = new SVGPath();
            SVGPath in = new SVGPath();
            ext.setContent(OUT_DESIGN);
            ext.getStyleClass().add(OUT);
            in.setContent(IN_DESIGN);
            in.getStyleClass().add(IN);
            Group pin = new Group(ext, in);
            System.out.println(pin == null);
            pin.getStyleClass().add(PIN);
            pinToWaypoint.put(waypoint, pin);
            pin.setOnMouseClicked(e -> {if(e.isStillSincePress()) {
                waypoints.remove(waypoint);
            }
            });
            pin.setOnMouseDragged(e -> {
                pin.setLayoutX(pin.getLayoutX() + e.getX() - mouseLocation.getX());
                pin.setLayoutY(pin.getLayoutY() + e.getY() - mouseLocation.getY());
            });
            pin.setOnMouseReleased(e -> {if(!e.isStillSincePress()) {
                PointWebMercator pointWM = property.get().pointAt(e.getX(), e.getY());
                PointCh pointCh = pointWM.toPointCh();
                Waypoint point = newWaypoint(pointCh);
                if(point != null && !waypoints.contains(point)){
                    pane.getChildren().remove(pinToWaypoint.remove(waypoint));
                    waypoints.set(waypoints.indexOf(waypoint), point);
                } else {
                    updatePins();
                }
            }
            });
            pin.setOnMousePressed(this::press);
            pane.getChildren().add(pin);
        }
        updatePins();
         */
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
        System.out.println("Waypoint is called");
        PointWebMercator pointWM = property.get().pointAt(x, y);
        PointCh point = pointWM.toPointCh();
        Waypoint waypoint = newWaypoint(point);
        if(waypoint != null && !waypoints.contains(waypoint)) {
                System.out.println("waypoint is added");
                waypoints.add(waypoint);
        } else if(waypoints.contains(waypoint)) {
            System.out.println("Waypoint is deleted");
            waypoints.remove(waypoint);
        }
    }

    /**
     * Method that will turn a PointCh into a point (if it is possible)
     * @param point the PointCh that is to be turned into a point
     * @return a new point, or null if it could not be made
     */
    private Waypoint newWaypoint(PointCh point) {
        System.out.println("new waypoint is called");
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

    private void setPins(ListChangeListener.Change<? extends Waypoint> c) {
        while(c.next()) {
            if(c.wasRemoved()) {
                for(Waypoint waypoint : c.getRemoved()) {
                    pane.getChildren().remove(pinToWaypoint.remove(waypoint));
                }
            }
            if(c.wasAdded()) {
                System.out.println("trying to add");
                for(Waypoint waypoint : c.getAddedSubList()) {
                    SVGPath ext = new SVGPath();
                    ext.setContent(OUT_DESIGN);
                    ext.getStyleClass().add(OUT);
                    SVGPath in = new SVGPath();
                    in.setContent(IN_DESIGN);
                    in.getStyleClass().add(IN);
                    Group pin = new Group(ext, in);
                    System.out.println(pin == null);
                    pin.getStyleClass().add(PIN_NAME);
                    pin.getStyleClass().add(GREEN);
                    PointWebMercator p = PointWebMercator.of(property.get().zoom(),
                            WebMercator.x(graph.nodePoint(waypoint.nodeId()).lon()),
                            WebMercator.y(graph.nodePoint(waypoint.nodeId()).lat()));
                    pin.setLayoutX(p.x());
                    System.out.println("x: " + p.x());
                    System.out.println("x of pin: " + pin.getLayoutX());
                    pin.setLayoutY(p.y());
                    System.out.println("y: " + p.y());
                    System.out.println("y of pin: " + pin.getLayoutY());
                    pinToWaypoint.put(waypoint, pin);
                    pin.getStyleClass().add(GREEN);
                    pin.setOnMouseClicked(e -> {if(e.isStillSincePress()) {
                        System.out.println("click");
                        waypoints.remove(waypoint);
                    }
                    });
                    pin.setOnMouseDragged(e -> {
                        System.out.println("dragged");
                        pin.setLayoutX(pin.getLayoutX() + e.getSceneX() - mouseLocation.get().getX());
                        pin.setLayoutY(pin.getLayoutY() + e.getSceneY() - mouseLocation.get().getY());
                    });
//                    pin.setOnMouseReleased(e -> {if(!e.isStillSincePress()) {
//                        System.out.println("released");
//                        PointWebMercator pointWM = property.get().pointAt(e.getX(), e.getY());
//                        PointCh pointCh = pointWM.toPointCh();
//                        Waypoint point = newWaypoint(pointCh);
//                        if(point != null && !waypoints.contains(point)){
//                            pane.getChildren().remove(pinToWaypoint.remove(waypoint));
//                            waypoints.set(waypoints.indexOf(waypoint), point);
//
//                        } else {
//                            updatePins();
//                        }
//                    }
//                    });
                    pin.setOnMousePressed(this::press);
                    pane.getChildren().add(pin);
                    Rectangle rect = new Rectangle(10, 10, Color.RED);
                    rect.setLayoutX(10*waypoints.indexOf(waypoint));
                    rect.setLayoutY(10*waypoints.indexOf(waypoint));
                    System.out.println("x of rect: " + rect.getLayoutX());
                    System.out.println("y of rect: " + rect.getLayoutY());
                    pane.getChildren().add(rect);
                }
            }
        }
        //updatePins();
    }



    private void press(MouseEvent e) {
        mouseLocation.set(new Point2D(e.getX(), e.getY()));
    }

    private void updatePins() {
        for(Waypoint waypoint : waypoints) {
            PointWebMercator point = PointWebMercator.of(property.get().zoom(),
                    WebMercator.x(graph.nodePoint(waypoint.nodeId()).lon()),
                    WebMercator.y(graph.nodePoint(waypoint.nodeId()).lat()));
            MapViewParameters parameters = property.get();
            Group pin = pinToWaypoint.get(waypoint);
            System.out.println("in update");
            System.out.println(pin == null);
            pin.setLayoutX(parameters.viewX(point));
            pin.setLayoutY(parameters.viewY(point));
            if(waypoints.indexOf(waypoint) == 0) {
                pin.getStyleClass().add(GREEN);
            } else if(waypoints.indexOf(waypoint) == waypoints.size() - 1) {
                pin.getStyleClass().add(RED);
            } else {
                pin.getStyleClass().add(BLUE);
            }
        }
    }
}
