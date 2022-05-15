package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import com.sun.javafx.geom.Point2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class WaypointsManager {
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
    }

    public Pane pane() {
        return null;
    }
    public void addWaypoint(double x, double y) {

    }

    private void setPins() {
        List<Node> pins = new ArrayList<>();
        pane.setPickOnBounds(false);
        for(int i = 0; i < pins.size(); i++) {

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

            //correct positioning of the new marker
            pin.setLayoutX(myProperty.get().viewX(PointWebMercator.ofPointCh(waypoints.get(i).waypoint())));
            pin.setLayoutY(myProperty.get().viewY(PointWebMercator.ofPointCh(waypoints.get(i).waypoint())));

            //Managing of possible events
            //1. Adding
            pane.setOnMouseClicked(click -> { if(click.isStillSincePress()){
                addWaypoint(click.getX(), click.getY());
                waypoints.notifyAll();
                setPins();
            }
            });

            //2. Removing
            int currentPinIndex = i;
            pin.setOnMouseClicked(click -> { if(click.isStillSincePress()) {
                waypoints.remove(currentPinIndex);
                setPins();
            }
            });

            //3. Moving
            ObjectProperty<Point2D> movement = new SimpleObjectProperty<>();
            pin.setOnMousePressed(hold -> { if(!hold.isStillSincePress()) {
                movement.setValue(new Point2D((float) hold.getX(),(float) hold.getY()));
            }
            });
            pin.setOnMouseDragged(drag -> {
                pin.setLayoutX(drag.getSceneX() - movement.get().x);
                pin.setLayoutY(drag.getSceneY() - movement.get().y);
            });

            //TODO: check what this does specifically
            pin.setOnMouseReleased(release -> { if(!release.isStillSincePress()) {
                PointWebMercator point = myProperty.get().pointAt(release.getSceneX(),
                        release.getSceneY());
            }
            });
        }
    }
}
