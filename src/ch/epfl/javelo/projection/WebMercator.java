package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * @author fuentes
 */

public final class WebMercator {

    private WebMercator(){

    }


    /**
     *
     * @param lon longitude in radians
     * @return x position on the Mercator coordinate system
     */
    public static double x(double lon){

        return (0.5*Math.PI) * (Math.toDegrees(lon) + Math.PI);
    }

    /**
     *
     * @param lat latitude in radians
     * @return y on the Mercator coordinate system
     */
    public static double y(double lat){

        return (0.5*Math.PI) * (Math.PI - Math2.asinh(Math.tan(Math.toDegrees(lat))));
    }

    /**
     *
     * @param x the x coordinate on the Mercator coordinate system
     * @return in radians the longitude of that coordinate
     */
    public static double lon(double x){

        return Math.toRadians((2*Math.PI*x) - Math.PI);
    }

    /**
     *
     * @param y the y coordiante on the Mercator coordinate system
     * @return in radians the latitude of that coordinate
     */
    public static double lat(double y){

        return Math.toRadians( Math.atan(Math.sinh(Math.PI - (2*Math.PI*y))));

    }

}
