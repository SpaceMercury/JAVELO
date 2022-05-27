package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import com.sun.javafx.geom.Point2D;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public final class BaseMapManager {

    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final TileManager tileManager;
    private static int IMG_SIZE = 256;
    private final WaypointsManager waypointsManager;
    private ObjectProperty<Point2D> point2DObjectProperty;
    private final ObjectProperty<MapViewParameters> mvParameters;
    private final MapViewParameters mapView;

    /**
     *
     * @param tileManager
     * @param waypointsManager
     * @param mapViewParameters
     */
    public BaseMapManager(TileManager tileManager,
                          WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParameters){

        this.waypointsManager = waypointsManager;
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);
        this.tileManager = tileManager;
        mvParameters = mapViewParameters;
        mapView = mvParameters.getValue();

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.widthProperty().bind(pane.heightProperty());
        //pane.getChildren().add(canvas);
//        scrollZoom();
//        moveCursor();
//        clickToAddWaypoint();


        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            System.out.println("test1");
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        redrawOnNextPulse();
        mvParameters.addListener(observable -> redrawOnNextPulse());
        canvas.widthProperty().addListener(observable -> redrawOnNextPulse());
        canvas.heightProperty().addListener(observable -> redrawOnNextPulse());

    }

    /**
     *
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     *
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        redrawOnNextPulse();
        drawTilesOnCanvas();
    }


    /**
     * Draws the Tiles all of the tiles on the canvas
     */
    private void drawTilesOnCanvas() {

        System.out.println("pane . " +pane.getHeight());
        System.out.println("canvas . " +canvas.getHeight());
        int xOffset = (int) (mapView.x() % IMG_SIZE);
        int yOffset = (int) (mapView.y() % IMG_SIZE);
        boolean perfectX = xOffset == 0;
        boolean perfectY = yOffset == 0;

        int lengthNum = xOffset == 0
             ? Math2.ceilDiv((int) canvas.getWidth(), IMG_SIZE)
             : Math2.ceilDiv((int) canvas.getWidth(), IMG_SIZE) + 1;

        int heightNum = yOffset == 0
                ? Math2.ceilDiv((int) canvas.getHeight(), IMG_SIZE)
                : Math2.ceilDiv((int) canvas.getHeight(), IMG_SIZE) + 1;

        for (int i = 0; i < lengthNum; i++) {
            for (int j = 0; j < heightNum; j++) {

                int tileX = Math.floorDiv((int) mapView.x(), IMG_SIZE) + i;
                int tileY = Math.floorDiv((int) mapView.y(), IMG_SIZE) + j;
                Image tileImage = getTileImage(mapView, tileX, tileY);

                double canvasX = perfectX
                        ? IMG_SIZE * i
                        : -(IMG_SIZE - yOffset) + IMG_SIZE * j;
                double canvasY = perfectY
                        ? IMG_SIZE * j
                        : -(IMG_SIZE - yOffset) + IMG_SIZE * j;
                System.out.println(canvasX);
                canvas.getGraphicsContext2D()
                        .drawImage(tileImage, 10, 10);
            }
        }
    }

    /**
     *
     * @param mapView The mapview
     * @param tileX the X coordinate of the tile
     * @param tileY the Y coordinate of the tile
     * @return the Image of that tile
     */
    private Image getTileImage(MapViewParameters mapView, int tileX, int tileY) {
        TileManager.TileId id = new TileManager.TileId(mapView.zoom(), tileX, tileY);
        Image tileImage = null;
        try {
            tileImage = tileManager.imageForTileAt(id);
        } catch (IOException e) {}
        return tileImage;
    }


//    private void scrollZoom(){
//        pane.setOnScroll(scroll -> {
//            int zoomLvl = Math2.clamp(8, mvParameters.getValue().zoom(), 19);
//      /      mvParameters.set( zoomLvl, (int) scroll.getDeltaY(), mapView.x(), mapView.y()))
//        });
//    }
//    private void moveCursor(){
//        MouseEvent drag = new MouseEvent();
//        pane.setOnMouseClicked(hold ->  {
//            point2DObjectProperty.setValue(hold.getX() ,hold.getY()));
//            drag.
//        };
//        while(drag.isStillSincePress()){
//
//        }
//    }
//    private void clickToAddWaypoint(){
//        pane.setOnMouseClicked(click -> waypointsManager.addWaypopint( );
//    }

    /**
     * Getter for the pane
     * @return the pane
     */
    public Pane pane(){
        return pane;
    }



}
