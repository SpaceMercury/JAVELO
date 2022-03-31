package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.util.List;

public final class MultiRoute implements Route{
    private final List<Route> segments;
    public MultiRoute(List<Route> segments){
        this.segments = segments;
    }
    private double positionCorrection(double position){
        double length = 0;
        for (int i = 0; i < segments.size(); i++) {
            length += segments.get(i).length();
        }
        return Math2.clamp(0, position, length);
    }
    /**
     * @param position
     * @return
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position = positionCorrection(position);
        double segmentStart = 0, segmentEnd = 0;
         for (int i = 0; i < segments.size() ; i++) {
         segmentEnd += segments.get(i).length();
         if(position >= segmentStart && position <= segmentEnd){
         return i;
         }
         segmentStart = segmentEnd;
         }
    }

    /**
     * @return
     */
    @Override
    public double length() {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public List<Edge> edges() {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<PointCh> points() {
        return null;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public PointCh pointAt(double position) {
        position = positionCorrection(position);
        return null;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public double elevationAt(double position) {
        position = positionCorrection(position);
        return 0;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public int nodeClosestTo(double position) {
        position = positionCorrection(position);
        return 0;
    }

    /**
     * @param point
     * @return
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
}
