package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    
    public static RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY );


    /**
     * Function that compares two RoutePoints and outputs the one that's closest to the reference
     * @param that the other RoutePoint
     * @return this Routepoint if the distance to the reference of the other routepoint is bigger
     */
    public RoutePoint min(RoutePoint that) {

        if(distanceToReference <= that.distanceToReference){
            return this;
        }
        else{
            return that;
        }
    }


    /**
     * Function that compares the distance between a RoutePoint and a point with a position and reference
     * @param thatPoint PointCh coordinate of that
     * @param thatPosition position of that
     * @param thatDistanceToReference distance to the reference
     * @return RoutePoint if it is closer, and a new instance of that RoutePoint if that is closer
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){

        if(distanceToReference <= thatDistanceToReference){
            return this;
        }
        else{
            return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
        }
    }



    /**
     * Function that creates a new point but shifted by a certain position
     * @param positionDifference the shift in position of the point
     * @return a point with the same parameters as this but adding the position difference
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(point, position + positionDifference, distanceToReference);
    }
}