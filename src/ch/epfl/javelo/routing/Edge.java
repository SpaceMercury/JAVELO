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
     * @param graph The graph, from which the attributes are to be taken
     * @param edgeId The id of the edge of the graph, of which the attributes are to be taken
     * @param fromNodeId The id of the start node
     * @param toNodeId The id of the end node
     * @return a new Edge with the chosen attributes
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * Gives the position on the edge, that is closest to the given point
     * @param point the given point
     * @return the position on the edge that is closest to the point
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * Gives the point located at the position on the edge
     * @param position the position in meters
     * @return the point on the edge at said position
     */
    public PointCh pointAt(double position){
        return new PointCh(fromPoint.e() + ((position/length) * (toPoint.e()-fromPoint.e()) ), fromPoint.n() + ((position/length) * (toPoint.n()-fromPoint.n())));
    }

    /**
     * Gives the elevation of the edge at the specified position
     * @param position the position
     * @return the elevation in meters
     */
    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }
}
