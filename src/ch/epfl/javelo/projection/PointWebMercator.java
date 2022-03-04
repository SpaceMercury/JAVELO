package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

public record PointWebMercator(double x, double y) {

    /**
     * checks that the values are contained within the boundaries 0 and 1
     * @param x x coordinate
     * @param y y coordinate
     */
    public PointWebMercator {
        Preconditions.checkArgument((x >= 0 && x <= 1) && (y >= 0 && y <= 1));
    }

    /**
     * Using the variable zoomTotal, it will be easier to change the constant factor of 8 for the card if that needs to be the case
     * Otherwise this functions gives back
     * @param zoomLevel Level at which the zoom is currently
     * @param x zoomed x coordinate
     * @param y zoomed y coordinate
     * @return (x,y) dezoomed to 'level 0'
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        int zoomTotal = 8 + zoomLevel;
        return new PointWebMercator(Math.scalb(x, -zoomTotal), Math.scalb(y, -zoomTotal));
    }

    /**
     * Creates a PointWebMercator instance off of a pointCh
     * @param pointCh The pointCh that is to be turned into a PointWebMercator
     * @return The corresponding PointWebMercator
     */
    public static PointWebMercator ofPointCh(PointCh pointCh){
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * Function scales x coordinate to corresponding zoom level
     * @param zoomLevel Zoom Level that we want
     * @return Upscaled x coordinate
     */
    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, zoomLevel);
    }

    /**
     * Function scales y coordinate to corresponding zoom level
     * @param zoomLevel zoom level that we want
     * @return Upscaled y coordinate
     */
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, zoomLevel);
    }

    /**
     * Gives longitude of the PointWebMercator
     * @return longitude of PointWebMercator
     */
    public double lon(){
        return 2*Math.PI*x - Math.PI;
    }

    /**
     * Gives latitude of the PointWebMercator
     * @return latitude of PointWebMercator
     */
    public double lat(){
        return Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));
    }

    /**
     * Converts a PointWebMercator to a PointCh
     * @return Converted PointCh
     */
    public PointCh toPointCh(){
        if(SwissBounds.containsEN(Ch1903.e(WebMercator.lon(x), WebMercator.lat(y)), Ch1903.n(WebMercator.lon(x), WebMercator.lat(y)))) {
            return new PointCh(Ch1903.e(WebMercator.lon(x), WebMercator.lat(y)), Ch1903.n(WebMercator.lon(x), WebMercator.lat(y)));
        }
        return null;
    }

}