package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;


public final class BaseMapManager {


    private Canvas canvas;
    private Pane pane;
    private boolean redrawNeeded;
    private TileManager tileManager;
    private static int IMG_SIZE = 256;

    ObjectProperty<MapViewParameters> mvParameters;


    public BaseMapManager(TileManager tileManager, ObjectProperty<MapViewParameters> mapViewParameters){

        this.pane = pane;
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

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        draw();
    }


    private void draw() {

        MapViewParameters mapView = mvParameters.getValue();
        int lengthNum = mapView.x() % IMG_SIZE == 0
             ? Math2.ceilDiv((int) canvas.getWidth(), IMG_SIZE)
             : Math2.ceilDiv((int) canvas.getWidth(), IMG_SIZE) + 1;

        int heightNum = mapView.y() % IMG_SIZE == 0
                ? Math2.ceilDiv((int) canvas.getHeight(), IMG_SIZE)
                : Math2.ceilDiv((int) canvas.getHeight(), IMG_SIZE) + 1;


        drawTiles(mapView, lengthNum, heightNum);

    }

    private void drawTiles(MapViewParameters mapView, int lengthNum, int heightNum) {
        for (int i = 0; i < lengthNum; i++) {
            for (int j = 0; j < heightNum; j++) {

                int tileX = Math.floorDiv(mapView.x(),IMG_SIZE) + i;
                int tileY = Math.floorDiv(mapView.y(),IMG_SIZE) + j;

                TileManager.TileId id = new TileManager.TileId(mapView.zoom(), tileX, tileY);
                Image tileImage = null;
                try {
                    tileImage = tileManager.imageForTileAt(id);
                } catch (IOException e) {
                }

                double canvasX = mapView.topLeftPixel().getX()+ i*IMG_SIZE;
                double canvasY = mapView.topLeftPixel().getY()+ j*IMG_SIZE;

                canvas.getGraphicsContext2D().drawImage(tileImage,canvasX,canvasY);
            }
        }
    }


    public Pane pane(){
        return pane;
    }

}
