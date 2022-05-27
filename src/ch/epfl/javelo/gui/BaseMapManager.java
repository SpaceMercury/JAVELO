package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;



public final class BaseMapManager {

    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final TileManager tileManager;
    private static int IMG_SIZE = 256;
    private final WaypointsManager waypointsManager;
    private Point2D mousePoint2D;
    private final ObjectProperty<MapViewParameters> mvParameters;

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
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        //Initialise the mouse at 0, 0
        mousePoint2D = new Point2D.Double(0,0);

        scrollZoom();
        moveCursor();
        clickToAddWaypoint();


        redrawOnNextPulse();


        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        //mvParameters.addListener(observable -> redrawOnNextPulse());
        //canvas.widthProperty().addListener(observable -> redrawOnNextPulse());
        //canvas.heightProperty().addListener(observable -> redrawOnNextPulse());



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

        int xOffset = (int) (mvParameters.getValue().x() % IMG_SIZE);
        int yOffset = (int) (mvParameters.getValue().y() % IMG_SIZE);
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

                int tileX = Math.floorDiv((int) mvParameters.getValue().x(), IMG_SIZE) + i;
                int tileY = Math.floorDiv((int) mvParameters.getValue().y(), IMG_SIZE) + j;
                Image tileImage = getTileImage(mvParameters.getValue(), tileX, tileY);

                double canvasX = perfectX
                        ? IMG_SIZE * i
                        : -(IMG_SIZE - xOffset) + IMG_SIZE * i;
                double canvasY = perfectY
                        ? IMG_SIZE * j
                        : -(IMG_SIZE - yOffset) + IMG_SIZE * j;
                canvas.getGraphicsContext2D()
                        .drawImage(tileImage, canvasX, canvasY);
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

    private void setMouseScroll(ScrollEvent s){
        mousePoint2D.setLocation(s.getX(), s.getY());
    }

    private void scrollZoom(){
        pane.setOnScroll(scroll -> {
            setMouseScroll(scroll);
            PointWebMercator p = mvParameters.getValue().pointAt(mousePoint2D.getX(), mousePoint2D.getY());
            int zoomLvl = Math2.clamp(8, mvParameters.getValue().zoom(), 19);
            mvParameters.setValue(new MapViewParameters(zoomLvl, p.xAtZoomLevel(zoomLvl), p.yAtZoomLevel(zoomLvl)));
        });
    }
    private void moveCursor(){
//        MouseEvent drag = new MouseEvent();
//        pane.setOnMouseClicked(hold ->  {
//            point2DObjectProperty.setValue(hold.getX() ,hold.getY()));
//            drag.
//        };
//        while(drag.isStillSincePress()){
//
//        }
    }

    private void clickToAddWaypoint(){
        pane.setOnMouseClicked(click -> waypointsManager.addWaypoint(mousePoint2D.getX(), mousePoint2D.getY()));
        System.out.println("click" + mousePoint2D.getX() + " ffff "+ mousePoint2D.getY());
    }

    /**
     * Getter for the pane
     * @return the pane
     */
    public Pane pane(){
        return pane;
    }
}
