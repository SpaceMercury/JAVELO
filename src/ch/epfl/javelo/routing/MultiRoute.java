package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.binarySearch;

/**
 * @author ventura
 */
public final class MultiRoute implements Route{
    private final List<Route> segments;
    private final double[] segmentLengths;

    /**
     * Constructor of Multiroute, creates a Multiroute with the edges corresponding to
     * the ones in the list segments, and assigning the length of each segment into
     * the list of segment lengths
     * @param segments the list of segments to be assigned to the new route
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        this.segmentLengths = new double[segments.size() + 1];
        for(int i = 0; i < segments.size(); i++){
            segmentLengths[i + 1] = segments.get(i).length();
        }
    }
    /**
     * Gives the index of the segment at the desired position
     * @param position the chosen position
     * @return the index of the segment at that position
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
     * @return the length of the route in meters
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
     * Getter for the edges of the itinerary
     * @return all the edges on the itinerary
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
     * @return a list of all the points located on the edges of the subsegments of the route
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
     * Gives the point at the chosen position in respect to the start of the itinerary
     * @param position chosen position
     * @return point at the specified position
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
     * Gives the altitude at the chosen position in respect to the start of the itinerary
     * @param position the chosen position
     * @return the altitude at the position
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
     * Gives the index of the closest node at the chosen position in respect to the start of the itinerary
     * @param position position at which the node shall be searched for
     * @return returns index of the closest node to the position
     */
    @Override
    public int nodeClosestTo(double position) {
        double length = 0;
        int count = 0, node = 0;
        while (Math2.clamp(0, position, this.length()) - length > 0) {
            if (Math2.clamp(0, position, this.length()) - length <= segments.get(count).length()) {
                node = segments.get(count).nodeClosestTo((Math2.clamp(0, position, this.length()) - length) + 1);
            } else {
                node = segments.get(count).nodeClosestTo(segments.get(count).length()) ;
            }
            length += segments.get(count).length();
            count++;
        }
        return node;
    }

    /**
     * Gives the closest point on the itinerary to the PointCh chosen
     * @param point PointCh to which the closest point shall be found
     * @return closest point on the itinerary to the PointCh
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint PointCH = RoutePoint.NONE;
        double sum = 0;
        for(Route r : segments){
            PointCH= PointCH.min(r.pointClosestTo(point).withPositionShiftedBy(sum));
            sum += r.length();
        }
        return PointCH;
    }
}
