package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.List;

public final class RouteComputer {

    private Graph graph;
//    private CostFunction costFunction;

    public RouteComputer (Graph graph/*CostFunction costFunction*/){
        this.graph = graph;
       // this.costFunction = costFunction;
    }


    Route bestRouteBetween(int startNodeId, int endNodeId){

        Preconditions.checkArgument(startNodeId != endNodeId);
        List<Integer> exploring = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        float[] distance = new float[graph.nodeCount()];
        int[] predecesor = new int[graph.nodeCount()];

        for (int i = 0; i < distance.length; i++) {
            distance[i] = Float.POSITIVE_INFINITY;
            predecesor[i] = 0;
        }

        distance[startNodeId] = 0;
        exploring.add(startNodeId);
        int N = 0;
        int nPrime;
        float d;
        int edgeID;


        while(!exploring.isEmpty()){

            for (int i = 0; i < exploring.size(); i++) {
                if (distance[exploring.get(i)] < distance[exploring.get(i+1)] ){
                    N = exploring.get(i);
                }
            }

            if (N == endNodeId){
                break;
            }

            for (int i = 0; i < graph.nodeOutDegree(N); i++) {
                edgeID = graph.nodeOutEdgeId(N, i);
                nPrime = graph.edgeTargetNodeId(edgeID);
                d = distance[N] +  (float)graph.edgeLength(edgeID);

                if( d < nPrime ){
                    distance[nPrime] = d;
                    predecesor[nPrime] = N;
                    exploring.add(nPrime);
                }
            }
        }


        do{
            edgeList.add( Edge.of(graph, graph. , predecesor[endNodeId], endNodeId);

        }while ();


        return new SingleRoute(edgeList);
    }


}