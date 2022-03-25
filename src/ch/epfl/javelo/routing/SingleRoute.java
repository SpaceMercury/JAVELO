package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
        /**double segmentStart = 0, segmentEnd = 0;
        for (int i = 0; i < edges.size() - 1 ; i++) {
            segmentEnd += edges.get(i).length();
            if(position >= segmentStart && position <= segmentEnd && i >= 1){
                return i - 1;
            }
            segmentStart = segmentEnd;
        }*/

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
     * Getter for the edges of the list
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
    //TODO: Ask what to do if position is bigger than the segment length.
    public PointCh pointAt(double position) {
//        int edgeNumber = 0;
//        while(position - edges.get(edgeNumber).length() >= 0){
//            position -= edges.get(edgeNumber).length();
//            edgeNumber++;
//        }
//        return edges.get(edgeNumber).pointAt(position);

        double[] lengthList= new double[edges.size()+1];
        int edge = 0;
        double totalLength = 0;
        lengthList[0] = totalLength;

        for (int i = 1; i <= edges.size(); ++i ){
            totalLength = totalLength + edges.get(i-1).length();
            lengthList[i] = (totalLength);
        }
        int searchAlg = Arrays.binarySearch(lengthList, position);

        if(searchAlg <= 0) {
            edge = Math.abs(searchAlg) - 2;
           return edges.get(edge).pointAt(position);
        }
        else{
           return edges.get(searchAlg).pointAt(position);
        }
    }

    /**
     *
     * @param position desired position
     * @return the altitude at the given position (can be NaN if the edge has no profile)
     */
    @Override
    public double elevationAt(double position) {

        double[] lengthList= new double[edges.size()+1];
        int edge = 0;
        double totalLength = 0;
        lengthList[0] = totalLength;

        for (int i = 1; i <= edges.size(); ++i ){
            totalLength = totalLength + edges.get(i-1).length();
            lengthList[i] = (totalLength);
        }
        int searchAlg = Arrays.binarySearch(lengthList, position);
        if(searchAlg <= 0) {
            edge = Math.abs(searchAlg) - 2;
            return edges.get(edge).elevationAt(position);
        }
        else{
            return edges.get(searchAlg).elevationAt(0);
        }
    }

    /**
     *
     * @param position the desired position
     * @return the index of the node that is closest to that position
     */
    @Override
    public int nodeClosestTo(double position) {

        double[] lengthList= new double[edges.size()+1];
        double totalLength = 0;
        lengthList[0] = totalLength;

        for (int i = 1; i <= edges.size(); ++i ){
            totalLength = totalLength + edges.get(i-1).length();
            lengthList[i] = (totalLength);
        }
        int searchAlg = Arrays.binarySearch(lengthList, position);
        if (searchAlg >= 0){
            return searchAlg;
        }
        // Find which node the position is closest to
        else{
            double node1 = lengthList[Math.abs(searchAlg)-2];
            double node2 = lengthList[Math.abs(searchAlg)-1];

             if ((node1+node2)/2 >= position){
                 return Math.abs(searchAlg)-2;
             }
             else{
                 return Math.abs(searchAlg)-1;
             }
        }
    }

    /**
     *
     * @param point given PointCh point
     * @return the closes point on the itinerary to the given point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {

        PointCh closestPoint = null;
        double closestDistance = 0.;
        for (int i=0; i < edges.size()-1; ++i) {
            closestDistance = edges.get(i).positionClosestTo(point);
            if (edges.get(i+1).positionClosestTo(point) < edges.get(i).positionClosestTo(point)){
                closestPoint = edges.get(i+1).pointAt(closestDistance);
            }
        }
        return new RoutePoint(closestPoint, closestDistance, closestPoint.distanceTo(point) );
    }
}
