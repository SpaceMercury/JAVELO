package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;


/**
 * @author fuentes
 */
public final class BaseMapManager {

    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private final TileManager tileManager;
    private static int IMG_SIZE = 256;
    private static int MIN_ZOOM = 8;
    private static int MAX_ZOOM = 19;
    private final WaypointsManager waypointsManager;
    private ObjectProperty<Point2D> mousePoint2D;
    private final ObjectProperty<MapViewParameters> mvParameters;

    /**
     * Public BaseMapManager constructor
     *
     * @param tileManager       the TileManager
     * @param waypointsManager  the WaypointsManager
     * @param mapViewParameters An ObjectProperty of MapViewParameters
     */
    public BaseMapManager(TileManager tileManager,
                          WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParameters) {

        this.waypointsManager = waypointsManager;
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);
        this.tileManager = tileManager;
        mvParameters = mapViewParameters;
        //bind canvas to the pane
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        pane.setPrefSize(IMG_SIZE, IMG_SIZE);

        //Events
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
     * Method to set redrawNeeded to true and to delay the drawing process
     * So that it doesn't redraw more than 60 times per second
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Calls drawTilesOnCanvas which will draw the tiles
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        redrawOnNextPulse();
        drawTilesOnCanvas();
    }


    /**
     * Draws the Tiles on the canvas
     */
    private void drawTilesOnCanvas() {

        //Offset for the tiles on canvas
        int xOffset = (int) Math.floor(mvParameters.getValue().x() % IMG_SIZE);
        int yOffset = (int) Math.floor(mvParameters.getValue().y() % IMG_SIZE);

        //calculate how many tiles fit lengthwise
        int lengthNum = xOffset == 0
                ? Math2.ceilDiv((int) canvas.getWidth(), IMG_SIZE)
                : Math2.ceilDiv((int) canvas.getWidth(), IMG_SIZE) + 1;

        //calculate how many tiles fit heightwise
        int heightNum = yOffset == 0
                ? Math2.ceilDiv((int) canvas.getHeight(), IMG_SIZE)
                : Math2.ceilDiv((int) canvas.getHeight(), IMG_SIZE) + 1;

        //Loop to draw the tiles
        for (int i = 0; i < lengthNum; i++) {
            for (int j = 0; j < heightNum; j++) {

                int tileX = Math.floorDiv((int) mvParameters.getValue().x(), IMG_SIZE) + i;
                int tileY = Math.floorDiv((int) mvParameters.getValue().y(), IMG_SIZE) + j;
                Image tileImage = getTileImage(mvParameters.getValue(), tileX, tileY);
                canvas.getGraphicsContext2D()
                        .drawImage(tileImage, 256 * i - xOffset, 256 * j - yOffset);

            }
        }
    }

    /**
     * @param mapView The mapview
     * @param tileX   the X coordinate of the tile
     * @param tileY   the Y coordinate of the tile
     * @return the Image of that tile
     */
    private Image getTileImage(MapViewParameters mapView, int tileX, int tileY) {
        TileManager.TileId id = new TileManager.TileId(mapView.zoom(), tileX, tileY);
        Image tileImage = null;
        try {
            tileImage = tileManager.imageForTileAt(id);
        } catch (IOException e) {
        }
        return tileImage;
    }


    /**
     * Event manager for zoom
     */
    private void scrollZoom() {
        canvas.setOnScroll(scroll -> {
            PointWebMercator p = PointWebMercator.of(mvParameters.getValue().zoom(),
                    mvParameters.getValue().x() + scroll.getX(),
                    mvParameters.getValue().y() + scroll.getY());

            int zoomLvl = Math2.clamp(MIN_ZOOM, mvParameters.get().zoom() + (int) Math.signum(scroll.getDeltaY()), MAX_ZOOM);
            mvParameters.set(new MapViewParameters(zoomLvl, p.xAtZoomLevel(zoomLvl) - scroll.getX(), p.yAtZoomLevel(zoomLvl) - scroll.getY()));
        });
    }


    /**
     * Event manager for moving the by dragging
     */
    private void moveCursor() {

        mousePoint2D = new SimpleObjectProperty<>();
        pane.setOnMousePressed(press -> {
            mousePoint2D.setValue(new Point2D(press.getX(), press.getY()));
        });

        pane.setOnMouseDragged(hold -> {
            MapViewParameters mValue = mvParameters.getValue();
            Point2D currentPoint = new Point2D(hold.getSceneX(), hold.getSceneY());
            mvParameters.setValue(mValue.withMinXY(mValue.x() + mousePoint2D.getValue().getX() - currentPoint.getX(),
                    mValue.y() + mousePoint2D.getValue().getY() - currentPoint.getY()));
            mousePoint2D.setValue(currentPoint);

        });

    }

    /**
     * Event manager for adding Waypoints
     */
    private void clickToAddWaypoint() {

        pane.setOnMousePressed(press -> {
            mousePoint2D.setValue(new Point2D(press.getX(), press.getY()));
        });
        pane.setOnMouseClicked(click -> {
            waypointsManager.addWaypoint(mousePoint2D.getValue().getX(), mousePoint2D.getValue().getY());
        });
    }

    /**
     * Getter for the pane
     *
     * @return the pane
     */
    public Pane pane() {
        return pane;
    }
}
