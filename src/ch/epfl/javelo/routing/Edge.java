package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeID, int toNodeID, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {


    /**
     *
     * @param graph
     * @param edgeId
     * @param fromNodeId
     * @param toNodeId
     * @return
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){

        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId) ,graph.nodePoint(toNodeId) , graph.edgeLength(edgeId), graph.edgeProfile(edgeId))
    }

    public double positionClosestTo(PointCh point){
        Math2.projectionLength();
    }


    public PointCh pointAt(double position){
        return Math2.interpolate(0,0,position);
    }


    public double elevationAt(double position){
        ElevationProfile.elevationAt(position);
    }


    /**
     * Getter for the NodeId of the beggining node
     * @return Id of the Node
     */
    public int fromNodeId() {
        return fromNodeID;
    }

    /**
     * Getter for the NodeId of the destination
     * @return Id of the Node
     */
    public int toNodeId() {
        return toNodeID;
    }
}
