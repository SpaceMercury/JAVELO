package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;


public final class BaseMapManager {


    private Canvas canvas;
    private Pane pane;
    private boolean redrawNeeded;
    private TileManager tileManager;
    private static int IMG_SIZE = 256;
    private WaypointsManager waypointsManager;


    ObjectProperty<MapViewParameters> mvParameters;
    MapViewParameters mapView = mvParameters.getValue();


    /**
     *
     * @param tileManager
     * @param waypointsManager
     * @param mapViewParameters
     */
    public BaseMapManager(TileManager tileManager,
                          WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParameters){

        this.pane = new Pane();
        this.waypointsManager = waypointsManager;
        canvas = new Canvas();
        this.tileManager = tileManager;
        mvParameters = mapViewParameters;
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.widthProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

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


        int xOffset = mapView.x() % IMG_SIZE;
        int yOffset = mapView.y() % IMG_SIZE;
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

                int tileX = Math.floorDiv(mapView.x(),IMG_SIZE) + i;
                int tileY = Math.floorDiv(mapView.y(),IMG_SIZE) + j;
                Image tileImage = getTileImage(mapView, tileX, tileY);

                double canvasX = perfectX
                    ? IMG_SIZE * i
                    : - (IMG_SIZE-yOffset) + IMG_SIZE * j;
                double canvasY = perfectY
                        ? IMG_SIZE * j
                        : - (IMG_SIZE-yOffset) + IMG_SIZE * j;

                canvas.getGraphicsContext2D()
                        .drawImage(tileImage,canvasX,canvasY);
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



    // @TODO need help for the event manager
    private void scrollZoom(){
        canvas.setOnScroll(scroll -> mvParameters.setValue(new MapViewParameters(
                mapView.zoom() + (int) scroll.getDeltaY(),
                mapView.x(), mapView.y())));
    }


    /**
     * Getter for the pane
     * @return the pane
     */
    public Pane pane(){
        return pane;

    }




}
