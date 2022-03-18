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
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    public PointCh pointAt(double position){
        return new PointCh(fromPoint.e() + ((position/length) * (toPoint.e()-fromPoint.e()) ), fromPoint.n() + ((position/length) * (toPoint.n()-fromPoint.n())));
    }


    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }

    /**
     * Getter for the NodeId of the beginning node
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
