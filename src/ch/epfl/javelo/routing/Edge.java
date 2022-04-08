package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * @author ventura
 * @author fuentes
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {


    /**
     * Static constructor of edge, that makes fromNodeId and toNodeId the given ones
     * and affects the corresponding attributes of the edge with id edgeId from graph to the rest
     * of the attributes
     * @param graph
     * @param edgeId
     * @param fromNodeId
     * @param toNodeId
     * @return a new Edge
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     *
     * @param point
     * @return
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     *
     * @param position
     * @return
     */
    public PointCh pointAt(double position){
        return new PointCh(fromPoint.e() + ((position/length) * (toPoint.e()-fromPoint.e()) ), fromPoint.n() + ((position/length) * (toPoint.n()-fromPoint.n())));
    }

    /**
     *
     * @param position
     * @return
     */
    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }
}
