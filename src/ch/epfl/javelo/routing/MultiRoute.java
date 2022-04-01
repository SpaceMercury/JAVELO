package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.binarySearch;

public final class MultiRoute implements Route{
    private final List<Route> segments;
    private final double[] segmentLengths;
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        this.segmentLengths = new double[segments.size() + 1];
        for(int i = 0; i < segments.size(); i++){
            segmentLengths[i + 1] = segments.get(i).length();
        }
    }
    /**
     * @param position
     * @return
     */
    @Override
    public int indexOfSegmentAt(double position) {
        double length = 0;
        int count = 0, id = 0;
        while ( Math2.clamp(0, position, this.length()) - length > 0){
            if ( Math2.clamp(0, position, this.length()) - length <= segments.get(count).length()){
                id += segments.get(count).indexOfSegmentAt( Math2.clamp(0, position, this.length()) - length) ;
            } else{
                id += segments.get(count).indexOfSegmentAt(segments.get(count).length()) + 1;
            }
            length += segments.get(count).length();
            count++;
        }
        return id;
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
            if(i != segments.size() - 1){
                pointCH.remove(pointCH.size() - 1);
            }
        }
        return pointCH;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public PointCh pointAt(double position) {
        position =  Math2.clamp(0, position, length());
        for(int i = 0; i < segments.size(); i++){
            if(segments.get(i).length() < position){
                position -= segments.get(i).length();
            } else{
                return segments.get(i).pointAt(position);
            }
        }
        return null;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public double elevationAt(double position) {
        position =  Math2.clamp(0, position, length());
        for(int i = 0; i < segments.size(); i++){
            if(segments.get(i).length() < position){
                position -= segments.get(i).length();
            } else{
                return segments.get(i).elevationAt(position);
            }
        }
        return 0;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public int nodeClosestTo(double position) {
        position =  Math2.clamp(0, position, length());
        for(int i = 0; i < segments.size(); i++){
            if(segments.get(i).length() < position){
                position -= segments.get(i).length();
            } else{
                return segments.get(i).nodeClosestTo(position);
            }
        }
        return 0;
    }

    /**
     * @param point
     * @return
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        for (Route segment : segments) {
            closestPoint = closestPoint.min(segment.pointClosestTo(point));
        }
        return closestPoint;
    }
}
