
package ch.epfl.javelo.gui;


import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * @author fuentes
 */
public final class JaVelo extends Application {

    /**
     * Main start function
     * @param args
     */
    public static void main(String[] args) { launch(args); }


    @Override
    public void start(Stage primaryStage) throws Exception {

        //Set the title of the Application as well as some parameters
        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        //Create instances of all the necessary classes
        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        CityBikeCF costFunction = new CityBikeCF(graph);
        RouteComputer computer = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(computer);
        String tileServerHost = "tile.openstreetmap.org";
        Path cacheBasePath = Path.of("osm-cache");
        ErrorManager errorManager = new ErrorManager();
        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);
        AnnotatedMapManager annotatedMap = new AnnotatedMapManager(graph, tileManager, routeBean,
                e -> errorManager.displayError("Aucune route a proximite"));

        //Create the splitPane and setting it correctly
        SplitPane splitPane = new SplitPane(annotatedMap.pane());
        splitPane.orientationProperty().setValue(Orientation.VERTICAL);

        //Adding the Route if it is not null
        routeBean.getElevationProfileProperty().addListener( (p, ov, nv) -> {

            if(splitPane.getItems().size() == 2){
                splitPane.getItems().remove(1);
                splitPane.getItems().add(new ElevationProfileManager(routeBean.getElevationProfileProperty(), new SimpleDoubleProperty()).pane());

            }

            if(routeBean.getRoute() != null) {
                ElevationProfileManager elevationProfileManager = new ElevationProfileManager(routeBean.getElevationProfileProperty(), new SimpleDoubleProperty());
                splitPane.getItems().add(elevationProfileManager.pane());
                SplitPane.setResizableWithParent( elevationProfileManager.pane(),false);
            }
        });

        //Create the stackPane with the SplitPane and the ErrorManager on top
        StackPane stack = new StackPane(splitPane, errorManager.pane());

        //Menu Bar up top
        MenuItem exporterGPX = new MenuItem("Exporter GPX");
        Menu file = new Menu("Fichier");
        file.getItems().add(exporterGPX);
        MenuBar menuBar = new MenuBar(file);

        //Creating the borderPane and putting the stackedPane and the MenuBar together
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(stack);

        //Set scene and show
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();
    }
}
