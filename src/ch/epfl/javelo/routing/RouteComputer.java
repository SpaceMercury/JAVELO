package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fuentes
 */
public final class RouteComputer {


    private Graph graph;
    private CostFunction costFunction;

    /**
     * Constructor of RouteComputer
     * @param graph the graph in which we want to find the Route
     * @param costFunction the costFunction to determine the best route
     */
    public RouteComputer (Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Private function used in bestRouteBetween, calculates the fastest path taken to a destination
     * @param startNodeId the ID of the first node
     * @param endNodeId the ID of the last node
     * @param predecessor the list of predecessors to find the way
     * @return the SingleRoute that is the best Route between 2 nodes
     */
    private Route calculateWay(int startNodeId, int endNodeId, int[] predecessor){

        List<Integer> reverseList = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        reverseList.add(endNodeId);
        int P = endNodeId;

        do{
            P = predecessor[P];
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
     * Function that goes through all the nodes of the graph and finds the quickest way to the endNode from the startNode
     * @param startNodeId Id of the startNode of the Route
     * @param endNodeId Id of the endNode of the Route
     * @return A SingleRoute if a route is found, Null if no route is found
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){

        // Throw IllegalArgumentException if the startNode is the same as the endNode
        Preconditions.checkArgument(startNodeId != endNodeId);

        List<Integer> exploring = new ArrayList<>();
        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];


        // All distances set to INFINITY, and all predecessors set to an arbitrary value (0)
        for (int i = 0; i < distance.length; i++) {
            distance[i] = Float.POSITIVE_INFINITY;
            predecessor[i] = 0;
        }

        distance[startNodeId] = 0;
        //List of nodes we are exploring
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
            // Removes the node we are about to explore from the list
            N = exploring.remove(tempIndex);

            // If the node we found is the same as the endNode, we have sucessfully found the quickest route
            if (N == endNodeId){
                // Calls the private method calculateWay
                return calculateWay(startNodeId, endNodeId, predecessor);
            }

            for (int i = 0; i < graph.nodeOutDegree(N); i++) {
                edgeID = graph.nodeOutEdgeId(N, i);
                nPrime = graph.edgeTargetNodeId(edgeID);
                d = distance[N] +  (float)costFunction.costFactor(N, edgeID)*(float)graph.edgeLength(edgeID);

                if( d < distance[nPrime] ){
                    distance[nPrime] = d;
                    predecessor[nPrime] = N;
                    exploring.add(nPrime);
                }
            }
        }
        return null;
    }


}