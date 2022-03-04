package ch.epfl.javelo.projection;

public class PointWebMercator {

    private double x;
    private double y;


    /**
     * Default constructor of PointWebMercator which needs to be implemented, as we need to implement
     * a non default constructor, which overrides default 'default' constructor.
     */
    public PointWebMercator(){
        x = 0.0;
        y = 0.0;
    }

    public PointWebMercator(double x, double y) {
        this.x = x;
        this.y = y;
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
    double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, zoomLevel);
    }

    /**
     * Function scales y coordinate to corresponding zoom level
     * @param zoomLevel zoom level that we want
     * @return Upscaled y coordinate
     */
    double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, zoomLevel);
    }

    /**
     * Gives longitude of the PointWebMercator
     * @return longitude of PointWebMercator
     */
    double lon(){
        return 2*Math.PI*x - Math.PI;
    }

    /**
     * Gives latitude of the PointWebMercator
     * @return latitude of PointWebMercator
     */
    double lat(){
        return Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));
    }

    /**
     * Converts a PointWebMercator to a PointCh
     * @return Converted PointCh
     */
    PointCh toPointCh(){
        if(SwissBounds.containsEN(x, y)) {
            return new PointCh(x, y);
        }
        return null;
    }

}