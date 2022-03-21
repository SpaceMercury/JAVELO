package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;


/**
 * @author fuentes
 */
public final class SingleRoute implements Route{

    List<Edge> edges;

    public SingleRoute(List<Edge> edges){

        Preconditions.checkArgument(edges.size() > 0);
        this.edges = edges;

    }


    /**
     * @param position desired double position
     * @return the index of the segment of an itinerary at position
     */
    @Override
    public int indexOfSegmentAt(double position) {

        for (int i = 0; i < edges.size() - 1 ; i++) {
            if( position >= edges.get(1).length() && position <= edges.get(i+1).length()){
                return i;
            }
        }
        return 0;
    }

    /**
     * @return the length of the itinerary in meters
     */
    @Override
    public double length() {
        double tempLength = 0;
        for (Edge edge : edges) {
            tempLength = tempLength + edge.length();
        }
        return tempLength;
    }

    /**
     *
     * @return all the edges of the itinerary
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * @return all of the points (nodes) located on the edges of the itinerary
     */
    @Override
    public List<PointCh> points() {

        List<PointCh> pointList = new ArrayList<PointCh>();

        for (Edge edge : edges) {
            pointList.add(edge.fromPoint());
        }
        pointList.add(edges.get(edges().size()-1).toPoint());
        return pointList;
    }

    /**
     *
     * @param position desired position
     * @return the point at the given position of the itinerary
     */
    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    /**
     *
     * @param position desired position
     * @return the altitude at the given position (can be NaN if the edge has no profile)
     */
    @Override
    public double elevationAt(double position) {
        return 0;
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    /**
     *
     * @param point given PointCh point
     * @return the closes point on the itinerary to the given point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {

    }
}
