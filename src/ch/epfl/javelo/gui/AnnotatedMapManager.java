package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public final class AnnotatedMapManager {


    private Pane pane;
    private BaseMapManager baseMap;
    private WaypointsManager waypointsManager;
    private RouteManager routeManager;
    private ObjectProperty<MapViewParameters> mvParameters;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean bean, Consumer<String> errorConsumer){

        mvParameters = new SimpleObjectProperty<>();
        routeManager = new RouteManager(bean,mvParameters , errorConsumer);
        waypointsManager = new WaypointsManager(graph,mvParameters , bean.getWaypoints(), errorConsumer);
        baseMap = new BaseMapManager(tileManager,waypointsManager, mvParameters);


        pane = new StackPane(baseMap.pane(), waypointsManager.pane(), routeManager.pane());

    }

    public Pane pane(){

        return pane;
    }

    public ObjectProperty<MouseEvent> mousePositionOnRouteProperty(){

        return null;
    }
}
