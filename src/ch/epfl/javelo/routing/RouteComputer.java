package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.List;

public final class RouteComputer {

    private Graph graph;
    private CostFunction costFunction;

    public RouteComputer (Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }


    public Route bestRouteBetween(int startNodeId, int endNodeId){

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

        int nPrime;
        float d;
        int edgeID;


        while(!exploring.isEmpty()){
            int tempIndex=0;
            int N = exploring.get(0);
            for (int i = 0; i < exploring.size(); i++) {
                if (distance[N] > distance[exploring.get(i)] ){
                    N = exploring.get(i);
                    tempIndex = i;
                }
            }
            N = exploring.remove(tempIndex);

            if (N == endNodeId){
                break;
            }

            for (int i = 0; i < graph.nodeOutDegree(N); i++) {
                edgeID = graph.nodeOutEdgeId(N, i);
                nPrime = graph.edgeTargetNodeId(edgeID);
                d = distance[N] +  (float)costFunction.costFactor(N, edgeID)*(float)graph.edgeLength(edgeID);

                if( d < distance[nPrime] ){
                    distance[nPrime] = d;
                    predecesor[nPrime] = N;
                    exploring.add(nPrime);
                }
            }
        }


        List<Integer> reverseList = new ArrayList<>();

        reverseList.add(endNodeId);

        int P = endNodeId;


       while (P != startNodeId) {
           P = predecesor[P];
           reverseList.add(P);
       }

        for (int i = 0; i < reverseList.size()-1 ; i++) {

            int nodeNum = graph.nodeOutDegree(reverseList.get(i+1));

            int edgeId = 0;
            for (int j = 0; j < nodeNum ; j++) {

                if(graph.edgeTargetNodeId(graph.nodeOutEdgeId(reverseList.get(i+1),j)) == reverseList.get(i)){
                   edgeId = graph.nodeOutEdgeId(reverseList.get(i+1),j);
               }
            }
            edgeList.add(Edge.of(graph, edgeId, reverseList.get(i+1), reverseList.get(i)));
        }



        return new SingleRoute(edgeList);
    }


}