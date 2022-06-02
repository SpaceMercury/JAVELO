package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public final class AnnotatedMapManager {


    public static final int START_ZOOM = 12;
    public static final int START_X = 543200;
    public static final int START_Y = 370650;
    private Pane pane;
    private BaseMapManager baseMap;
    private WaypointsManager waypointsManager;
    private RouteManager routeManager;
    private ObjectProperty<MapViewParameters> mvParameters;
    private final DoubleProperty mouse;
    private Point2D mouse2D;


    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean bean, Consumer<String> errorConsumer){

        mvParameters = new SimpleObjectProperty<>();
        mouse2D = new Point2D(0,0);
        mvParameters.setValue(new MapViewParameters(START_ZOOM, START_X, START_Y));
        routeManager = new RouteManager(bean,mvParameters , errorConsumer);
        waypointsManager = new WaypointsManager(graph,mvParameters , bean.getWaypoints(), errorConsumer);
        baseMap = new BaseMapManager(tileManager,waypointsManager, mvParameters);
        mouse = new SimpleDoubleProperty();
        pane = new StackPane(baseMap.pane(), waypointsManager.pane(), routeManager.pane());
        pane.getStylesheets().add("map.css");

        mousePos();

    }


    private void mousePos(){

        routeManager.pane().setOnMouseExited((move) -> {
            mouse2D.add(move.getX(), move.getY());
        });
        routeManager.pane().setOnMouseMoved((move) -> {
            mouse2D.add(move.getX(), move.getY());
        });

    }


    public Pane pane(){
        return pane;
    }

    public ReadOnlyDoubleProperty mousePositionOnRouteProperty(){
        return mouse;
    }
}
