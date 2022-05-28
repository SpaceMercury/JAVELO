package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * @author fuentes
 */
public record MapViewParameters(int zoom, double x, double y){


    /**
     * Returns the point at the top left corner of the screen
     * @return a Point2D, the type used by java to represent points
     */
    public Point2D topLeftPixel(){
        return new Point2D(x,y);
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public MapViewParameters withMinXY(double x, double y){
        return new MapViewParameters(zoom,x,y);
    }

    /**
     *
     * @param xCoord the x coordinate relative to the top left corner
     * @param yCoord the y coordinate relative to the to left corner
     * @return an instance of PointWebMercator
     */
    public PointWebMercator pointAt(double xCoord, double yCoord){
        return PointWebMercator.of(zoom, x+xCoord, y+yCoord);
    }

    /**
     * Returns the x point relative to the map shown on screen
     * @param point WebMercator point
     * @return position x relative to the upper left corner of the portion of the map shown on screen
     */
    public double viewX(PointWebMercator point){
        return point.xAtZoomLevel(zoom)-x;
    }

    /**
     * Returns the y point relative to the map shown on screen
     * @param point WebMercator point
     * @return position y relative to the upper left corner of the portion of the map shown on screen
     */
    public double viewY(PointWebMercator point){
        return point.xAtZoomLevel(zoom)-y;
    }


}
