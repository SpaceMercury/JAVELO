package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
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

    private List<Edge> edges;



    /**
     * SingleRoute constructor, builds a SingleRoute with all the edges given as a parameter
     * @param edges List of edges
     */
    public SingleRoute(List<Edge> edges){

        Preconditions.checkArgument(edges.size() > 0);
        this.edges = List.copyOf(edges);
    }


    /**
     * Function always returns 0 for SingleRoute
     * @param position desired double position
     * @return the index of the segment of an itinerary at position
     */
    @Override
    public int indexOfSegmentAt(double position) {
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
     * @return all the edges of the segment
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * @return All the points (nodes) located on the edges of the segment
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
     * Gives the point at the chosen position in respect to the start of the itinerary
     * @param position desired position
     * @return the point at the given position of the itinerary
     */
    @Override
    public PointCh pointAt(double position) {

        double[] lengthList= new double[edges.size()+1];
        int edge = 0;
        double totalLength = 0;
        lengthList[0] = totalLength;

        for (int i = 1; i <= edges.size(); ++i ){
            totalLength = totalLength + edges.get(i-1).length();
            lengthList[i] = (totalLength);
        }
        int searchAlg = Arrays.binarySearch(lengthList, position);

        if(searchAlg == -1){
            return edges.get(0).fromPoint();
        }
        if(Math.abs(searchAlg)-2 >= edges.size()){
            return edges.get(edges.size()-1).toPoint();
        }
        if(searchAlg <= 0) {
            if(searchAlg <= -2) {
                edge = Math.abs(searchAlg) - 2;
            }
           return edges.get(edge).pointAt(position - lengthList[edge]);
        }
        else{
           return edges.get(searchAlg-1).pointAt(edges.get(searchAlg-1).length());
        }
    }

    /**
     * Gives the altitude at the chosen position in respect to the start of the itinerary
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
        if(position > lengthList[edges.size()]){
            Edge lastEdge = edges.get(edges.size()-1);
            return lastEdge.elevationAt(lastEdge.length());
        }

        int searchAlg = Arrays.binarySearch(lengthList, position);
        if(searchAlg == -1){
            return edges.get(0).elevationAt(0);
        }
        if(searchAlg < 0) {
            if(searchAlg <= -2) {
                edge = Math.abs(searchAlg) - 2;
            }
            return edges.get(edge).elevationAt(position - lengthList[edge]);
        }
        if(searchAlg >= edges.size()){
            return edges.get(edges.size()-1).elevationAt(edges.get(edges.size()-1).length());
        }
        else{
            return edges.get(searchAlg).elevationAt(0);
        }

    }

    /**
     * Gives the index of the closest node at the chosen position in respect to the start of the itinerary
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

        if(searchAlg == -1){
            return 0;
        }
        if(Math.abs(searchAlg) >= edges.size()+2){
            return edges.get(edges.size()-1).toNodeId();
        }
        if (searchAlg == edges.size()){
            return edges.get(searchAlg-1).toNodeId();
        }
        if (searchAlg >= 0){
            return edges.get(searchAlg).fromNodeId();
        }

        // Find which node the position is closest to
        else{
            double node1 = lengthList[Math.abs(searchAlg)-2];
            double node2 = lengthList[Math.abs(searchAlg)-1];

             if (Math.abs(node1-position) <= Math.abs(node2-position)){
                 return edges.get(Math.abs(searchAlg)-2).fromNodeId();
             }
             else{
                 return edges.get(Math.abs(searchAlg)-2).toNodeId();
             }
        }
    }

    /**
     * Gives the closest point on the itinerary to the PointCh chosen
     * @param point given PointCh point
     * @return the closes point on the itinerary to the given point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {

        double[] lengthList= new double[edges.size()+1];
        double totalLength = 0;
        lengthList[0] = totalLength;

        for (int i = 1; i <= edges.size(); ++i ){
            totalLength = totalLength + edges.get(i-1).length();
            lengthList[i] = (totalLength);
        }
        RoutePoint smallestRoutePoint = RoutePoint.NONE;

        double position;
        double edgePos;

        for (Edge edge : edges) {
            position = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            edgePos = position + lengthList[edges.indexOf(edge)];
            smallestRoutePoint = smallestRoutePoint.min(edge.pointAt(position), edgePos, point.distanceTo(edge.pointAt(position)));
        }

        return smallestRoutePoint;


    }
}
