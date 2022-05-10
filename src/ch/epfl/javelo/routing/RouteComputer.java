package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

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
        int p = endNodeId;

        do{
            p = predecessor[p];
            reverseList.add(p);
        }while(p != startNodeId);

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


        //Nested WeightedNode record
        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }

        }

        // Throw IllegalArgumentException if the startNode is the same as the endNode
        Preconditions.checkArgument(startNodeId != endNodeId);

        //Creation of the arrays we will use in A-star algorithm
        PriorityQueue<WeightedNode> exploration = new PriorityQueue<>();
        float[] distance = new float[graph.nodeCount()+1];
        int[] predecessor = new int[graph.nodeCount()+1];

        // All distances set to INFINITY, and all predecessors set to an arbitrary value (0)
        for (int i = 0; i < distance.length; i++) {
            distance[i] = Float.POSITIVE_INFINITY;
        }

        distance[startNodeId] = 0;
        //Adding the first WeightedNode
        exploration.add(new WeightedNode(startNodeId,0));

        //Temporary constants that we use later for the algorithm
        int nPrime;
        float d;
        int edgeID;

        while(!exploration.isEmpty()){

            //Remove method from PriorityQueue removes the node with the smallest distance
             int n = exploration.remove().nodeId;

            // If the node we found is the same as the endNode, we have sucessfully found the quickest route
            if (n == endNodeId){
                // Calls the private method calculateWay
                return calculateWay(startNodeId, endNodeId, predecessor);
            }

            for (int i = 0; i < graph.nodeOutDegree(n); i++) {
                edgeID = graph.nodeOutEdgeId(n, i);
                nPrime = graph.edgeTargetNodeId(edgeID);
                d = distance[n] + (float)costFunction.costFactor(n, edgeID)*(float)graph.edgeLength(edgeID);

                if( d < distance[nPrime] && d != Float.NEGATIVE_INFINITY ){
                    distance[nPrime] = d;
                    predecessor[nPrime] = n;
                    exploration.add(new WeightedNode(nPrime, distance[nPrime]));
                }
            }
            //Set the distance of the selected node to -inf, so we don't explore nodes we've already explored
            distance[n] = Float.NEGATIVE_INFINITY;
        }
        //If no itinerary exists return null
        return null;
    }


}