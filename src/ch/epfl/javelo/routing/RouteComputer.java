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

    /**
     * Private function used in bestRouteBetween, calculates the fastest path taken to a destination
     * @param startNodeId the ID of the first node
     * @param endNodeId the ID of the last node
     * @param predecesor the list of predecessors to find the way
     * @return the SingleRoute that is the best Route between 2 nodes
     */
    private Route calculateWay(int startNodeId, int endNodeId, int[] predecesor){

        List<Integer> reverseList = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        reverseList.add(endNodeId);
        int P = endNodeId;

        do{
            P = predecesor[P];
            reverseList.add(P);
        }while(P != startNodeId);

        for (int i = reverseList.size()-1; i > 0 ; i--) {

            int nodeNum = graph.nodeOutDegree(reverseList.get(i));
            int edgeId = 0;

            for (int j = 0; j < nodeNum ; j++) {
                if(graph.edgeTargetNodeId(graph.nodeOutEdgeId(reverseList.get(i),j)) == reverseList.get(i-1)){
                    edgeId = graph.nodeOutEdgeId(reverseList.get(i),j);
                }
            }
            edgeList.add(Edge.of(graph, edgeId, reverseList.get(i), reverseList.get(i-1)));
        }
        return new SingleRoute(edgeList);
    }


    /**
     *
     * @param startNodeId Id of the startNode of the Route
     * @param endNodeId Id of the endNode of the Route
     * @return A SingleRoute
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){

        Preconditions.checkArgument(startNodeId != endNodeId);

        List<Integer> exploring = new ArrayList<>();
        float[] distance = new float[graph.nodeCount()];
        int[] predecesor = new int[graph.nodeCount()];


        // Dijsktra Algorithm to find the shortest path
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
                return calculateWay(startNodeId, endNodeId, predecesor);
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
        return null;
    }


}