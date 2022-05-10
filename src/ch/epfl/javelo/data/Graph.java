package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * @author fuentes
 * @author vince
 */

public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Constructor of the graph class
     * @param nodes graphnodes of the new Graph
     * @param sectors graphsectors of the new Graph
     * @param edges graphedges of the new Graph
     * @param attributeSets list of attribute sets of the new graph
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }


    /**
     *
     * @param basePath Location of the files from which to read
     * @return a new Graph with the attributes which have been loaded from the bin files
     * @throws IOException in the case of an I/O error, like a file not existing
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        Path nodePath = basePath.resolve("nodes.bin");
        IntBuffer nodeBuffer;
        try(FileChannel channel = FileChannel.open(nodePath)){
            nodeBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        }

        Path sectorPath = basePath.resolve("sectors.bin");
        ByteBuffer sectorBuffer;
        try(FileChannel channel = FileChannel.open(sectorPath)){
            sectorBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }

        Path edgePath = basePath.resolve("edges.bin");
        ByteBuffer edgeBuffer;
        try(FileChannel channel = FileChannel.open(edgePath)){
            edgeBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }

        Path profileIdPath = basePath.resolve("profile_ids.bin");
        IntBuffer proIdBuffer;
        try(FileChannel channel = FileChannel.open(profileIdPath)){
            proIdBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        }

        Path elevationPath = basePath.resolve("elevations.bin");
        ShortBuffer elevationBuffer;
        try(FileChannel channel = FileChannel.open(elevationPath)){
            elevationBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asShortBuffer();
        }

        Path attributePath = basePath.resolve("attributes.bin");
        LongBuffer attributeBuffer;
        try(FileChannel channel = FileChannel.open(attributePath)){
            attributeBuffer = channel.map(FileChannel.MapMode.READ_ONLY,0, channel.size()).asLongBuffer();
        }
        List<AttributeSet> attributeSet = new ArrayList<>();
        for (int i = 0; i < attributeBuffer.capacity(); i++) {
            attributeSet.add(new AttributeSet(attributeBuffer.get(i)));
        }

        return new Graph(new GraphNodes(nodeBuffer), new GraphSectors(sectorBuffer), new GraphEdges(edgeBuffer, proIdBuffer, elevationBuffer), attributeSet);
    }

    /**
     *
     * @return the total numbers of nodes contained in the graph
     */
    public int nodeCount(){
        return nodes.count();
    }

    /**
     *
     * @param nodeId the ID of the node
     * @return the point at the node with the given nodeId
     */
    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     *
     * @param nodeId Index of the node
     * @return number of edges going out of the node
     */
    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    /**
     *
     * @param nodeId Index of the node
     * @param edgeIndex i
     * @return index of i-th edge going out of the node with index nodeId
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Method that finds the closest Node to a point by comparing the square of the distances.
     * @param point in respect to which the node is looked for
     * @param searchDistance distance within which the node is to be found
     * @return the id of the node closest to said point
     */
    public int nodeClosestTo(PointCh point, double searchDistance){
        List<GraphSectors.Sector> inArea = sectors.sectorsInArea(point, searchDistance);
        int closestNodeId = -1;
        double minDistance = Math.pow(searchDistance,2);
        for(GraphSectors.Sector sect : inArea){
            for(int i = sect.startNodeId(); i < sect.endNodeId(); i++){
                PointCh contender = new PointCh(nodes.nodeE(i), nodes.nodeN(i));
                if(point.squaredDistanceTo(contender) < minDistance){
                    closestNodeId = i;
                    minDistance = point.squaredDistanceTo(contender);
                }
            }
        }
        return closestNodeId;
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return the ID of the target node
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return boolean if the edge is inverted
     */
    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return the set of attributes of the specified edge
     */
    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(this.edges.attributesIndex(edgeId));
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return length of the specified edge
     */
    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return elevation gain on the specified edge
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return the profile of an edge as a function
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if(edges.hasProfile(edgeId)){
            return Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId));
        }
        else{
            return Functions.constant(Double.NaN);
        }
    }

}

