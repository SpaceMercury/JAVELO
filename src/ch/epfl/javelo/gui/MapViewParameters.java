package ch.epfl.javelo.gui;

import java.awt.geom.Point2D;

public record MapViewParameters(int zoom, int x, int y){


    public Point2D topLeftPixel(){

        return new Point2D( 0, 0);
    }
}
