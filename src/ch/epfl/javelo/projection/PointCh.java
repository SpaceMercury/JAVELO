package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * @author vince
 */
public record PointCh(double e, double n) {

    /**
     * Constructor return IllegalArgumentException if the coordinates are not
     * contained within the swiss bounds
     * @param e displacement on the horizontal axis with Bern as the origin  (equivalent of x coordinate of point)
     * @param n displacement on the vertical axis with Bern as the origin (equivalent of y coordinate of point)
     */
    public PointCh{
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Gives squared distance of a point to another point
     * @param that the point in relation to which the squared distance is measured
     * @return the square of the distance to that point in meters
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e - this.e, that.n - this.n);
    }

    /**
     * Gives distance between this point and point that
     * @param that the point in respect to which the distance is to be measured
     * @return the distance in meters
     */
    public double distanceTo(PointCh that) {
        return Math2.norm(that.e - this.e, that.n - this.n);
    }

    /**
     * Gives WGS84 longitude of point in radians from PointCh coordinates
     * @return the longitude of the point in the WGS84 system in radians
     */
    public double lon() {
        return Ch1903.lon(this.e, this.n);
    }

    /**
     * Gives WGS84 latitude of point in radians from PointCh coordinates
     * @return the latitude of the point in the WGS84 system in radians
     */
    public double lat() {
        return Ch1903.lat(this.e, this.n);
    }

}