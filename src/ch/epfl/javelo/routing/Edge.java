package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeID, int toNodeID, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

/*
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){


        return new Edge(fromNodeId, toNodeId, , , graph.edgeLength(edgeId), graph.edgeProfile(edgeId))
    }

    public double positionClosestTo(PointCh point){

    }


    public PointCh pointAt(double position){

    }


    public double elevationAt(double position){

    }
*/

}
