package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;


import java.util.function.Consumer;

/**
 * @author fuentes
 */
public final class AnnotatedMapManager {


    public static final int START_ZOOM = 12;
    public static final int START_X = 543200;
    public static final int START_Y = 370650;
    private Pane pane;
    private BaseMapManager baseMap;
    private WaypointsManager waypointsManager;
    private RouteManager routeManager;
    private RouteBean rBean;
    private ObjectProperty<MapViewParameters> mvParameters;
    private final DoubleProperty mouse;
    private ObjectProperty<Point2D> mouse2D;


    /**
     * Public constructor of AnnotatedMapManager
     * @param graph the Graph of the nodes
     * @param tileManager the TileManager retrieving the tiles
     * @param bean the Bean giving us the properties
     * @param errorConsumer the Error Consumer
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean bean, Consumer<String> errorConsumer){

        rBean = bean;
        mvParameters = new SimpleObjectProperty<>();
        mouse2D = new SimpleObjectProperty<>(Point2D.ZERO);
        mvParameters.setValue(new MapViewParameters(START_ZOOM, START_X, START_Y));
        routeManager = new RouteManager(bean, mvParameters, errorConsumer);
        waypointsManager = new WaypointsManager(graph,mvParameters , bean.getWaypoints(), errorConsumer);
        baseMap = new BaseMapManager(tileManager,waypointsManager, mvParameters);
        mouse = new SimpleDoubleProperty();
        pane = new StackPane(baseMap.pane(), waypointsManager.pane(), routeManager.pane());
        pane.getStylesheets().add("map.css");

        mousePos();
//        mouseBindings();
    }


    /**
     * Events for the mouse2D coordinates in Point2D
     */
    private void mousePos(){

        pane.setOnMouseMoved((move) -> {
            mouse2D.set(new Point2D(move.getX(), move.getY()));
        });

        pane.setOnMouseExited((move) -> {
            mouse2D.set(null);

        });

    }


//    private void mouseBindings(){
//
//        mouse2D.bind(Bindings.createDoubleBinding( () -> {
//            pointWebMercator = calculClosePoint();
//
//            double distanceToNode = Math2.norm(mvParameters.get().viewX(  )-mouse2D.get().getX(),
//                    mvParameters.get().viewY(pointWebMercator)-mouse2D.get().getY());
//
//            if(distanceToNode > 15){
//                position = Double.NaN;
//
//            }
//            else{
//                position = Double.NaN;
//            }
//            return position;
//        }, mouse2D, rBean.getRouteProperty(), mvParameters));
//
//    }


    /**
     * Getter for the pane
     * @return the Pane
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Getter for the mouseProperty
     * @return the position of the mouse in a ReadOnly Property
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty(){
        return mouse;
    }
}

