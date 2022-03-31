package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.binarySearch;

public final class MultiRoute implements Route{
    private final List<Route> segments;
    private final double[] segmentLengths;
    public MultiRoute(List<Route> segments){
        this.segments = List.copyOf(segments);
        this.segmentLengths = new double[segments.size() + 1];
        for(int i = 0; i < segments.size(); i++){
            segmentLengths[i + 1] = segments.get(i).length();
        }
    }
    private double positionCorrection(double position){
        double length = 0;
        for (int i = 0; i < segments.size(); i++) { //check if it is < or <=
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
         return segments.size() - 1;
    }

    /**
     * @return
     */
    @Override
    public double length() {
        double length = 0;
        for(int i = 0; i < segments.size(); i++){
            length += segments.get(i).length();
        }
        return length;
    }

    /**
     * @return
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            edges.addAll(segments.get(i).edges());
        }
        return edges;
    }

    /**
     * @return
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> pointCH = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            pointCH.addAll(segments.get(i).points());
        }
        return pointCH;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public PointCh pointAt(double position) {
        position = positionCorrection(position);
        int id = binaryIdResearch(position);
        return segments.get(id).pointAt(position - segments.get(id).length());
    }

    /**
     * @param position
     * @return
     */
    @Override
    public double elevationAt(double position) {
        position = positionCorrection(position);
        int id = binaryIdResearch(position);
        return segments.get(id).elevationAt(position - segments.get(id).length());
    }

    /**
     * @param position
     * @return
     */
    @Override
    public int nodeClosestTo(double position) {
        position = positionCorrection(position);
        int id = binaryIdResearch(position);
        return segments.get(id).nodeClosestTo(position - segments.get(id).length());
    }

    /**
     * @param point
     * @return
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        for (int i = 0; i < segments.size(); i++) {
            closestPoint = closestPoint.min(segments.get(i).pointClosestTo(point));
        }
        return closestPoint;
    }

    private int binaryIdResearch(double position){
        int id = binarySearch(segmentLengths, position);
        if(id < 0){
            id = -(id + 2);
        }
        if(id == segmentLengths.length - 1){
            id--;
        }
        return id;
    }

}
