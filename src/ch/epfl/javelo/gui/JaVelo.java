package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
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

public final class JaVelo extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        String tileServerHost = "tile.openstreetmap.org";
        //Path cacheBasePath = Path.of("osm-cache");
        //Graph graph = Graph.loadFrom(Path.of("javelo-data"));

        SplitPane splitPane = new SplitPane();
        MenuItem exporterGPX = new MenuItem("Exporter GPX");
        Menu file = new Menu("Fichier");
        MenuBar menuBar = new MenuBar(file);


//        SplitPane.setResizableWithParent();

        BorderPane borderPane = new BorderPane();
        borderPane.getChildren().add(splitPane);
        borderPane.getChildren().add(menuBar);


        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();
    }
}
