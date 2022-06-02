
package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        CityBikeCF costFunction = new CityBikeCF(graph);
        RouteComputer computer = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(computer);

        String tileServerHost = "tile.openstreetmap.org";
        Path cacheBasePath = Path.of("File2");
        ErrorManager errorManager = new ErrorManager();

        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);

        AnnotatedMapManager annotatedMap = new AnnotatedMapManager(graph, tileManager, routeBean, e -> errorManager.displayError("erreur"));
        SplitPane splitPane = new SplitPane(annotatedMap.pane());
        MenuItem exporterGPX = new MenuItem("Exporter GPX");
        Menu file = new Menu("Fichier");
        file.getItems().add(exporterGPX);
        MenuBar menuBar = new MenuBar(file);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(splitPane);

        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();
    }
}
