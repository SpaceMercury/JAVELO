package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

@SuppressWarnings("ALL")
public interface Route {


    /**
     *
     * @param position
     * @return
     */
    abstract int indexOfSegmentAt(double position);

    /**
     *
     * @return
     */
    abstract double length();

    /**
     *
     * @return
     */
    abstract List<Edge> edges();

    /**
     *
     * @return
     */
    abstract List<PointCh> points();

    /**
     *
     * @param position
     * @return
     */
    abstract PointCh pointAt(double position);

    /**
     *
     * @param position
     * @return
     */
    abstract double elevationAt(double position);

    /**
     *
     * @param position
     * @return
     */
    abstract int nodeClosestTo(double position);

    /**
     *
     * @param point
     * @return
     */
    abstract RoutePoint pointClosestTo(PointCh point);


}
