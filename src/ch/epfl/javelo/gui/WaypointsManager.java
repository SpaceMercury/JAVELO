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

public final class WaypointsManager {
    private static final int SEARCH_RADIUS = 500;
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> myProperty;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorConsumer;
    private final Pane pane;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> myProperty,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.myProperty = myProperty;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();
        makePins();
        myProperty.addListener((o, oV, nV) -> makePins());
        waypoints.addListener((ListChangeListener<? super Waypoint>) c -> makePins());
    }

    public Pane pane() {
        return pane;
    }
    public void addWaypoint(double x, double y) {
        PointWebMercator point = myProperty.get().pointAt(x, y);
        PointCh current = point.toPointCh();
        int currentNodeId = graph.nodeClosestTo(current, 500);
        if(currentNodeId != -1) {
            Waypoint newWaypoint = new Waypoint(current, currentNodeId);
            waypoints.add(newWaypoint);
        }
        else {
            errorConsumer.accept("Aucune route à proximité");
        }
    }

    private void makePins() {
        List<Node> pins = new ArrayList<>();
        pane.setPickOnBounds(false);
        for(int i = 0; i < waypoints.size(); i++) {
            //creation of a new pin
            SVGPath pathExt = new SVGPath();
            SVGPath pathInt = new SVGPath();
            pathExt.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            pathExt.getStyleClass().add("pin_outside");
            pathInt.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
            pathInt.getStyleClass().add("pin_inside");
            Group pin = new Group(pathExt, pathInt);
            pin.getStyleClass().add("pin");

            //pin gets added to the list
            pins.add(pin);

            //check which pin it is(first, last, or just in the middle)
            if(i == 0) {
                pin.getStyleClass().add("first");
            } else if(i == pins.size() - 1) {
                pin.getStyleClass().add("last");
            } else {
                pin.getStyleClass().add("middle");
            }

            //Correct positioning of the new marker
            pin.setLayoutX(myProperty.get().viewX(PointWebMercator.ofPointCh(waypoints.get(i).waypoint())));
            pin.setLayoutY(myProperty.get().viewY(PointWebMercator.ofPointCh(waypoints.get(i).waypoint())));

            int currentPinId = i;
            //Deleting a waypoint
            pin.setOnMouseClicked(e -> {if(e.isStillSincePress()) {
                waypoints.remove(currentPinId);
                makePins();
            }
            });

            //Saving the displacement of a waypoint while it is being moved
            ObjectProperty<Point2D> displacement = new SimpleObjectProperty<>();
            pin.setOnMousePressed(e -> {if(!e.isStillSincePress()) {
                displacement.set(new Point2D(e.getX(), e.getY()));
            }
            });

            //Setting the new value of the coordinates of the waypoint
            pin.setOnMouseDragged(e -> {
                pin.setLayoutX(e.getSceneX() - displacement.get().getX());
                pin.setLayoutY(e.getSceneY() - displacement.get().getY());
            });

            //Replacing the old waypoint with the new one in the list
            pin.setOnMouseReleased(e -> {if(!e.isStillSincePress()) {
                PointWebMercator point = myProperty.get().pointAt(e.getSceneX(), e.getSceneY());
                PointCh other = point.toPointCh();
                int currentNode = graph.nodeClosestTo(other, SEARCH_RADIUS);
                if(currentNode != -1) {
                    Waypoint waypoint = new Waypoint(other, currentNode);
                    waypoints.set(currentPinId, waypoint);
                    makePins();
                } else {
                    errorConsumer.accept("Aucune route à proximité");
                }
            }
            });
            myProperty.addListener((waypoints) -> makePins());
        }
        pane.getChildren().setAll(pins);
    }
}